# BookStore_24133059

Bài kiểm tra quá trình môn **Lập Trình Web**, HK1 2026–2027, **đề số 02**.
**Sinh viên:** Trần Minh Thọ — **MSSV:** 24133059.

**Công nghệ:** Java 17+, Maven, Servlet/JSP, JDBC, SiteMesh 3, Tomcat 11, SQL Server.
Kiến trúc 3 tầng: `controller → service → dao`.

## Chức năng

- Đăng ký, xác thực OTP qua email, đăng nhập và đăng xuất.
- Xem sách theo tác giả, phân trang, chi tiết và đánh giá sách.
- Admin: xem, thêm, sửa, xóa sách và phân quyền truy cập.
- User: giỏ hàng, thanh toán COD, lịch sử và chi tiết đơn hàng.
- Giỏ giới hạn **1–min(tồn kho, 20)** cuốn/đầu sách; kiểm tra lại giá và kho trước khi đặt.
- Đặt hàng trừ kho; khách chỉ tự hủy đơn `NEW`, trả kho đúng một lần.

## Chạy project

1. Khởi tạo database `BookStore` trong SSMS theo thứ tự:
   `database/01_schema.sql` → `02_seed.sql` → `03_order_schema.sql`.
   Có thể chạy thêm `05_seed_orders.sql` để tạo 8 đơn mẫu.
   Các script khởi tạo có thao tác xóa dữ liệu, chỉ chạy trên database mới hoặc dùng để làm lại dữ liệu mẫu.
2. Sửa kết nối SQL Server trong `src/main/resources/database.properties`.
3. Cấu hình SMTP trong `src/main/resources/email.properties`, tham khảo `email.properties.example`.
   Nếu để trống tài khoản SMTP, mã OTP được in ra console Tomcat.
4. Import **Existing Maven Projects** trong Eclipse/STS, chọn **Run on Server → Tomcat 11**.
   Mở [trang chủ](http://localhost:8080/BookStore_24133059/home).

Build WAR bằng JDK 17 trở lên:

```bash
mvn clean package
```

File deploy: `target/BookStore_24133059.war`.

## Tài khoản mẫu

Mật khẩu chung: **`123456`**.

| Email | Vai trò |
|---|---|
| `admin@bookstore.local` | Admin |
| `user@bookstore.local` | User, có đơn mẫu |
| `mai@bookstore.local`, `hung@bookstore.local` | User |

## Lịch sử đơn hàng

| Mã trong database | Trạng thái |
|---|---|
| `NEW` | Đơn hàng mới |
| `CONFIRMED` | Đã xác nhận |
| `PREPARING` | Chuẩn bị hàng |
| `SHIPPING` | Vận chuyển |
| `DELIVERING` | Giao hàng |
| `DELIVERED` | Đã giao |
| `CANCELLED` | Đơn hàng hủy |
| `RETURNED` | Đơn hàng hoàn |

Lọc tại `/orders?status=<MÃ>`. Để demo, đổi trạng thái theo
[`database/04_order_status_demo.sql`](database/04_order_status_demo.sql), rồi tải lại trang **Đơn hàng của tôi**.
Script `05_seed_orders.sql` mới không xóa đơn thật hoặc tạo trùng khi chạy lại; không tự chuyển đổi các mẫu từ bản cũ.

## Kiểm thử

```bash
mvn test
```

Mặc định chạy 16 ca nghiệp vụ. Chạy thêm 5 ca tích hợp sau khi deploy WAR mới lên Tomcat cục bộ:

```powershell
$env:BOOKSTORE_TEST_URL='http://localhost:8080/BookStore_24133059'
mvn test
Remove-Item Env:BOOKSTORE_TEST_URL
```

Cần schema database và tài khoản mẫu `mai@bookstore.local`.
Kết quả ngày 05/10/2026: **21/21 ca đạt** — xem [báo cáo kiểm thử](docs/kiem-thu-user-2026-10-05.md).

**Lưu ý:** Giá trong database tính bằng **nghìn đồng** (`95.00` hiển thị `95.000 ₫`).
Nộp [link GitHub](https://github.com/tho03072006-bot/BookStore_24133059) lên UTeXLMS theo yêu cầu bài tập.
