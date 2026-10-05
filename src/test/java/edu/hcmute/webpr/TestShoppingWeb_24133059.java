package edu.hcmute.webpr;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import edu.hcmute.webpr.dao.JDBCConnect_24133059;
import edu.hcmute.webpr.dao.OrderDao_24133059;
import edu.hcmute.webpr.model.*;
import edu.hcmute.webpr.util.PasswordUtil_24133059;

/** Opt-in tests against a running local Tomcat and SQL Server. Only test rows are removed. */
@EnabledIfEnvironmentVariable(named = "BOOKSTORE_TEST_URL", matches = "http://(localhost|127\\.0\\.0\\.1):\\d+/BookStore_24133059")
class TestShoppingWeb_24133059 {
    private String base;
    private String email;
    private int userId;
    private int bookId;
    private int secondBookId;
    private Connection db;
    private HttpClient client;

    @BeforeEach
    void setup() throws Exception {
        base = System.getenv("BOOKSTORE_TEST_URL");
        db = new JDBCConnect_24133059().getConnection();
        email = "shopping-" + UUID.randomUUID().toString().substring(0, 8) + "@example.invalid";
        userId = insert("INSERT INTO users(email, fullname, passwd, is_admin) VALUES (?, ?, ?, 0)",
                email, "Người kiểm thử", PasswordUtil_24133059.md5("TestPass123"));
        bookId = insert("INSERT INTO books(title, price, quantity, cover_image) VALUES (?, 95, 30, ?)",
                "Sách kiểm thử giỏ hàng", "book_01.webp");
        secondBookId = insert("INSERT INTO books(title, price, quantity) VALUES (?, 50, 30)", "Sách mẫu kiểm thử");
        client = newClient();
    }

    @AfterEach
    void cleanup() throws Exception {
        if (db != null) {
            try {
                // Order details cascade from the dedicated test account's orders.
                update("DELETE FROM users WHERE id = ? AND email = ?", userId, email);
                update("DELETE FROM books WHERE bookid = ?", bookId);
                update("DELETE FROM books WHERE bookid = ?", secondBookId);
            } finally {
                db.close();
            }
        }
    }

    @Test
    void cartCrudLimitsAndGuestLogin() throws Exception {
        assertTrue(get("/checkout").uri().getPath().endsWith("/login"));
        post("/cart/add", "bookId", bookId, "quantity", 2);
        assertQuantity(2);
        for (String invalid : List.of("0", "-1", "21", "2147483647", "2147483648", "abc", "")) {
            var response = post("/cart/add", "bookId", bookId, "quantity", invalid);
            assertTrue(response.body().contains("alert error"), "Expected error for " + invalid);
            assertQuantity(2);
        }
        post("/cart/update", "bookId", bookId, "quantity", 20);
        assertQuantity(20);
        update("UPDATE books SET quantity = 3 WHERE bookid = ?", bookId);
        assertQuantity(3); // GET refresh clamps old stock.
        post("/cart/update", "bookId", bookId, "quantity", 4);
        assertQuantity(3);
        var login = post("/login", "email", email, "password", "TestPass123", "next", "checkout");
        assertTrue(login.uri().getPath().endsWith("/checkout"));
        assertQuantity(3); // Login rotates session without losing guest cart.
        post("/cart/remove", "bookId", bookId);
        assertTrue(get("/cart").body().contains("Giỏ hàng của bạn đang trống"));
        post("/cart/add", "bookId", bookId, "quantity", 1);
        post("/cart/clear");
        assertTrue(get("/cart").body().contains("Giỏ hàng của bạn đang trống"));
        assertTrue(get("/checkout").uri().getPath().endsWith("/cart"));
    }

