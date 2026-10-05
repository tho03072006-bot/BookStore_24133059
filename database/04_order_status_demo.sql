/* =====================================================================
   ĐỔI TRẠNG THÁI ĐƠN HÀNG NGAY TRONG DATABASE
   Sinh viên: Trần Minh Thọ - MSSV: 24133059

   Đề bài yêu cầu "vào database để thay đổi các trạng thái để quan sát
   trạng thái đơn thay đổi theo trạng thái tương ứng". File này gom sẵn
   các câu lệnh đó, chỉ cần sửa số đơn rồi chạy, sau đó F5 lại trang
   "Đơn hàng của tôi" trên web là thấy đơn nhảy sang tab mới.

   08 trạng thái và nhãn hiển thị tương ứng trên giao diện:

       NEW         ->  Đơn hàng mới
       CONFIRMED   ->  Đã xác nhận
       PREPARING   ->  Chuẩn bị hàng
       SHIPPING    ->  Vận chuyển
       DELIVERING  ->  Giao hàng
       DELIVERED   ->  Đã giao
       CANCELLED   ->  Đơn hàng hủy
       RETURNED    ->  Đơn hàng hoàn
   ===================================================================== */

USE BookStore;
GO

/* --- 1. Xem các đơn đang có, để biết order_id cần sửa --------------- */
SELECT o.order_id,
       u.email,
       o.order_date,
       o.status,
       o.total_amount,
       (SELECT COUNT(*) FROM order_detail d WHERE d.order_id = o.order_id) AS so_dong
FROM orders o
JOIN users u ON u.id = o.userid
ORDER BY o.order_id DESC;
GO

/* --- 2. Đổi trạng thái MỘT đơn: sửa số 1 thành order_id cần đổi ----- */
-- UPDATE orders SET status = 'CONFIRMED'  WHERE order_id = 1;   -- Đã xác nhận
-- UPDATE orders SET status = 'PREPARING'  WHERE order_id = 1;   -- Chuẩn bị hàng
-- UPDATE orders SET status = 'SHIPPING'   WHERE order_id = 1;   -- Vận chuyển
-- UPDATE orders SET status = 'DELIVERING' WHERE order_id = 1;   -- Giao hàng
-- UPDATE orders SET status = 'DELIVERED'  WHERE order_id = 1;   -- Đã giao
-- UPDATE orders SET status = 'CANCELLED'  WHERE order_id = 1;   -- Đơn hàng hủy
-- UPDATE orders SET status = 'RETURNED'   WHERE order_id = 1;   -- Đơn hàng hoàn
-- UPDATE orders SET status = 'NEW'        WHERE order_id = 1;   -- Đơn hàng mới

/* --- 3. Rải mỗi đơn một trạng thái khác nhau để xem đủ 8 bộ lọc -----
   Chạy khối này khi đã đặt từ 8 đơn trở lên: nó gán lần lượt 8 trạng
   thái cho 8 đơn mới nhất của tài khoản user@bookstore.local.         */
/*
WITH don AS (
    SELECT o.order_id,
           ROW_NUMBER() OVER (ORDER BY o.order_id DESC) AS thu_tu
    FROM orders o
    JOIN users u ON u.id = o.userid
    WHERE u.email = 'user@bookstore.local'
)
UPDATE o
SET o.status = CASE d.thu_tu
        WHEN 1 THEN 'NEW'
        WHEN 2 THEN 'CONFIRMED'
        WHEN 3 THEN 'PREPARING'
        WHEN 4 THEN 'SHIPPING'
        WHEN 5 THEN 'DELIVERING'
        WHEN 6 THEN 'DELIVERED'
        WHEN 7 THEN 'CANCELLED'
        WHEN 8 THEN 'RETURNED'
        ELSE o.status
    END
FROM orders o
JOIN don d ON d.order_id = o.order_id
WHERE d.thu_tu <= 8;
*/

/* --- 4. Đếm số đơn theo từng trạng thái ----------------------------- */
SELECT status, COUNT(*) AS so_don
FROM orders
GROUP BY status
ORDER BY status;
GO
