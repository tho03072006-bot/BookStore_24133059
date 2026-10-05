package edu.hcmute.webpr.service;

import java.util.ArrayList;
import java.util.List;

import edu.hcmute.webpr.model.Book_24133059;
import edu.hcmute.webpr.model.CartItem_24133059;
import edu.hcmute.webpr.model.Cart_24133059;
import edu.hcmute.webpr.util.Constants_24133059;

/**
 * TẦNG BUSINESS - hiện thực nghiệp vụ giỏ hàng.
 *
 * Trần Minh Thọ - 24133059
 */
public class CartService_24133059 implements ICartService_24133059 {

    private final IBookService_24133059 bookService;

    public CartService_24133059() {
        this(new BookService_24133059());
    }

    CartService_24133059(IBookService_24133059 bookService) {
        this.bookService = bookService;
    }

    @Override
    public void addToCart(Cart_24133059 cart, int bookId, int quantity) {
        synchronized (cart) {
            addLocked(cart, bookId, quantity);
        }
    }

    private void addLocked(Cart_24133059 cart, int bookId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Số lượng phải từ 1 trở lên.");
        }
        Book_24133059 book = bookService.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Cuốn sách này không còn tồn tại.");
        }

        CartItem_24133059 item = cart.find(bookId);
        long newQuantity = (item == null) ? quantity : (long) item.getQuantity() + quantity;
        int limit = limitOf(book);

        if (limit <= 0) {
            throw new IllegalArgumentException("Sách \"" + book.getTitle() + "\" đã hết hàng.");
        }
        if (newQuantity > limit) {
            throw new IllegalArgumentException("Chỉ có thể mua tối đa " + limit
                    + " cuốn \"" + book.getTitle() + "\".");
        }

        cart.put(new CartItem_24133059(book, (int) newQuantity));
    }

    @Override
    public void updateQuantity(Cart_24133059 cart, int bookId, int quantity) {
        synchronized (cart) {
            updateLocked(cart, bookId, quantity);
        }
    }

    private void updateLocked(Cart_24133059 cart, int bookId, int quantity) {
        CartItem_24133059 item = cart.find(bookId);
        if (item == null) {
            throw new IllegalArgumentException("Cuốn sách này không có trong giỏ hàng.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Số lượng phải từ 1 trở lên. "
                    + "Muốn bỏ khỏi giỏ thì bấm nút Xóa.");
        }

        Book_24133059 book = bookService.findById(bookId);
        if (book == null) {
            cart.remove(bookId);
            throw new IllegalArgumentException("Cuốn sách này không còn tồn tại, đã bỏ khỏi giỏ.");
        }

        int limit = limitOf(book);
        if (quantity > limit) {
            throw new IllegalArgumentException("Chỉ có thể mua tối đa " + limit
                    + " cuốn \"" + book.getTitle() + "\".");
        }

        cart.put(new CartItem_24133059(book, quantity));
    }

    @Override
    public void removeFromCart(Cart_24133059 cart, int bookId) {
        synchronized (cart) {
            if (!cart.contains(bookId)) {
                throw new IllegalArgumentException("Cuốn sách này không có trong giỏ hàng.");
            }
            cart.remove(bookId);
        }
    }

    @Override
    public void clear(Cart_24133059 cart) {
        synchronized (cart) {
            cart.clear();
        }
    }

    @Override
    public String refresh(Cart_24133059 cart) {
        synchronized (cart) {
            return refreshLocked(cart);
        }
    }

    private String refreshLocked(Cart_24133059 cart) {
        List<String> notes = new ArrayList<>();

        for (CartItem_24133059 item : cart.getItems()) {
            Book_24133059 book = bookService.findById(item.getBookId());

            if (book == null) {
                cart.remove(item.getBookId());
                notes.add("\"" + item.getTitle() + "\" không còn bán nên đã được bỏ khỏi giỏ");
                continue;
            }

            int limit = limitOf(book);
            if (limit <= 0) {
                cart.remove(item.getBookId());
                notes.add("\"" + item.getTitle() + "\" đã hết hàng nên được bỏ khỏi giỏ");
            } else {
                CartItem_24133059 updated = new CartItem_24133059(book,
                        Math.min(item.getQuantity(), limit));
                if (item.getPrice().compareTo(updated.getPrice()) != 0) {
                    notes.add("Giá của \"" + book.getTitle() + "\" đã thay đổi thành "
                            + updated.getPriceText());
                }
                if (item.getQuantity() > limit) {
                    notes.add("\"" + item.getTitle() + "\" chỉ còn " + limit
                            + " cuốn nên số lượng đã được giảm lại");
                }
                cart.put(updated);
            }
        }

        return notes.isEmpty() ? null : String.join("; ", notes) + ".";
    }

    /** Số lượng tối đa được phép mua: nhỏ hơn giữa tồn kho và trần mỗi đầu sách. */
    private int limitOf(Book_24133059 book) {
        int stock = (book.getQuantity() == null) ? 0 : book.getQuantity();
        return Math.min(stock, Constants_24133059.CART_MAX_QUANTITY_PER_BOOK);
    }
}
