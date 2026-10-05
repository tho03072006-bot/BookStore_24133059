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
import java.util.regex.Pattern;
import edu.hcmute.webpr.dao.BookDao_24133059;

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
    private String registrationEmail;
    private String adminBookTitle;

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
                if (registrationEmail != null) {
                    update("DELETE FROM users WHERE email = ?", registrationEmail);
                    update("DELETE FROM users WHERE email = ?", registrationEmail + "x");
                }
                update("DELETE FROM books WHERE bookid = ?", bookId);
                update("DELETE FROM books WHERE bookid = ?", secondBookId);
                if (adminBookTitle != null) {
                    update("DELETE FROM books WHERE title = ? OR title = ?", adminBookTitle, adminBookTitle + " edited");
                }
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
        assertTrue(invalid.body().contains("id=\"receiverPhone-error\""));
        assertTrue(invalid.body().contains("aria-describedby=\"phone-hint receiverPhone-error\""));
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
            String demo = Files.readString(Path.of("database/04_order_status_demo.sql"), StandardCharsets.UTF_8)
                    .replace("USE BookStore;", "")
                    .replace("DECLARE @orderId INT = NULL", "DECLARE @orderId INT = " + id)
                    .replace("= 'CONFIRMED'; --", "= '" + status.getCode() + "'; --");
            runSeed(demo);
            int expected = status == OrderStatus_24133059.CANCELLED || status == OrderStatus_24133059.RETURNED ? 30 : 29;
            assertEquals(expected, stock());
            runSeed(demo); // Applying the same state twice must not change stock again.
            assertEquals(expected, stock());
            var filtered = get("/orders?status=" + status.getCode());
            assertTrue(filtered.body().contains("Đơn #" + id), status.getCode());
            assertTrue(filtered.body().contains("<span class=\"badge st-" + status.getCode() + "\">" + status.getLabel()));
            assertTrue(get("/orders/detail?id=" + id).body().contains(status.getLabel()));
            String different = status == OrderStatus_24133059.NEW ? "CONFIRMED" : "NEW";
            assertFalse(get("/orders?status=" + different).body().contains("Đơn #" + id));
        }
        String restore = Files.readString(Path.of("database/04_order_status_demo.sql"), StandardCharsets.UTF_8)
                .replace("USE BookStore;", "").replace("DECLARE @orderId INT = NULL", "DECLARE @orderId INT = " + id)
                .replace("= 'CONFIRMED'; --", "= 'NEW'; --");
        update("UPDATE books SET quantity = 0 WHERE bookid = ?", bookId);
        assertThrows(SQLException.class, () -> runSeed(restore));
        assertEquals(1, scalar("SELECT COUNT(*) FROM orders WHERE order_id = ? AND status = 'RETURNED'", id));
        update("UPDATE books SET quantity = 30 WHERE bookid = ?", bookId);
        runSeed(restore);
        runSeed(restore);
        assertEquals(29, stock());
        runSeed(restore.replace("= 'NEW'; --", "= 'RETURNED'; --"));
        assertEquals(30, stock());
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
        String migration = Files.readString(Path.of("database/03_order_schema.sql"), StandardCharsets.UTF_8)
                .replace("USE BookStore;", "");
        runSeed(migration);
        runSeed(migration);
        assertEquals(1, scalar("SELECT COUNT(*) FROM orders WHERE order_id = ?", realOrderId));
        assertEquals(29, stock());
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
                try (Statement stmt = db.createStatement()) {
                    // SQL Server có nhiều update count/result set trong một batch.
                    // Đọc hết để không bỏ sót THROW đến sau một UPDATE.
                    boolean result = stmt.execute(batch);
                    while (result || stmt.getUpdateCount() != -1) {
                        if (result) {
                            try (ResultSet rs = stmt.getResultSet()) { while (rs.next()) { /* drain */ } }
                        }
                        result = stmt.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
                    }
                }
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

    @Test
    void searchFiltersLiteralWildcardsSortAndPagination() throws Exception {
        String marker = "catalog-" + UUID.randomUUID().toString().substring(0, 8);
        update("UPDATE books SET title = ?, isbn = 101234567 WHERE bookid = ?", marker + " 100%_[", bookId);
        update("UPDATE books SET title = ? WHERE bookid = ?", marker + " second", secondBookId);
        int authorId = scalar("SELECT TOP 1 author_id FROM author ORDER BY author_id");
        update("INSERT INTO book_author(bookid, author_id) VALUES (?, ?)", bookId, authorId);
        BookDao_24133059 dao = new BookDao_24133059();
        var both = new BookFilter_24133059(marker, null, "price_asc", false);
        assertEquals(2, dao.countSearch(both));
        assertEquals(List.of(secondBookId, bookId), dao.search(both, 0, 10).stream().map(Book_24133059::getBookId).toList());
        assertEquals(bookId, dao.search(new BookFilter_24133059(marker, null, "price_desc", false), 0, 1).get(0).getBookId());
        assertEquals(1, dao.countSearch(new BookFilter_24133059(marker, authorId, "latest", false)));
        assertEquals(1, dao.countSearch(new BookFilter_24133059("101234567", null, "latest", false)));
        assertEquals(1, dao.countSearch(new BookFilter_24133059(marker + " 100%_[", null, "latest", false)));
        assertEquals(0, dao.countSearch(new BookFilter_24133059("' OR 1=1 --", null, "latest", false)));
        update("UPDATE books SET quantity = 0 WHERE bookid = ?", bookId);
        assertEquals(1, dao.countSearch(new BookFilter_24133059(marker, null, "latest", true)));
        var empty = get("/products?q=" + marker + "&author=2147483647");
        assertTrue(empty.body().contains("Không tìm thấy sách phù hợp"));
        var filtered = get("/products?q=" + marker + "&sort=price_asc");
        assertTrue(filtered.body().contains("value=\"/products?q=" + marker));
        assertTrue(get("/products?sort=price_asc&stock=1").body().contains("sort=price_asc&amp;stock=1"));
        assertEquals("latest", new BookFilter_24133059("", null, "price;DROP TABLE books", false).getSort());
    }

    @Test
    void csrfSessionRotationAndSafeLogout() throws Exception {
        var initial = client.send(HttpRequest.newBuilder(URI.create(base + "/login")).GET().build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertTrue(initial.headers().allValues("Set-Cookie").stream().anyMatch(v -> v.toLowerCase().contains("httponly")));
        String before = csrfToken(client);
        assertEquals(403, rawPost("/cart/add", "bookId=" + bookId + "&quantity=1").statusCode());
        assertEquals(403, rawPost("/cart/add", "_csrf=invalid&bookId=" + bookId + "&quantity=1").statusCode());
        String foreign = csrfToken(newClient());
        assertEquals(403, rawPost("/cart/add", "_csrf=" + foreign + "&bookId=" + bookId + "&quantity=1").statusCode());
        assertTrue(get("/cart").body().contains("Giỏ hàng của bạn đang trống"));
        login();
        assertNotEquals(before, csrfToken(client));
        assertEquals(403, rawPost("/cart/add", "_csrf=" + before + "&bookId=" + bookId + "&quantity=1").statusCode());
        assertTrue(get("/logout").body().contains("Đơn hàng của tôi"));
        var response = get("/orders");
        assertEquals("no-store", response.headers().firstValue("Cache-Control").orElseThrow());
        assertEquals("DENY", response.headers().firstValue("X-Frame-Options").orElseThrow());
        assertTrue(response.headers().firstValue("Content-Security-Policy").orElseThrow().contains("form-action 'self'"));
        String form = "_csrf=" + csrfToken(client);
        assertEquals(200, rawPost("/logout", form).statusCode());
        assertTrue(get("/orders").uri().getPath().endsWith("/login"));
    }

    @Test
    void bookCoverCacheRevalidatesWithEtag() throws Exception {
        URI uri = URI.create(base + "/image?name=book_01.webp");
        var first = client.send(HttpRequest.newBuilder(uri).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
        assertEquals(200, first.statusCode());
        assertTrue(first.body().length > 100);
        assertEquals("public, max-age=3600", first.headers().firstValue("Cache-Control").orElseThrow());
        String etag = first.headers().firstValue("ETag").orElseThrow();
        var second = client.send(HttpRequest.newBuilder(uri).header("If-None-Match", etag).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
        assertEquals(304, second.statusCode());
        assertEquals(0, second.body().length);
        assertEquals(etag, second.headers().firstValue("ETag").orElseThrow());
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "BOOKSTORE_TEST_OTP_LOG", matches = ".+")
    void registrationOtpDemoResendThrottleAndAttemptLimit() throws Exception {
        registrationEmail = "otp-" + UUID.randomUUID().toString().substring(0, 8) + "@example.invalid";
        var invalid = post("/register", "email", registrationEmail, "fullname", "Người thử OTP", "phone", "letters",
                "password", "TestPass123", "confirmPassword", "TestPass123");
        assertTrue(invalid.body().contains("Số điện thoại cần"));
        var pending = post("/register", "email", registrationEmail, "fullname", "Người thử OTP", "phone", "0912345678",
                "password", "TestPass123", "confirmPassword", "TestPass123");
        assertTrue(pending.uri().getPath().endsWith("/verify-otp"));
        assertEquals(0, scalar("SELECT COUNT(*) FROM users WHERE email = ?", registrationEmail));
        String log = Files.readString(Path.of(System.getenv("BOOKSTORE_TEST_OTP_LOG")), StandardCharsets.UTF_8);
        var matcher = Pattern.compile(Pattern.quote("Ma OTP cho " + registrationEmail + " la: ") + "([0-9]{6})").matcher(log);
        assertTrue(matcher.find(), "Demo OTP must be available in the local server log");
        String otp = matcher.group(1);
        assertTrue(post("/resend-otp", "unused", "").body().contains("Vui lòng chờ 60 giây"));
        assertTrue(post("/verify-otp", "otp", "invalid").body().contains("Mã OTP không đúng"));
        assertTrue(post("/verify-otp", "otp", otp).uri().getPath().endsWith("/login"));
        assertEquals(1, scalar("SELECT COUNT(*) FROM users WHERE email = ? AND is_admin = 0", registrationEmail));
        // A new registration has five attempts; even the correct code is rejected afterwards.
        post("/register", "email", registrationEmail + "x", "fullname", "Người thử OTP", "phone", "",
                "password", "TestPass123", "confirmPassword", "TestPass123");
        for (int i = 0; i < 5; i++) post("/verify-otp", "otp", "invalid");
        assertTrue(post("/verify-otp", "otp", "000000").body().contains("Bạn đã nhập sai 5 lần"));
        assertEquals(0, scalar("SELECT COUNT(*) FROM users WHERE email = ?", registrationEmail + "x"));
    }

    private String csrfToken(HttpClient http) throws Exception {
        String body = http.send(HttpRequest.newBuilder(URI.create(base + "/login")).GET().build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        var matcher = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"").matcher(body);
        assertTrue(matcher.find());
        return matcher.group(1);
    }

    @Test
    void existingAdminCrudWorksWithMultipartCsrf() throws Exception {
        assertTrue(get("/admin/books").uri().getPath().endsWith("/login"));
        login();
        assertTrue(get("/admin/books").body().contains("không có quyền"));
        update("UPDATE users SET is_admin = 1 WHERE id = ?", userId);
        rawPost("/logout", "_csrf=" + csrfToken(client));
        assertTrue(post("/login", "email", email, "password", "TestPass123").uri().getPath().endsWith("/admin/books"));
        int authorId = scalar("SELECT TOP 1 author_id FROM author ORDER BY author_id");
        adminBookTitle = "admin-test-" + UUID.randomUUID();
        assertEquals(200, multipart("/admin/books/add", "title", adminBookTitle, "price", "42.50", "quantity", "6",
                "authorIds", authorId, "coverImage", "book_01.webp").statusCode());
        int id = scalar("SELECT bookid FROM books WHERE title = ?", adminBookTitle);
        assertEquals(6, scalar("SELECT quantity FROM books WHERE bookid = ?", id));
        assertTrue(get("/admin/books/view?id=" + id).body().contains(adminBookTitle));
        assertEquals(200, multipart("/admin/books/edit", "bookId", id, "title", adminBookTitle + " edited", "price", "43.50",
                "quantity", "9", "authorIds", authorId).statusCode());
        assertEquals(9, scalar("SELECT quantity FROM books WHERE bookid = ?", id));
        assertTrue(get("/book?id=" + id).body().contains(adminBookTitle + " edited"));
        post("/admin/books/delete", "id", id);
        assertEquals(0, scalar("SELECT COUNT(*) FROM books WHERE bookid = ?", id));
    }

    @Test
    void existingReviewsRequireLoginValidateAndEscapeText() throws Exception {
        assertTrue(post("/review", "bookId", bookId, "rating", 5, "reviewText", "Guest").uri().getPath().endsWith("/login"));
        login();
        post("/review", "bookId", bookId, "rating", 0, "reviewText", "Invalid");
        assertEquals(0, scalar("SELECT COUNT(*) FROM rating WHERE userid = ? AND bookid = ?", userId, bookId));
        var escaped = post("/review", "bookId", bookId, "rating", 5, "reviewText", "<script>alert(1)</script> & text");
        assertTrue(escaped.body().contains("&lt;script&gt;"));
        assertFalse(escaped.body().contains("<script>alert(1)</script>"));
        post("/review", "bookId", bookId, "rating", 3, "reviewText", "Updated review");
        assertEquals(1, scalar("SELECT COUNT(*) FROM rating WHERE userid = ? AND bookid = ?", userId, bookId));
        assertEquals(3, scalar("SELECT rating FROM rating WHERE userid = ? AND bookid = ?", userId, bookId));
    }

    private HttpResponse<String> multipart(String path, Object... fields) throws Exception {
        String boundary = "Boundary" + UUID.randomUUID();
        StringBuilder body = new StringBuilder();
        Object[] withToken = new Object[fields.length + 2];
        withToken[0] = "_csrf";
        withToken[1] = csrfToken(client);
        System.arraycopy(fields, 0, withToken, 2, fields.length);
        for (int i = 0; i < withToken.length; i += 2) {
            body.append("--").append(boundary).append("\r\nContent-Disposition: form-data; name=\"")
                .append(withToken[i]).append("\"\r\n\r\n").append(withToken[i + 1]).append("\r\n");
        }
        body.append("--").append(boundary).append("--\r\n");
        return client.send(HttpRequest.newBuilder(URI.create(base + path))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8)).build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    @Test
    void missingPriceIsBlockedWhileExplicitZeroPriceCanCheckout() throws Exception {
        update("UPDATE books SET price = NULL WHERE bookid = ?", bookId);
        assertTrue(get("/book?id=" + bookId).body().contains("chưa niêm giá"));
        assertTrue(post("/cart/add", "bookId", bookId, "quantity", 1).body().contains("chưa có giá hợp lệ"));
        assertEquals(30, stock());
        login();
        update("UPDATE books SET price = 0 WHERE bookid = ?", bookId);
        post("/cart/add", "bookId", bookId, "quantity", 1);
        int id = orderId(submitOrder());
        assertEquals(0, scalar("SELECT total_amount FROM orders WHERE order_id = ?", id));
        assertEquals(29, stock());
    }

    private HttpResponse<String> rawPost(String path, String form) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
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
            String tokenPage = http.send(HttpRequest.newBuilder(URI.create(base + "/login")).GET().build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
            var matcher = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"").matcher(tokenPage);
            assertTrue(matcher.find(), "Form must provide a CSRF token");
            StringBuilder form = new StringBuilder("_csrf=" + matcher.group(1));
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
