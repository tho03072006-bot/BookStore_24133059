/* =====================================================================
   BÀI TẬP TIẾP THEO (vai trò User): Giỏ hàng - Thanh toán COD -
   Lịch sử đặt hàng lọc theo trạng thái.
   Sinh viên: Trần Minh Thọ - MSSV: 24133059

   Chạy file này SAU 01_schema.sql và 02_seed.sql.

   File chỉ THÊM 02 bảng mới (orders, order_detail). Năm bảng của đề thi
   giữa kỳ (author, books, book_author, users, rating) giữ nguyên 100%,
   không sửa một cột nào.

   GIỎ HÀNG không có bảng riêng: giỏ nằm trong Session của từng người
   dùng, chỉ khi bấm "Đặt hàng" mới ghi xuống database thành 1 dòng
   orders + n dòng order_detail.
   ===================================================================== */

USE BookStore;
GO


/* ---------------------------------------------------------------------
   Bảng orders - mỗi dòng là một đơn hàng.

   status nhận đúng 08 giá trị, tương ứng 08 trạng thái đề bài yêu cầu:

       NEW         Đơn hàng mới
       CONFIRMED   Đã xác nhận
       PREPARING   Chuẩn bị hàng
       SHIPPING    Vận chuyển
       DELIVERING  Giao hàng
       DELIVERED   Đã giao
       CANCELLED   Đơn hàng hủy
       RETURNED    Đơn hàng hoàn

   Dùng mã tiếng Anh không dấu để khi gõ tay trong SSMS không sợ sai dấu;
   giao diện tự đổi sang nhãn tiếng Việt. Ràng buộc CHECK bên dưới chặn
   mọi giá trị lạ. Xem file 04_order_status_demo.sql để biết câu lệnh
   đổi trạng thái sẵn dùng.
   --------------------------------------------------------------------- */
IF OBJECT_ID('dbo.orders', 'U') IS NULL
BEGIN
CREATE TABLE orders (
    order_id       INT IDENTITY(1,1) NOT NULL,
    userid         INT               NOT NULL,
    order_date     DATETIME          NOT NULL CONSTRAINT DF_orders_date   DEFAULT GETDATE(),
    receiver_name  NVARCHAR(50)      NOT NULL,
    receiver_phone VARCHAR(15)       NOT NULL,
    address        NVARCHAR(200)     NOT NULL,
    note           NVARCHAR(200)         NULL,
    payment_method VARCHAR(10)       NOT NULL CONSTRAINT DF_orders_pay    DEFAULT 'COD',
    status         VARCHAR(12)       NOT NULL CONSTRAINT DF_orders_status DEFAULT 'NEW',
    total_amount   DECIMAL(12,2)     NOT NULL,
    CONSTRAINT PK_orders PRIMARY KEY (order_id),
    CONSTRAINT FK_orders_users FOREIGN KEY (userid)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT CK_orders_status CHECK (status IN
        ('NEW', 'CONFIRMED', 'PREPARING', 'SHIPPING',
         'DELIVERING', 'DELIVERED', 'CANCELLED', 'RETURNED')),
    CONSTRAINT CK_orders_payment CHECK (payment_method IN ('COD')),
    CONSTRAINT CK_orders_total CHECK (total_amount >= 0)
);
CREATE INDEX IX_orders_user_status ON orders (userid, status);
END;
GO

/* ---------------------------------------------------------------------
   Bảng order_detail - các dòng sách trong một đơn hàng.

   title và price được CHÉP LẠI tại thời điểm đặt hàng, không đọc sang
   bảng books lúc hiển thị. Nhờ vậy khi admin sửa giá hoặc xoá sách
   (chức năng CRUD của Câu 6) thì hoá đơn cũ vẫn giữ nguyên giá và tên
   sách lúc khách mua.

   Vì lý do đó bookid để NULL được và dùng ON DELETE SET NULL: xoá sách
   thì dòng hoá đơn vẫn còn, chỉ mất đường dẫn sang trang chi tiết. Nếu
   dùng ON DELETE CASCADE thì xoá sách sẽ làm bay luôn lịch sử mua hàng.
   --------------------------------------------------------------------- */
IF OBJECT_ID('dbo.order_detail', 'U') IS NULL
BEGIN
CREATE TABLE order_detail (
    detail_id INT IDENTITY(1,1) NOT NULL,
    order_id  INT               NOT NULL,
    bookid    INT                   NULL,
    title     NVARCHAR(200)     NOT NULL,
    price     DECIMAL(6,2)      NOT NULL,
    quantity  INT               NOT NULL,
    CONSTRAINT PK_order_detail PRIMARY KEY (detail_id),
    CONSTRAINT FK_od_orders FOREIGN KEY (order_id)
        REFERENCES orders(order_id) ON DELETE CASCADE,
    CONSTRAINT FK_od_books FOREIGN KEY (bookid)
        REFERENCES books(bookid) ON DELETE SET NULL,
    CONSTRAINT CK_od_quantity CHECK (quantity > 0),
    CONSTRAINT CK_od_price CHECK (price >= 0)
);
CREATE INDEX IX_order_detail_order ON order_detail (order_id);
END;
GO

PRINT 'Da tao xong 2 bang moi: orders, order_detail.';
GO
