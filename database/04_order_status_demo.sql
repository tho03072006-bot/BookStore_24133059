/* Đổi trạng thái qua database để kiểm tra 8 bộ lọc.
   Trần Minh Thọ - 24133059. Chạy sau 03_order_schema.sql.
   Sửa @orderId và @newStatus bên dưới; mặc định NULL nên không đổi đơn nào.
   Dùng đơn mới đặt qua web hoặc mẫu từ 05_seed_orders.sql bản hiện tại.
   NEW / CONFIRMED / PREPARING / SHIPPING / DELIVERING / DELIVERED:
   đơn giữ số lượng đã trừ kho. CANCELLED / RETURNED: trả lại kho.
   Chạy lại cùng trạng thái không trừ/trả kho thêm lần nữa. */
USE BookStore;
GO
SELECT o.order_id, u.email, o.order_date, o.status, o.total_amount
FROM orders o JOIN users u ON u.id = o.userid
ORDER BY o.order_id DESC;
GO

DECLARE @orderId INT = NULL; -- Điền mã đơn của mình, ví dụ 1.
DECLARE @newStatus VARCHAR(12) = 'CONFIRMED'; -- Chọn một mã trong 8 mã ở trên.

IF @orderId IS NOT NULL
BEGIN
    SET XACT_ABORT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        IF @newStatus NOT IN ('NEW','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED')
            THROW 50001, 'Ma trang thai khong hop le.', 1;
        DECLARE @oldStatus VARCHAR(12);
        SELECT @oldStatus = status FROM orders WITH (UPDLOCK, ROWLOCK) WHERE order_id = @orderId;
        IF @oldStatus IS NULL THROW 50002, 'Khong tim thay don hang.', 1;
        IF @oldStatus NOT IN ('CANCELLED','RETURNED') AND @newStatus IN ('CANCELLED','RETURNED')
        BEGIN
            UPDATE b SET b.quantity = ISNULL(b.quantity,0) + d.quantity
            FROM books b JOIN (
                SELECT bookid, SUM(quantity) AS quantity FROM order_detail
                WHERE order_id = @orderId AND bookid IS NOT NULL GROUP BY bookid
            ) d ON d.bookid = b.bookid;
        END;
        IF @oldStatus IN ('CANCELLED','RETURNED') AND @newStatus NOT IN ('CANCELLED','RETURNED')
        BEGIN
            DECLARE @lines INT = (SELECT COUNT(DISTINCT bookid) FROM order_detail WHERE order_id = @orderId);
            UPDATE b SET b.quantity = b.quantity - d.quantity
            FROM books b JOIN (
                SELECT bookid, SUM(quantity) AS quantity FROM order_detail
                WHERE order_id = @orderId AND bookid IS NOT NULL GROUP BY bookid
            ) d ON d.bookid = b.bookid WHERE b.quantity >= d.quantity;
            IF @@ROWCOUNT <> @lines THROW 50003, 'Khong du ton kho de khoi phuc don.', 1;
        END;
        UPDATE orders SET status = @newStatus WHERE order_id = @orderId;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO
SELECT status, COUNT(*) AS so_don FROM orders GROUP BY status ORDER BY status;
GO
-- F5 trang Đơn hàng của tôi để quan sát nhãn, bộ lọc và tiến trình mới.
