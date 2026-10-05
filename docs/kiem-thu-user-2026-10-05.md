# Kiểm tra yêu cầu vai trò User — 05/10/2026

Project: `BookStore_24133059` — Trần Minh Thọ, MSSV 24133059.

## Đối chiếu yêu cầu

| Yêu cầu | Kết quả | Thành phần chính |
|---|---|---|
| Thêm, xóa, sửa số lượng giỏ trong giới hạn | Đạt | `CartService_24133059`, các servlet `/cart/*`, `cart.jsp` |
| Thanh toán COD | Đạt | `CheckoutServlet_24133059`, `OrderService_24133059`, `OrderDao_24133059` |
| Lịch sử đơn hàng lọc 8 trạng thái | Đạt | `OrderStatus_24133059`, `/orders`, `/orders/detail`, `orders.jsp` |
| Đổi trạng thái bằng database để quan sát | Đạt | `database/04_order_status_demo.sql`; kiểm thử cập nhật SQL rồi đọc lại giao diện |

08 trạng thái: `NEW`, `CONFIRMED`, `PREPARING`, `SHIPPING`, `DELIVERING`,
`DELIVERED`, `CANCELLED`, `RETURNED`.

## Các lỗi đã sửa trong lần kiểm tra

- Chặn tràn số khi cộng số lượng rất lớn vào dòng giỏ đã có. Tham số sai định
  dạng hoặc vượt miền `int` bị từ chối, không tự thêm một cuốn.
- Đọc lại giá/tên/ảnh/tồn kho từ database. Nếu giá hoặc số lượng thay đổi ngay
  trước khi đặt, hiển thị thông báo và yêu cầu kiểm tra lại giỏ. Giá trên hóa
  đơn đã đặt giữ nguyên.
- Khóa theo giỏ trong suốt lúc chốt COD và xóa giỏ để hai request đồng thời
  không tạo hai đơn. Các thao tác sửa giỏ dùng cùng khóa.
- Câu SQL trừ kho kiểm tra cả số lượng và giá. Nếu không thỏa, rollback toàn
  bộ đơn thay vì ghi hóa đơn với giá cũ hoặc trừ kho một phần.
- Script tạo 8 đơn mẫu không xóa lịch sử thật, không tạo trùng khi chạy lại,
  và trừ kho cho đơn còn hiệu lực để hủy đơn mẫu không làm tăng kho sai.

## Xác minh

Môi trường: JDK 21 (biên dịch với `release 17`), Maven 3.9.16, Tomcat 11.0.25,
Microsoft SQL Server. Bản WAR mới được chạy trên Tomcat riêng ở cổng 8083.

**Kết quả: 31 ca đạt, 0 lỗi, 0 bỏ qua** khi bật kiểm thử tích hợp.