    @Test
    void codPriceValidationInventoryAndCancellation() throws Exception {
        login();
        post("/cart/add", "bookId", bookId, "quantity", 2);
        var invalid = post("/checkout", "receiverName", "An", "receiverPhone", "123", "address", "Địa chỉ");
        assertTrue(invalid.body().contains("Số điện thoại phải"));
        assertEquals(0, scalar("SELECT COUNT(*) FROM orders WHERE userid = ?", userId));
        update("UPDATE books SET price = 120 WHERE bookid = ?", bookId);
        var priceChanged = submitOrder();
        assertTrue(priceChanged.body().contains("đã thay đổi thành"));
        assertEquals(0, scalar("SELECT COUNT(*) FROM orders WHERE userid = ?", userId));
        var placed = submitOrder();
        assertTrue(placed.uri().getPath().endsWith("/orders/detail"));
        int id = orderId(placed);
        assertEquals(28, stock());
        assertEquals(240, scalar("SELECT total_amount FROM orders WHERE order_id = ?", id));
        assertTrue(placed.body().contains("Đơn hàng mới"));
        assertTrue(placed.body().contains("COD"));
        assertTrue(get("/cart").body().contains("Giỏ hàng của bạn đang trống"));
        submitOrder();
        assertEquals(1, scalar("SELECT COUNT(*) FROM orders WHERE userid = ?", userId));
        update("UPDATE books SET title = ?, price = 150 WHERE bookid = ?", "Tên mới", bookId);
        var history = get("/orders/detail?id=" + id);
        assertTrue(history.body().contains("Sách kiểm thử giỏ hàng"));
        assertTrue(history.body().contains("120.000")); // Invoice snapshot survives edits.
        HttpClient other = newClient();
        request(other, "/login", "email", "mai@bookstore.local", "password", "123456");
        assertFalse(request(other, "/orders/detail?id=" + id).body().contains("<h1>Đơn hàng #" + id + "</h1>"));
        request(other, "/orders/cancel", "id", id);
        assertEquals(28, stock());
        post("/orders/cancel", "id", id);
        assertEquals(30, stock());
        post("/orders/cancel", "id", id);
        assertEquals(30, stock()); // Only the first cancel restores stock.
        assertEquals(1, scalar("SELECT COUNT(*) FROM orders WHERE order_id = ? AND status = 'CANCELLED'", id));
    }

    @Test
    void databaseStatusChangesAppearInAllEightFilters() throws Exception {
        login();
        post("/cart/add", "bookId", bookId, "quantity", 1);
        int id = orderId(submitOrder());
        for (OrderStatus_24133059 status : OrderStatus_24133059.values()) {
            update("UPDATE orders SET status = ? WHERE order_id = ?", status.getCode(), id);
            var filtered = get("/orders?status=" + status.getCode());
            assertTrue(filtered.body().contains("Đơn #" + id), status.getCode());
            assertTrue(filtered.body().contains("<span class=\"badge st-" + status.getCode() + "\">" + status.getLabel()));
            assertTrue(get("/orders/detail?id=" + id).body().contains(status.getLabel()));
            String different = status == OrderStatus_24133059.NEW ? "CONFIRMED" : "NEW";
            assertFalse(get("/orders?status=" + different).body().contains("Đơn #" + id));
        }
        // Multiple filtered pages must retain the status query parameter.
        for (int i = 0; i < 6; i++) {
            update("INSERT INTO orders(userid, receiver_name, receiver_phone, address, status, total_amount) "
                    + "VALUES (?, N'Test', '0912345678', N'Test', 'RETURNED', 0)", userId);
        }
        assertTrue(get("/orders?status=RETURNED").body().contains("page=2&amp;status=RETURNED"));
        assertTrue(get("/orders?status=RETURNED&page=2").body().contains("Đơn #" + id));
        assertFalse(get("/orders?status=RETURNED&page=2").body().contains("Không có đơn nào"));
    }

    @Test
    void seedPreservesOrdersAndReservesInventoryOnlyOnce() throws Exception {
        // Redirect the actual SQL seed script to this test's account and books.
        String seed = Files.readString(Path.of("database/05_seed_orders.sql"), StandardCharsets.UTF_8)
                .replace("USE BookStore;", "")
                .replace("user@bookstore.local", email)
                .replace("SET @b1 = @i;", "SET @b1 = " + bookId + ";")
                .replace("SET @b2 = @i + 20;", "SET @b2 = " + secondBookId + ";");
        login();
        post("/cart/add", "bookId", bookId, "quantity", 1);
        int realOrderId = orderId(submitOrder());
        runSeed(seed);
        assertEquals(9, scalar("SELECT COUNT(*) FROM orders WHERE userid = ?", userId));
        assertEquals(17, stock()); // 1 real + 6 active sample orders with 2 copies.
        assertEquals(24, scalar("SELECT quantity FROM books WHERE bookid = ?", secondBookId));
        runSeed(seed);
        assertEquals(9, scalar("SELECT COUNT(*) FROM orders WHERE userid = ?", userId));
        assertEquals(17, stock());
        assertEquals(1, scalar("SELECT COUNT(*) FROM orders WHERE order_id = ?", realOrderId));
        int sampleId = scalar("SELECT order_id FROM orders WHERE userid = ? AND note = N'[DEMO_24133059] NEW'", userId);
        post("/orders/cancel", "id", sampleId);
        assertEquals(19, stock());
        assertEquals(25, scalar("SELECT quantity FROM books WHERE bookid = ?", secondBookId));
        runSeed(seed);
        assertEquals(9, scalar("SELECT COUNT(*) FROM orders WHERE userid = ?", userId));
        assertEquals(19, stock()); // Changed sample status does not recreate the order.
    }

