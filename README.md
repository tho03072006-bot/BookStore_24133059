# BookStore – Đề thi Quá trình số 02

**Môn:** Lập Trình Web — HK1 2026-2027 — Khoa CNTT, Bộ môn Công nghệ phần mềm
**Sinh viên:** Trần Minh Thọ — **MSSV:** 24133059 — **Mã đề:** 02

Maven Project (đóng gói `war`) dùng **Servlet + JDBC + JSP + SiteMesh 3**, chạy trên
**Apache Tomcat 11** (Jakarta EE), dữ liệu lưu ở **Microsoft SQL Server**.

Tất cả Class / Interface / Controller / Service đặt tên theo quy tắc của đề:
`TênClass_24133059.java`.

---

## 1. Chạy thử trong 4 bước

### Bước 1 — Tạo database

Mở SQL Server Management Studio, chạy lần lượt:

1. `database/01_schema.sql` — tạo database `BookStore` và 5 bảng đúng sơ đồ trong đề.
2. `database/02_seed.sql` — thêm dữ liệu mẫu: 5 tác giả, 50 cuốn sách, 4 tài khoản, 20 review.
3. `database/03_order_schema.sql` — thêm 2 bảng `orders` và `order_detail` cho
   chức năng giỏ hàng / đặt hàng COD / lịch sử đơn hàng.
4. `database/05_seed_orders.sql` — tạo sẵn 8 đơn hàng mẫu, mỗi đơn một trạng
   thái, để mở trang "Đơn hàng của tôi" là bấm thử được ngay cả 8 bộ lọc.

Hoặc chạy bằng dòng lệnh:

```bash
sqlcmd -S localhost,1433 -U sa -P <mat_khau> -C -f 65001 -i database/01_schema.sql
```

```bash
sqlcmd -S localhost,1433 -U sa -P <mat_khau> -C -f 65001 -i database/02_seed.sql
```

### Bước 2 — Sửa thông tin kết nối

Mở `src/main/resources/database.properties` và sửa `db.server`, `db.port`,
`db.name`, `db.user`, `db.password` cho khớp máy đang chạy. **Không cần sửa
code Java rồi build lại.**

Thử kết nối trước khi chạy web: chuột phải
`src/main/java/edu/hcmute/webpr/dao/JDBCConnect_24133059.java` →
*Run As → Java Application*. Console in ra `Kết nối SQL Server thành công!`
là đã thông.

### Bước 3 — Gửi mail OTP (Câu 2)

`src/main/resources/email.properties` đã điền sẵn tài khoản SMTP nên chạy là
gửi được mã OTP thật, không phải cấu hình thêm gì.

Muốn đổi sang hộp thư khác thì xem hướng dẫn lấy **App Password** của Gmail
trong `email.properties.example`. Để trống hai dòng `mail.username` và
`mail.password` thì chương trình vẫn chạy bình thường, mã OTP chỉ chuyển sang
**in ra Console của Tomcat** thay vì gửi qua email.

> Nếu để trống, chương trình vẫn chạy bình thường: mã OTP sẽ được **in ra Console
> của Tomcat** thay vì gửi qua email.

### Bước 4 — Chạy project

Trong Spring Tools for Eclipse:

1. *File → Import → Existing Maven Projects* → chọn thư mục `BookStore_24133059`.
2. Chuột phải project → *Run As → Run on Server* → chọn **Tomcat v11.0**.
3. Mở `http://localhost:8080/BookStore_24133059/home`.

Build bằng dòng lệnh (cần JDK 17 trở lên):

```bash
mvn clean package
```

Trên máy đang dùng, `JAVA_HOME` mặc định trỏ vào Java 8. Mở PowerShell và đặt JDK
21 trước khi chạy Maven:

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot'
& 'D:\WEB\apache-maven-3.9.16\bin\mvn.cmd' clean package
```

### Tài khoản mẫu

Mật khẩu của cả 4 tài khoản đều là `123456`.

| Email | Vai trò |
|---|---|
| `admin@bookstore.local` | Admin — vào được Trang quản trị |
| `user@bookstore.local` | User |
| `mai@bookstore.local` | User |
| `hung@bookstore.local` | User |

---

## 2. Cấu trúc 3 tầng (Câu 1)

```
src/main/java/edu/hcmute/webpr/
├── controller/   ← PRESENTATION LAYER (Servlet, mô hình MVC)
├── service/      ← BUSINESS LAYER  (nghiệp vụ, kiểm tra dữ liệu)
├── dao/          ← DATA ACCESS LAYER (JDBC thuần, SQL)
├── model/        ← đối tượng dữ liệu dùng chung cho cả 3 tầng
├── filter/       ← SiteMesh, phân quyền, UTF-8
└── util/         ← hằng số, băm mật khẩu, gửi mail

