/* =====================================================================
   DỮ LIỆU MẪU CHO LỊCH SỬ ĐẶT HÀNG
   Sinh viên: Trần Minh Thọ - MSSV: 24133059

   Chạy sau 03_order_schema.sql. File sinh 08 đơn hàng mẫu cho tài khoản
   user@bookstore.local, mỗi đơn một trạng thái khác nhau, để mở trang
   "Đơn hàng của tôi" là bấm thử được ngay cả 08 bộ lọc mà không phải tự
   đặt tay 8 lần.

   Chạy lại không tạo trùng 8 đơn mẫu và không xoá lịch sử đặt hàng thật.

   Các đơn còn hiệu lực (NEW đến DELIVERED) trừ kho như đơn đặt thật.
   CANCELLED/RETURNED không giữ hàng. Toàn bộ chạy trong transaction.
   ===================================================================== */

USE BookStore;
GO

DECLARE @uid INT = (SELECT id FROM users WHERE email = 'user@bookstore.local');

IF @uid IS NULL
BEGIN
    RAISERROR(N'Khong tim thay tai khoan user@bookstore.local. Hay chay 02_seed.sql truoc.', 16, 1);
    RETURN;
END

SET XACT_ABORT ON;
BEGIN TRY
BEGIN TRANSACTION;

DECLARE @i INT = 1;
DECLARE @oid INT, @trang_thai VARCHAR(12);
DECLARE @b1 INT, @b2 INT;
DECLARE @t1 NVARCHAR(200), @t2 NVARCHAR(200);
DECLARE @g1 DECIMAL(6,2), @g2 DECIMAL(6,2);
DECLARE @marker NVARCHAR(200);

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

    SET @marker = N'[DEMO_24133059] ' + @trang_thai;
    IF EXISTS (SELECT 1 FROM orders WITH (UPDLOCK, HOLDLOCK)
               WHERE userid = @uid AND note = @marker)
    BEGIN
        SET @i = @i + 1;
        CONTINUE;
    END;

    SET @b1 = @i;          -- sách thứ nhất của đơn
    SET @b2 = @i + 20;     -- sách thứ hai, lấy của tác giả khác cho đa dạng

    SET @t1 = NULL; SET @t2 = NULL; SET @g1 = NULL; SET @g2 = NULL;
    SELECT @t1 = title, @g1 = price FROM books WITH (UPDLOCK) WHERE bookid = @b1;
    SELECT @t2 = title, @g2 = price FROM books WITH (UPDLOCK) WHERE bookid = @b2;
    IF @t1 IS NULL OR @t2 IS NULL OR @g1 IS NULL OR @g2 IS NULL
        THROW 50001, N'Thieu sach mau. Hay kiem tra 02_seed.sql.', 1;

    IF @trang_thai NOT IN ('CANCELLED', 'RETURNED')
    BEGIN
        UPDATE books SET quantity = quantity - 2 WHERE bookid = @b1 AND quantity >= 2;
        IF @@ROWCOUNT <> 1 THROW 50002, N'Khong du ton kho cho don mau.', 1;
        UPDATE books SET quantity = quantity - 1 WHERE bookid = @b2 AND quantity >= 1;
        IF @@ROWCOUNT <> 1 THROW 50002, N'Khong du ton kho cho don mau.', 1;
    END;

    INSERT INTO orders (userid, order_date, receiver_name, receiver_phone,
                        address, note, payment_method, status, total_amount)
    VALUES (@uid,
            DATEADD(DAY, -@i, GETDATE()),
            N'Nguyễn Văn An',
            '0912345678',
            N'1 Võ Văn Ngân, phường Linh Chiểu, TP. Thủ Đức, TP. Hồ Chí Minh',
            @marker,
            'COD',
            @trang_thai,
            @g1 * 2 + @g2);

    SET @oid = SCOPE_IDENTITY();

    INSERT INTO order_detail (order_id, bookid, title, price, quantity) VALUES
        (@oid, @b1, @t1, @g1, 2),
        (@oid, @b2, @t2, @g2, 1);

    SET @i = @i + 1;
END;
COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
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