    private void runSeed(String seed) throws SQLException {
        for (String batch : seed.split("(?im)^\\s*GO\\s*$")) {
            if (!batch.isBlank()) {
                try (Statement stmt = db.createStatement()) { stmt.execute(batch); }
            }
        }
    }

    @Test
    void daoRollsBackWholeOrderOnInsufficientStockOrChangedPrice() throws Exception {
        OrderDetail_24133059 first = detail(1, "95.00");
        OrderDetail_24133059 tooMany = detail(31, "95.00");
        Order_24133059 order = new Order_24133059();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setReceiverName("An");
        order.setReceiverPhone("0912345678");
        order.setAddress("Địa chỉ");
        order.setPaymentMethod("COD");
        order.setStatus(OrderStatus_24133059.NEW);
        order.setTotalAmount(new BigDecimal("3040.00"));
        order.setDetails(List.of(first, tooMany));
        OrderDao_24133059 dao = new OrderDao_24133059();
        assertThrows(IllegalStateException.class, () -> dao.insert(order));
        assertEquals(30, stock());
        assertEquals(0, scalar("SELECT COUNT(*) FROM orders WHERE userid = ?", userId));
        order.setDetails(List.of(detail(1, "90.00")));
        assertThrows(IllegalStateException.class, () -> dao.insert(order));
        assertEquals(30, stock());
        assertEquals(0, scalar("SELECT COUNT(*) FROM orders WHERE userid = ?", userId));
    }

    private OrderDetail_24133059 detail(int quantity, String price) {
        OrderDetail_24133059 result = new OrderDetail_24133059();
        result.setBookId(bookId);
        result.setTitle("Sách kiểm thử giỏ hàng");
        result.setPrice(new BigDecimal(price));
        result.setQuantity(quantity);
        return result;
    }

    private HttpClient newClient() {
        return HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.ALWAYS).connectTimeout(Duration.ofSeconds(10)).build();
    }

    private void login() throws Exception {
        assertTrue(post("/login", "email", email, "password", "TestPass123").uri().getPath().endsWith("/home"));
    }

    private HttpResponse<String> submitOrder() throws Exception {
        return post("/checkout", "receiverName", "Nguyễn Văn An", "receiverPhone", "0912345678",
                "address", "1 Võ Văn Ngân", "note", "Giao giờ hành chính");
    }

    private int orderId(HttpResponse<String> response) {
        assertTrue(response.uri().getPath().endsWith("/orders/detail"), response.uri().toString());
        return Integer.parseInt(response.uri().getQuery().substring(3));
    }

    private void assertQuantity(int quantity) throws Exception {
        assertTrue(get("/cart").body().matches("(?s).*name=\"quantity\"\\s+value=\"" + quantity + "\".*"));
    }

    private HttpResponse<String> get(String path) throws Exception { return request(client, path); }
    private HttpResponse<String> post(String path, Object... values) throws Exception { return request(client, path, values); }

    private HttpResponse<String> request(HttpClient http, String path, Object... values) throws Exception {
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(base + path)).timeout(Duration.ofSeconds(20));
        if (values.length > 0 || path.endsWith("/cart/clear")) {
            StringBuilder form = new StringBuilder();
            for (int i = 0; i < values.length; i += 2) {
                if (form.length() > 0) form.append('&');
                form.append(URLEncoder.encode(values[i].toString(), StandardCharsets.UTF_8)).append('=')
                        .append(URLEncoder.encode(values[i + 1].toString(), StandardCharsets.UTF_8));
            }
            req.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(form.toString()));
        }
        var response = http.send(req.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), path);
        return response;
    }

    private int stock() throws Exception { return scalar("SELECT quantity FROM books WHERE bookid = ?", bookId); }

    private void bind(PreparedStatement ps, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) ps.setObject(i + 1, values[i]);
    }

    private int insert(String sql, Object... values) throws SQLException {
        try (PreparedStatement ps = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, values);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { assertTrue(rs.next()); return rs.getInt(1); }
        }
    }

    private void update(String sql, Object... values) throws SQLException {
        try (PreparedStatement ps = db.prepareStatement(sql)) { bind(ps, values); ps.executeUpdate(); }
    }

    private int scalar(String sql, Object... values) throws SQLException {
        try (PreparedStatement ps = db.prepareStatement(sql)) {
            bind(ps, values);
            try (ResultSet rs = ps.executeQuery()) { assertTrue(rs.next()); return rs.getInt(1); }
        }
    }
}