src/main/webapp/WEB-INF/
├── decorators/   ← user.jsp + admin.jsp (02 vai trò) + _header.jsp + _footer.jsp
├── views/        ← các trang nội dung (VIEW)
├── sitemesh3.xml ← ánh xạ URL với decorator
└── web.xml
```

Controller **không bao giờ** gọi thẳng DAO; luôn đi qua Service. Mỗi tầng đều có
interface riêng (`IBookService_24133059`, `IBookDao_24133059`, …).

---

## 3. Bảng đối chiếu: câu hỏi → tệp nguồn

| Câu | Yêu cầu | Tệp chính |
|---|---|---|
| **1** (1.5đ) | Cấu trúc 3 tầng | Các gói `controller` / `service` / `dao` |
| | Tạo cấu trúc database | `database/01_schema.sql` |
| | SiteMesh 2 vai trò User/Admin | `WEB-INF/sitemesh3.xml`, `decorators/user.jsp`, `decorators/admin.jsp`, `filter/TomcatSiteMeshFilter_24133059.java` |
| | Header: Trang Chủ, Sản phẩm, Đăng nhập, Trang quản trị | `decorators/_header.jsp` |
| | Footer: Họ tên, MSSV, Mã đề | `decorators/_footer.jsp` |
| **2** (1.5đ) | Đăng ký + kích hoạt OTP qua mail | `RegisterServlet_24133059`, `VerifyOtpServlet_24133059`, `ResendOtpServlet_24133059`, `AuthService_24133059`, `MailUtil_24133059` |
| | Đăng nhập / Đăng xuất bằng Session | `LoginServlet_24133059`, `LogoutServlet_24133059` |
| | Sai thì quay lại trang đăng nhập | `LoginServlet_24133059.doPost()` |
| **3** (2đ) | Home: sách theo từng tác giả, 3 sp/trang | `HomeServlet_24133059`, `BookService_24133059.buildHomeSections()`, `views/home.jsp` |
| **4** (2đ) | Chi tiết sách + reviews + form thêm review | `BookDetailServlet_24133059`, `ReviewServlet_24133059`, `views/book-detail.jsp` |
| **6** (3đ) | CRUD bảng Books có phân trang | `AdminBookListServlet_24133059` (xem), `AdminBookAddServlet_24133059` (tạo), `AdminBookEditServlet_24133059` (cập nhật), `AdminBookDeleteServlet_24133059` (xóa), `views/admin/*` |

### Danh sách URL

| URL | Chức năng |
|---|---|
| `/home` | Câu 3 — trang chủ, sách theo từng tác giả |
| `/home?author=<id>&page=<n>` | Đổi trang cho riêng khối của một tác giả |
| `/products` | Menu "Sản phẩm" — toàn bộ sách, có phân trang |
| `/book?id=<id>` | Câu 4 — chi tiết sách |
| `/review` (POST) | Câu 4 — gửi nhận xét |
| `/register`, `/verify-otp`, `/resend-otp` | Câu 2 — đăng ký + OTP |
| `/login`, `/logout` | Câu 2 — đăng nhập / đăng xuất |
| `/admin/books` | Câu 6 — danh sách, có phân trang (5 dòng/trang) |
| `/admin/books/view|add|edit|delete` | Câu 6 — xem / tạo / cập nhật / xóa |
| `/image?name=<cover_image>` | Phát ảnh bìa |

---

## 3b. Bài tập tiếp theo (vai trò User): Giỏ hàng – COD – Lịch sử đơn hàng

Ba chức năng này làm thêm sau bài kiểm tra giữa kỳ, **không sửa gì vào 5 bảng
và 5 câu của đề thi**; chỉ thêm 2 bảng mới và các lớp mới.

| Chức năng | Tệp chính |
|---|---|
| **Giỏ hàng** – thêm, xóa, sửa số lượng trong giới hạn | `model/Cart_24133059` + `model/CartItem_24133059`, `service/CartService_24133059`, `controller/CartServlet` · `CartAddServlet` · `CartUpdateServlet` · `CartRemoveServlet`, `views/cart.jsp` |
| **Thanh toán COD** | `service/OrderService_24133059.placeCodOrder()`, `dao/OrderDao_24133059.insert()`, `controller/CheckoutServlet_24133059`, `views/checkout.jsp` |
| **Lịch sử đơn hàng lọc theo trạng thái** | `model/OrderStatus_24133059` (08 trạng thái), `controller/OrderHistoryServlet` · `OrderDetailServlet` · `OrderCancelServlet`, `views/orders.jsp` · `views/order-detail.jsp` |
| Hai bảng mới | `database/03_order_schema.sql` |
| Câu lệnh đổi trạng thái để demo | `database/04_order_status_demo.sql` |

### URL mới

| URL | Chức năng |
|---|---|
| `/cart` | Xem giỏ hàng |
| `/cart/add` (POST) | Thêm sách vào giỏ |
| `/cart/update` (POST) | Sửa số lượng một dòng |
| `/cart/remove` · `/cart/clear` (POST) | Xóa một dòng · xóa sạch giỏ |
| `/checkout` | Thanh toán COD |
| `/orders?status=<MÃ>&page=<n>` | Lịch sử đơn, lọc theo trạng thái |
| `/orders/detail?id=<order_id>` | Chi tiết một đơn |
| `/orders/cancel` (POST) | Khách tự hủy đơn còn mới |

### 08 trạng thái đơn hàng

`NEW` Đơn hàng mới · `CONFIRMED` Đã xác nhận · `PREPARING` Chuẩn bị hàng ·
`SHIPPING` Vận chuyển · `DELIVERING` Giao hàng · `DELIVERED` Đã giao ·
`CANCELLED` Đơn hàng hủy · `RETURNED` Đơn hàng hoàn.

Để xem đơn nhảy trạng thái: mở `database/04_order_status_demo.sql`, chạy câu
`UPDATE orders SET status = '<MÃ>' WHERE order_id = <số đơn>;` rồi F5 lại trang
**Đơn hàng của tôi** trên web.

### Giới hạn số lượng trong giỏ

Số lượng mỗi đầu sách bị chặn trong khoảng **1 … min(tồn kho, 20)**. Kiểm tra ở
cả hai nơi: thuộc tính `min`/`max` của ô nhập trên trình duyệt, và `CartService`
ở tầng Business (nơi quyết định thật sự). Đặt hàng xong thì tồn kho
`books.quantity` bị trừ đi; hủy đơn thì được cộng trả lại.

Giỏ đọc lại giá hiện tại khi xem/cập nhật và trước khi đặt hàng. Nếu giá hoặc
số lượng khả dụng vừa thay đổi, khách được yêu cầu kiểm tra lại trước khi
chốt COD. Hóa đơn đã đặt vẫn giữ tên và giá tại thời điểm mua. Các request
thanh toán đồng thời cùng một giỏ chỉ tạo được một đơn.

`05_seed_orders.sql` tạo dữ liệu mẫu trong transaction, trừ kho cho các đơn
`NEW` đến `DELIVERED` và bỏ qua mẫu đã có bằng dấu `[DEMO_24133059]` trong ghi
chú. Chạy lại script không xóa đơn thật và không trừ kho lần nữa. Script mới
không tự chuyển đổi các đơn mẫu đã tạo bằng phiên bản cũ.

### Kiểm thử các chức năng User

Chạy 16 ca kiểm thử nghiệp vụ, không cần Tomcat hoặc SQL Server:

```bash
mvn test
```

Để chạy thêm 5 ca kiểm thử tích hợp, deploy WAR mới lên Tomcat cục bộ, kết
nối database theo `database.properties`, rồi mở PowerShell:

```powershell
$env:BOOKSTORE_TEST_URL='http://localhost:8080/BookStore_24133059'
mvn test
Remove-Item Env:BOOKSTORE_TEST_URL
```

Database cần có schema `01_schema.sql` và `03_order_schema.sql`, tài khoản
mẫu `mai@bookstore.local` từ `02_seed.sql`. Test tạo tài khoản/sách riêng,
tự dọn các dòng thử; không cần chạy lại các script khởi tạo trên database
đã dùng. Kết quả lần kiểm tra 05/10/2026: **21/21 ca đạt**, gồm COD, giỏ
hàng, cả 8 trạng thái, phân trang và script đơn mẫu. Xem
[báo cáo kiểm thử](docs/kiem-thu-user-2026-10-05.md).

---

## 4. Những chỗ đã cân nhắc kỹ

**Kiểu `text` đổi thành `VARCHAR(MAX)`.** Đề vẽ `description` và `review_text` kiểu
`text`. Database được tạo với collation UTF-8 (`Vietnamese_100_CI_AS_SC_UTF8`) để các
cột `varchar` mà đề quy định vẫn lưu được tiếng Việt có dấu; SQL Server không cho kiểu
LOB cũ (`text`, `ntext`) nằm trong database UTF-8 (lỗi 4188). `VARCHAR(MAX)` chính là
kiểu thay thế mà Microsoft khuyến nghị cho `text`. Các cột còn lại giữ nguyên 100% tên
và kiểu như trong đề.

**Giá lưu theo đơn vị nghìn đồng.** Đề quy định `books.price` kiểu `decimal(6,2)`, trần
chỉ 9999.99 — không đủ chứa giá sách tính bằng đồng (một cuốn 95.000đ đã vượt trần). Vì
không được sửa cấu trúc bảng, giá trị trong cột được hiểu là **nghìn đồng**: `95.00` hiển
thị thành `95.000 ₫`, `78.50` thành `78.500 ₫`, trần 9999.99 tương đương 9.999.990 ₫.
Toàn bộ việc quy đổi và định dạng nằm ở `util/MoneyUtil_24133059.java`; form thêm/sửa
sách ghi rõ đơn vị ngay cạnh ô nhập.

**Mật khẩu băm MD5.** Cột `passwd` đề quy định `varchar(32)` — vừa đúng 32 ký tự hex của
MD5. BCrypt (60 ký tự) hay SHA-256 (64 ký tự) sẽ không vừa cột.

**Mã OTP lưu trong Session, không lưu trong database.** Bảng `users` của đề không có cột
nào cho OTP hay trạng thái kích hoạt, mà đề yêu cầu tạo database *đúng như sơ đồ*. Vì vậy
hồ sơ đăng ký + mã OTP được giữ tạm trong Session; chỉ khi nhập đúng mã thì mới `INSERT`
vào bảng `users`. Cách này vừa giữ đúng schema, vừa bảo đảm tài khoản chưa kích hoạt
không tồn tại trong database.

**SiteMesh chỉ chạy ở `<dispatcher>FORWARD</dispatcher>`.** Mọi trang đều do Servlet xử
lý rồi forward sang JSP. Trên Tomcat 10/11, khi lệnh forward kết thúc thì response bị
đánh dấu "đã gửi xong" nên mọi thứ ghi thêm sau đó đều bị bỏ. Nếu để SiteMesh chạy ở
`REQUEST` (bọc bên ngoài lần forward) thì trình duyệt nhận HTTP 200 nhưng dài 0 byte —
tức là trang trắng. Đặt ở `FORWARD` nghĩa là SiteMesh chạy *bên trong* lần forward, ghép
xong trang trước khi forward kết thúc. Cùng lý do đó, `TomcatSiteMeshFilter_24133059`
dựng decorator bằng `include()` và tự hứng nội dung thay vì dùng `forward()` như mặc định
của SiteMesh 3.2.1.

**`<jsp-config>` ép UTF-8 cho mọi file .jsp.** Hai fragment `_header.jsp` và `_footer.jsp`
được nhúng tĩnh bằng `<%@ include %>` nên không có chỉ thị `pageEncoding` riêng; thiếu khai
báo này Jasper đọc chúng theo ISO-8859-1 và menu, footer bị vỡ font tiếng Việt.

**Ảnh bìa.** Mọi trang JSP đều gọi `/image?name=<cover_image>`; `ImageServlet_24133059`
tự tìm theo thứ tự: URL http(s) → file admin tải lên trong `D:\WEB\uploads\BookStore_24133059`
→ ảnh mẫu trong `assets/covers/`. Nhờ vậy View không phải quan tâm ảnh nằm ở đâu.

**Nguồn ảnh bìa và chạy offline.** Bộ dữ liệu mẫu dùng 47 ảnh WebP đúng tên sách,
lưu sẵn trong `assets/covers/` (mỗi ảnh dưới 80 KB, tổng khoảng 700 KB).
Ba sách số 12, 36 và 38 giữ bìa SVG vì chưa xác minh được ảnh bìa phù hợp.
Nguồn cho từng ảnh được ghi trong [`assets/covers/SOURCES.md`](src/main/webapp/assets/covers/SOURCES.md).
Các file SVG cũ được giữ làm ảnh dự phòng; bộ dữ liệu mẫu cùng CSS, font và
JavaScript không cần kết nối Internet.

**Giao diện và thao tác.** Trang chủ giữ 3 sách mỗi tác giả trên một trang.
Sản phẩm và quản trị có phân trang rút gọn khi số trang lớn; form hiện lỗi cạnh
ô nhập và giữ lại dữ liệu hợp lệ khi cần sửa. Tệp `assets/app.js` chỉ bổ sung
xác nhận trước khi xóa sách; các form và đường dẫn vẫn hoạt động khi tắt JavaScript.

---

## 5. Nộp bài

Nén cả thư mục project (`BookStore_24133059`) thành `24133059_02.zip` rồi nộp kèm file
`24133059.docx` chứa ảnh chụp toàn màn hình kết quả từng câu.

Có thể xóa thư mục `target/` trước khi nén cho nhẹ.

> **Trước khi nén, cân nhắc file `src/main/resources/email.properties`.** File này đang
> chứa App Password thật của tài khoản Gmail dùng để gửi OTP. Giữ nguyên thì người chấm
> mở ra là chạy được ngay chức năng OTP của Câu 2, nhưng đồng nghĩa mật khẩu ứng dụng đó
> nằm trong bài nộp. Nếu không muốn, hãy xoá giá trị hai dòng `mail.username` và
> `mail.password` trước khi nén — chương trình vẫn chạy, chỉ là mã OTP in ra Console
> thay vì gửi email.
