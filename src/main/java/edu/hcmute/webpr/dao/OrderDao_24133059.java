package edu.hcmute.webpr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.hcmute.webpr.model.OrderDetail_24133059;
import edu.hcmute.webpr.model.OrderStatus_24133059;
import edu.hcmute.webpr.model.Order_24133059;

/**
 * TẦNG DATA ACCESS - hiện thực truy xuất {@code orders} / {@code order_detail}
 * bằng JDBC thuần.
 *
 * Trần Minh Thọ - 24133059
 */
public class OrderDao_24133059 implements IOrderDao_24133059 {

    private final JDBCConnect_24133059 jdbc = new JDBCConnect_24133059();

    private static final String ORDER_COLUMNS =
            "o.order_id, o.userid, o.order_date, o.receiver_name, o.receiver_phone, "
            + "o.address, o.note, o.payment_method, o.status, o.total_amount";

    // ------------------------------------------------------------------
    // Đặt hàng
    // ------------------------------------------------------------------

    @Override
    public int insert(Order_24133059 order) {
        String insertOrder = "INSERT INTO orders (userid, order_date, receiver_name, "
                + "receiver_phone, address, note, payment_method, status, total_amount) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        String insertDetail = "INSERT INTO order_detail (order_id, bookid, title, price, quantity) "
                + "VALUES (?,?,?,?,?)";
        // Điều kiện quantity >= ? ngay trong câu UPDATE: nếu hai người cùng mua
        // cuốn cuối cùng thì người sau không update được dòng nào và cả đơn bị
        // huỷ bỏ, thay vì để tồn kho tụt xuống số âm.
        String reduceStock = "UPDATE books SET quantity = quantity - ? "
                + "WHERE bookid = ? AND quantity >= ?";

        Connection conn = null;
        try {
            conn = jdbc.getConnection();
            conn.setAutoCommit(false);

            int newId;
            try (PreparedStatement ps = conn.prepareStatement(insertOrder,
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getUserId());
                ps.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
                ps.setString(3, order.getReceiverName());
                ps.setString(4, order.getReceiverPhone());
                ps.setString(5, order.getAddress());
                ps.setString(6, order.getNote());
                ps.setString(7, order.getPaymentMethod());
                ps.setString(8, order.getStatus().getCode());
                ps.setBigDecimal(9, order.getTotalAmount());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Không lấy được order_id vừa sinh ra");
                    }
                    newId = keys.getInt(1);
                }
            }

            try (PreparedStatement psDetail = conn.prepareStatement(insertDetail);
                 PreparedStatement psStock = conn.prepareStatement(reduceStock)) {

                for (OrderDetail_24133059 detail : order.getDetails()) {
                    psDetail.setInt(1, newId);
                    if (detail.getBookId() == null) {
                        psDetail.setNull(2, Types.INTEGER);
                    } else {
                        psDetail.setInt(2, detail.getBookId());
                    }
                    psDetail.setString(3, detail.getTitle());
                    psDetail.setBigDecimal(4, detail.getPrice());
                    psDetail.setInt(5, detail.getQuantity());
                    psDetail.addBatch();

                    if (detail.getBookId() != null) {
                        psStock.setInt(1, detail.getQuantity());
                        psStock.setInt(2, detail.getBookId());
                        psStock.setInt(3, detail.getQuantity());
                        if (psStock.executeUpdate() == 0) {
                            throw new IllegalStateException("Sách \"" + detail.getTitle()
                                    + "\" không còn đủ số lượng trong kho.");
                        }
                    }
                }
                psDetail.executeBatch();
            }

