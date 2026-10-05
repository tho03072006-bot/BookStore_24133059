package edu.hcmute.webpr.dao;

import java.util.List;
import java.util.Map;

import edu.hcmute.webpr.model.OrderStatus_24133059;
import edu.hcmute.webpr.model.Order_24133059;

/**
 * TẦNG DATA ACCESS - hợp đồng truy xuất hai bảng {@code orders} và
 * {@code order_detail}.
 *
 * Trần Minh Thọ - 24133059
 */
public interface IOrderDao_24133059 {

    /**
     * Ghi một đơn hàng mới: thêm dòng orders, thêm các dòng order_detail và
     * trừ tồn kho trong cùng MỘT transaction.
     *
     * @return order_id vừa sinh ra
     * @throws IllegalStateException nếu có cuốn không còn đủ hàng
     */
    int insert(Order_24133059 order);

    /** Một trang đơn hàng của người dùng, lọc theo trạng thái nếu có. */
    List<Order_24133059> findByUser(int userId, OrderStatus_24133059 status,
                                    int offset, int limit);

    int countByUser(int userId, OrderStatus_24133059 status);

    /** Số đơn theo từng trạng thái, dùng cho con số trên các tab bộ lọc. */
    Map<String, Integer> countGroupedByStatus(int userId);

    /** Chi tiết một đơn, kèm các dòng sách. Trả về null nếu đơn không thuộc user. */
    Order_24133059 findByIdAndUser(int orderId, int userId);

    /**
     * Khách tự hủy đơn của mình. Chỉ hủy được khi đơn còn ở trạng thái NEW;
     * số lượng đã trừ được cộng trả lại tồn kho.
     *
     * @return true nếu hủy được
     */
    boolean cancelByUser(int orderId, int userId);
}
