package edu.hcmute.webpr.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import edu.hcmute.webpr.dao.IOrderDao_24133059;
import edu.hcmute.webpr.model.*;

class TestShopping_24133059 {
    private IBookService_24133059 books;
    private IOrderDao_24133059 orders;
    private CartService_24133059 carts;
    private OrderService_24133059 checkout;
    private Cart_24133059 cart;
    private Book_24133059 book;
    private User_24133059 buyer;

    @BeforeEach
    void setup() {
        books = mock(IBookService_24133059.class);
        orders = mock(IOrderDao_24133059.class);
        carts = new CartService_24133059(books);
        checkout = new OrderService_24133059(orders, carts);
        cart = new Cart_24133059();
        book = new Book_24133059();
        book.setBookId(1);
        book.setTitle("Sách tiếng Việt");
        book.setPrice(new BigDecimal("95.00"));
        book.setQuantity(30);
        when(books.findById(1)).thenReturn(book);
        buyer = new User_24133059();
        buyer.setId(2);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 21, Integer.MAX_VALUE})
    void invalidAddNeverChangesExistingQuantity(int quantity) {
        carts.addToCart(cart, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> carts.addToCart(cart, 1, quantity));
        assertEquals(1, cart.find(1).getQuantity());
        assertEquals(new BigDecimal("95.00"), cart.getTotalAmount());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 21, Integer.MAX_VALUE})
    void invalidUpdateKeepsCart(int quantity) {
        carts.addToCart(cart, 1, 2);
        assertThrows(IllegalArgumentException.class, () -> carts.updateQuantity(cart, 1, quantity));
        assertEquals(2, cart.find(1).getQuantity());
    }

    @Test
    void addAccumulatesButCannotExceedStockOrTwenty() {
        book.setQuantity(5);
        carts.addToCart(cart, 1, 2);
        carts.addToCart(cart, 1, 3);
        assertThrows(IllegalArgumentException.class, () -> carts.addToCart(cart, 1, 1));
        book.setQuantity(30);
        carts.updateQuantity(cart, 1, 20);
        assertThrows(IllegalArgumentException.class, () -> carts.addToCart(cart, 1, 1));
        assertEquals(20, cart.getTotalQuantity());
    }

    @Test
    void deletedAndSoldOutBooksAreRemoved() {
        carts.addToCart(cart, 1, 2);
        book.setQuantity(0);
        assertNotNull(carts.refresh(cart));
        assertTrue(cart.isEmpty());
        book.setQuantity(5);
        carts.addToCart(cart, 1, 2);
        when(books.findById(1)).thenReturn(null);
        assertNotNull(carts.refresh(cart));
        assertTrue(cart.isEmpty());
    }

    @Test
    void stockDecreaseRequiresReviewBeforeCheckout() {
        carts.addToCart(cart, 1, 5);
        book.setQuantity(2);
        assertThrows(IllegalArgumentException.class, this::placeOrder);
        assertEquals(2, cart.find(1).getQuantity());
        verifyNoInteractions(orders);
    }

    @Test
    void priceChangeRequiresReviewAndUpdatesTotal() {
        carts.addToCart(cart, 1, 2);
        book.setPrice(new BigDecimal("120.00"));
        assertThrows(IllegalArgumentException.class, this::placeOrder);
        assertEquals(new BigDecimal("240.00"), cart.getTotalAmount());
        verifyNoInteractions(orders);
        when(orders.insert(any())).thenReturn(7);
        assertEquals(7, placeOrder());
        verify(orders).insert(argThat(order -> order.getTotalAmount().compareTo(new BigDecimal("240")) == 0));
    }

    @Test
    void successfulCodOrderSnapshotsCurrentPriceAndClearsCart() {
        carts.addToCart(cart, 1, 2);
        when(orders.insert(any())).thenAnswer(invocation -> {
            Order_24133059 order = invocation.getArgument(0);
            assertEquals(2, order.getUserId());
            assertEquals("COD", order.getPaymentMethod());
            assertEquals(OrderStatus_24133059.NEW, order.getStatus());
            assertEquals(new BigDecimal("190.00"), order.getTotalAmount());
            assertEquals(2, order.getDetails().get(0).getQuantity());
            assertEquals(book.getTitle(), order.getDetails().get(0).getTitle());
            return 7;
        });
        assertEquals(7, placeOrder());
        assertTrue(cart.isEmpty());
        assertThrows(IllegalArgumentException.class, this::placeOrder);
        verify(orders, times(1)).insert(any());
    }

    @Test
    void rejectedInventoryOrDatabaseFailurePreservesCart() {
        carts.addToCart(cart, 1, 2);
        when(orders.insert(any())).thenThrow(new IllegalStateException("Kho thay đổi"));
        assertThrows(IllegalArgumentException.class, this::placeOrder);
        assertEquals(2, cart.getTotalQuantity());
        doThrow(new RuntimeException("DB unavailable")).when(orders).insert(any());
        assertThrows(RuntimeException.class, this::placeOrder);
        assertEquals(2, cart.getTotalQuantity());
    }

    @Test
    void requiresLoginAndValidShippingInformation() {
        carts.addToCart(cart, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> checkout.placeCodOrder(null, cart,
                "An", "0912345678", "Địa chỉ", ""));
        assertThrows(IllegalArgumentException.class, () -> checkout.placeCodOrder(buyer, cart,
                "", "0912345678", "Địa chỉ", ""));
        assertThrows(IllegalArgumentException.class, () -> checkout.placeCodOrder(buyer, cart,
                "An", "123", "Địa chỉ", ""));
        assertThrows(IllegalArgumentException.class, () -> checkout.placeCodOrder(buyer, cart,
                "An", "0912345678", "", ""));
        assertThrows(IllegalArgumentException.class, () -> checkout.placeCodOrder(buyer, cart,
                "An", "0912345678", "Địa chỉ", "a".repeat(201)));
        verifyNoInteractions(orders);
    }

    @Test
    void concurrentCheckoutCreatesOnlyOneOrder() throws Exception {
        carts.addToCart(cart, 1, 2);
        CountDownLatch enteredDao = new CountDownLatch(1);
        CountDownLatch finishDao = new CountDownLatch(1);
        CountDownLatch secondStarted = new CountDownLatch(1);
        when(orders.insert(any())).thenAnswer(invocation -> {
            enteredDao.countDown();
            assertTrue(finishDao.await(5, TimeUnit.SECONDS));
            return 7;
        });
        var executor = Executors.newFixedThreadPool(2);
        try {
            var first = executor.submit(this::placeOrder);
            try {
                assertTrue(enteredDao.await(5, TimeUnit.SECONDS));
                var second = executor.submit(() -> {
                    secondStarted.countDown();
                    assertThrows(IllegalArgumentException.class, this::placeOrder);
                });
                assertTrue(secondStarted.await(5, TimeUnit.SECONDS));
                finishDao.countDown();
                assertEquals(7, first.get(5, TimeUnit.SECONDS));
                second.get(5, TimeUnit.SECONDS);
            } finally {
                finishDao.countDown();
            }
        } finally {
            executor.shutdownNow();
        }
        verify(orders, times(1)).insert(any());
        assertTrue(cart.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "-1.00"})
    void missingOrNegativePriceCannotBeBoughtOrKeptInCart(String price) {
        carts.addToCart(cart, 1, 1);
        book.setPrice(price.equals("null") ? null : new BigDecimal(price));
        assertThrows(IllegalArgumentException.class, () -> carts.updateQuantity(cart, 1, 2));
        assertNotNull(carts.refresh(cart));
        assertTrue(cart.isEmpty());
        assertThrows(IllegalArgumentException.class, () -> carts.addToCart(cart, 1, 1));
        verifyNoInteractions(orders);
    }

    @Test
    void explicitlyFreeBookHasZeroTotal() {
        book.setPrice(BigDecimal.ZERO);
        carts.addToCart(cart, 1, 1);
        assertEquals(0, cart.getTotalAmount().signum());
    }

    private int placeOrder() {
        return checkout.placeCodOrder(buyer, cart, "Nguyễn Văn An", "0912345678", "1 Võ Văn Ngân", "");
    }
}
