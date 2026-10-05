/* =====================================================================
   DỮ LIỆU MẪU CHO LỊCH SỬ ĐẶT HÀNG
   Sinh viên: Trần Minh Thọ - MSSV: 24133059

   Chạy sau 03_order_schema.sql. File sinh 08 đơn hàng mẫu cho tài khoản
   user@bookstore.local, mỗi đơn một trạng thái khác nhau, để mở trang
   "Đơn hàng của tôi" là bấm thử được ngay cả 08 bộ lọc mà không phải tự
   đặt tay 8 lần.

   Chạy lại nhiều lần vẫn ra đúng 8 đơn (xoá đơn cũ của tài khoản đó trước).

   File này KHÔNG đụng tới cột books.quantity: đây là đơn lịch sử thêm
   thẳng vào database, không phải đơn đặt qua giao diện, nên tồn kho giữ
   nguyên như trong 02_seed.sql.
   ===================================================================== */

USE BookStore;
GO

DECLARE @uid INT = (SELECT id FROM users WHERE email = 'user@bookstore.local');

IF @uid IS NULL
BEGIN
    RAISERROR(N'Khong tim thay tai khoan user@bookstore.local. Hay chay 02_seed.sql truoc.', 16, 1);
    RETURN;
END

/* Xoá đơn cũ của tài khoản này; order_detail tự xoá theo ON DELETE CASCADE. */
DELETE FROM orders WHERE userid = @uid;

DECLARE @i INT = 1;
DECLARE @oid INT, @trang_thai VARCHAR(12);
DECLARE @b1 INT, @b2 INT;
DECLARE @t1 NVARCHAR(200), @t2 NVARCHAR(200);
DECLARE @g1 DECIMAL(6,2), @g2 DECIMAL(6,2);

WHILE @i <= 8
BEGIN
    SET @trang_thai = CASE @i
        WHEN 1 THEN 'NEW'
        WHEN 2 THEN 'CONFIRMED'
        WHEN 3 THEN 'PREPARING'
        WHEN 4 THEN 'SHIPPING'
        WHEN 5 THEN 'DELIVERING'
        WHEN 6 THEN 'DELIVERED'
        WHEN 7 THEN 'CANCELLED'
        ELSE 'RETURNED'
    END;

    SET @b1 = @i;          -- sách thứ nhất của đơn
    SET @b2 = @i + 20;     -- sách thứ hai, lấy của tác giả khác cho đa dạng

    SELECT @t1 = title, @g1 = price FROM books WHERE bookid = @b1;
    SELECT @t2 = title, @g2 = price FROM books WHERE bookid = @b2;

    INSERT INTO orders (userid, order_date, receiver_name, receiver_phone,
                        address, note, payment_method, status, total_amount)
    VALUES (@uid,
            DATEADD(DAY, -@i, GETDATE()),
            N'Nguyễn Văn An',
            '0912345678',
            N'1 Võ Văn Ngân, phường Linh Chiểu, TP. Thủ Đức, TP. Hồ Chí Minh',
            CASE WHEN @i % 3 = 0 THEN N'Giao trong giờ hành chính' ELSE NULL END,
            'COD',
            @trang_thai,
            @g1 * 2 + @g2);

    SET @oid = SCOPE_IDENTITY();

    INSERT INTO order_detail (order_id, bookid, title, price, quantity) VALUES
        (@oid, @b1, @t1, @g1, 2),
        (@oid, @b2, @t2, @g2, 1);

    SET @i = @i + 1;
END
GO

SELECT o.order_id, o.status, o.total_amount, o.order_date,
       (SELECT COUNT(*) FROM order_detail d WHERE d.order_id = o.order_id) AS so_dong
FROM orders o
JOIN users u ON u.id = o.userid
WHERE u.email = 'user@bookstore.local'
ORDER BY o.order_id;
GO

PRINT 'Da tao 8 don hang mau, moi don mot trang thai.';
GO