            conn.commit();
            return newId;

        } catch (IllegalStateException e) {
            rollback(conn);
            throw e;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Lỗi khi lưu đơn hàng", e);
        } finally {
            close(conn);
        }
    }

    // ------------------------------------------------------------------
    // Lịch sử đặt hàng
    // ------------------------------------------------------------------

    @Override
    public List<Order_24133059> findByUser(int userId, OrderStatus_24133059 status,
                                           int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT ").append(ORDER_COLUMNS)
                .append(", (SELECT COUNT(*) FROM order_detail d WHERE d.order_id = o.order_id) AS so_dong")
                .append(" FROM orders o WHERE o.userid = ?");
        if (status != null) {
            sql.append(" AND o.status = ?");
        }
        sql.append(" ORDER BY o.order_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        List<Order_24133059> list = new ArrayList<>();
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, userId);
            if (status != null) {
                ps.setString(i++, status.getCode());
            }
            ps.setInt(i++, Math.max(offset, 0));
            ps.setInt(i, Math.max(limit, 1));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
            attachDetails(conn, list);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc lịch sử đơn hàng", e);
        }
        return list;
    }

    @Override
    public int countByUser(int userId, OrderStatus_24133059 status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE userid = ?"
                + (status == null ? "" : " AND status = ?");
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            if (status != null) {
                ps.setString(2, status.getCode());
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm đơn hàng", e);
        }
    }

    @Override
    public Map<String, Integer> countGroupedByStatus(int userId) {
        String sql = "SELECT status, COUNT(*) FROM orders WHERE userid = ? GROUP BY status";
        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm đơn hàng theo trạng thái", e);
        }
        return result;
    }

    @Override
    public Order_24133059 findByIdAndUser(int orderId, int userId) {
        String sql = "SELECT " + ORDER_COLUMNS + ", 0 AS so_dong FROM orders o "
                + "WHERE o.order_id = ? AND o.userid = ?";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Order_24133059 order = mapOrder(rs);
                attachDetails(conn, List.of(order));
                return order;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc chi tiết đơn hàng", e);
        }
    }

    // ------------------------------------------------------------------
    // Hủy đơn
    // ------------------------------------------------------------------

    @Override
    public boolean cancelByUser(int orderId, int userId) {
        // Điều kiện status = 'NEW' nằm ngay trong câu UPDATE để hai lần bấm
        // liên tiếp không trả hàng về kho hai lần.
        String cancel = "UPDATE orders SET status = 'CANCELLED' "
                + "WHERE order_id = ? AND userid = ? AND status = 'NEW'";
        // Cú pháp UPDATE ... FROM của SQL Server: khi bảng đích có bí danh thì
        // phải cập nhật qua chính bí danh đó (UPDATE b), và cột quantity có ở
        // cả hai bảng nên bắt buộc ghi rõ b.quantity / d.quantity.
        String restoreStock = "UPDATE b SET b.quantity = b.quantity + d.quantity "
                + "FROM books b JOIN order_detail d ON d.bookid = b.bookid "
                + "WHERE d.order_id = ?";

        Connection conn = null;
        try {
            conn = jdbc.getConnection();
            conn.setAutoCommit(false);

            int rows;
            try (PreparedStatement ps = conn.prepareStatement(cancel)) {
                ps.setInt(1, orderId);
                ps.setInt(2, userId);
                rows = ps.executeUpdate();
            }
            if (rows == 0) {
                conn.rollback();
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(restoreStock)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }
            conn.commit();
            return true;

        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Lỗi khi hủy đơn hàng", e);
        } finally {
            close(conn);
        }
    }

    // ------------------------------------------------------------------
    // Hàm dùng chung trong nội bộ DAO
    // ------------------------------------------------------------------

    /**
     * Nạp các dòng sách cho nhiều đơn bằng MỘT câu lệnh IN (...) thay vì chạy
     * một query cho mỗi đơn (tránh lỗi N+1 query).
     */
    private void attachDetails(Connection conn, List<Order_24133059> orders) throws SQLException {
        if (orders.isEmpty()) {
            return;
        }
        Map<Integer, Order_24133059> byId = new LinkedHashMap<>();
        StringBuilder placeholders = new StringBuilder();
        for (Order_24133059 order : orders) {
            byId.put(order.getOrderId(), order);
            placeholders.append(placeholders.length() == 0 ? "?" : ",?");
        }

        String sql = "SELECT d.detail_id, d.order_id, d.bookid, d.title, d.price, d.quantity, "
                + "       b.cover_image "
                + "FROM order_detail d LEFT JOIN books b ON b.bookid = d.bookid "
                + "WHERE d.order_id IN (" + placeholders + ") "
                + "ORDER BY d.detail_id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            for (Integer id : byId.keySet()) {
                ps.setInt(i++, id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order_24133059 order = byId.get(rs.getInt("order_id"));
                    if (order != null) {
                        order.getDetails().add(mapDetail(rs));
                    }
                }
            }
        }
    }

    private Order_24133059 mapOrder(ResultSet rs) throws SQLException {
        Order_24133059 order = new Order_24133059();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("userid"));
        Timestamp date = rs.getTimestamp("order_date");
        order.setOrderDate(date == null ? null : date.toLocalDateTime());
        order.setReceiverName(rs.getString("receiver_name"));
        order.setReceiverPhone(rs.getString("receiver_phone"));
        order.setAddress(rs.getString("address"));
        order.setNote(rs.getString("note"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setStatus(OrderStatus_24133059.fromCode(rs.getString("status")));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        return order;
    }

    private OrderDetail_24133059 mapDetail(ResultSet rs) throws SQLException {
        OrderDetail_24133059 detail = new OrderDetail_24133059();
        detail.setDetailId(rs.getInt("detail_id"));
        detail.setOrderId(rs.getInt("order_id"));
        int bookId = rs.getInt("bookid");
        detail.setBookId(rs.wasNull() ? null : bookId);
        detail.setTitle(rs.getString("title"));
        detail.setPrice(rs.getBigDecimal("price"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setCoverImage(rs.getString("cover_image"));
        return detail;
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                // Không che lỗi gốc bằng lỗi rollback.
            }
        }
    }

    private void close(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
                // Kết nối sẽ được JVM thu hồi, không cần xử lý thêm.
            }
        }
    }
}