| Nhóm | Nội dung kiểm tra |
|---|---|
| 19 ca nghiệp vụ | Giới hạn 1–20/tồn kho, số lượng âm/0/cực lớn, giá đổi, hết hàng/xóa sách, thông tin nhận hàng, bảo toàn giỏ khi lỗi, gửi COD đồng thời; chặn giá NULL/âm, giá 0 được khai báo rõ vẫn hợp lệ |
| Giỏ qua HTTP | Thêm/sửa/xóa/xóa hết; từ chối tham số sai; giảm số lượng khi kho giảm; giữ giỏ khách khi đăng nhập; chặn thanh toán giỏ trống |
| Giá NULL / 0 qua HTTP/SQL | Chưa niêm giá thì không thêm giỏ/đặt hàng; giá 0 được khai báo rõ tạo COD tổng 0 và trừ đúng kho |
| COD qua HTTP/SQL | Thông tin sai không tạo đơn; giá đổi yêu cầu kiểm tra; đơn mới có COD và đúng tổng; trừ kho/xóa giỏ; gửi lại không tạo trùng; hóa đơn giữ giá cũ; phân quyền xem/hủy; hủy trả kho một lần |
| Lịch sử qua HTTP/SQL | Lần lượt cập nhật đủ 8 mã bằng SQL, xác minh bộ lọc/nhãn/chi tiết; loại đơn khỏi bộ lọc khác; phân trang giữ trạng thái; script đồng bộ kho khi hủy/hoàn và rollback nếu khôi phục thiếu kho |
| Tìm kiếm | Tên/tác giả/ISBN, lọc tác giả/còn hàng, thứ tự giá, ký tự %, _, [ được tìm literal; chuỗi SQL injection không mở rộng kết quả; bộ lọc giữ trong URL |
| CSRF và session | POST thiếu/sai token, token phiên khác và token trước đăng nhập đều trả 403; token đúng dùng được; GET logout không đăng xuất, POST logout kết thúc phiên; cookie HttpOnly, trang đơn không cache, CSP/chống iframe |
| Ảnh bìa | GET có Cache-Control và ETag; gửi If-None-Match đúng trả 304, thân rỗng |
| OTP demo | Số điện thoại sai bị từ chối; trước OTP không tạo user; mã đúng tạo User; mã sai báo lỗi; chặn gửi lại trong 60 giây và chặn sau 5 lần thử/mã |
| Chức năng cũ | Admin CRUD qua multipart có CSRF và phân quyền; review yêu cầu đăng nhập, kiểm tra số sao, escape HTML và cập nhật một review/người/sách |
| Transaction DAO | Thiếu kho ở dòng sau rollback cả đơn và phần kho đã trừ; giá khác database không ghi đơn |
| Script đơn mẫu | Chạy lại schema đơn hàng hai lần không xóa đơn; giữ đơn thật đã có; tạo 8 mẫu; đúng kho; chạy lại không trùng; hủy mẫu trả đúng kho; chạy lại sau hủy không sinh thêm |

Kiểm thử tích hợp tạo dữ liệu riêng và tự dọn sau mỗi ca. Các tài khoản, sách,
các đơn có sẵn trong database của người dùng được giữ lại. Script mẫu mới
không tự sửa các mẫu đã tạo bởi phiên bản cũ; kiểm tra thao tác COD/hủy bằng
đơn đặt qua giao diện hoặc dữ liệu mới từ script đã sửa.

## Chạy lại

```powershell
mvn clean package
# Deploy target/BookStore_24133059.war lên Tomcat trước khi chạy test web.
# Khởi động Tomcat với BOOKSTORE_MAIL_MODE=console để kiểm thử OTP không gửi email.
$env:BOOKSTORE_TEST_URL='http://localhost:8080/BookStore_24133059'
$env:BOOKSTORE_TEST_OTP_LOG='<đường dẫn tuyệt đối tới log Console chứa OTP-DEMO>'
mvn test
Remove-Item Env:BOOKSTORE_TEST_URL
Remove-Item Env:BOOKSTORE_TEST_OTP_LOG
```

Không đặt `BOOKSTORE_TEST_URL`: chỉ chạy 19 ca nghiệp vụ, 12 ca tích hợp được bỏ
qua. Chỉ đặt URL: chạy thêm 11 ca; ca OTP cần thêm đường dẫn log và Tomcat ở chế độ console. Đặt URL: dùng database cấu hình trong `database.properties`, schema
đơn hàng và tài khoản mẫu `mai@bookstore.local` cần tồn tại.

## Giao diện đã kiểm tra

Dùng trình duyệt ở 320, 390 và 1320 px: giỏ, thanh toán, lịch sử và chi tiết đơn không tràn ngang; desktop danh sách có 3 cột.
Kiểm tra tìm tiếng Việt “Mắt Biếc”, thêm giỏ giữ bộ lọc, sửa số lượng, đăng nhập giữ giỏ, lọc đã xác nhận và tiến trình đúng bước.
Form COD không có ID trùng/ô thiếu label; ảnh hiển thị được; Enter trên skip link đưa focus đến `main-content`.
Nút Cập nhật/Xóa đo được 44 px; ô điện thoại 44 px. Các kiểm tra DOM này không thay thế kiểm định WCAG đầy đủ.
Ảnh và bản đối chiếu chi tiết: [đối chiếu yêu cầu](doi-chieu-yeu-cau.md).

Luồng giỏ/COD/lịch sử dùng form và liên kết HTML; JavaScript chỉ bổ trợ UX.
SMTP qua hộp thư thật chưa được gửi trong lần kiểm thử này; ca OTP xác minh bằng log của server ở chế độ demo.
Cấu trúc `users.passwd varchar(32)` và MD5 được giữ theo đề, cần đổi cơ chế lưu mật khẩu nếu phát triển thành hệ thống vận hành thật.

Link nộp trên UTeXLMS vẫn là repo GitHub của project. Việc nộp LMS và kiểm tra
các bài tập 3–9 ngoài project này không thuộc lần kiểm tra mã nguồn này.
