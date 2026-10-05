package edu.hcmute.webpr.service;

import java.util.Map;

import edu.hcmute.webpr.model.Cart_24133059;
import edu.hcmute.webpr.model.OrderStatus_24133059;
import edu.hcmute.webpr.model.Order_24133059;
import edu.hcmute.webpr.model.PageResult_24133059;
import edu.hcmute.webpr.model.User_24133059;

/**
 * TẦNG BUSINESS - nghiệp vụ đặt hàng COD và lịch sử đơn hàng.
 *
 * Trần Minh Thọ - 24133059
 */
public interface IOrderService_24133059 {

    /**
     * Chốt giỏ hàng thành một đơn COD. Giỏ hàng được xóa sạch sau khi đặt
     * thành công.
     *
     * @return mã đơn hàng vừa tạo
     * @throws IllegalArgumentException nếu thông tin nhận hàng không hợp lệ,
     *                                  giỏ rỗng hoặc hàng không còn đủ
     */
    int placeCodOrder(User_24133059 buyer, Cart_24133059 cart, String receiverName,
                      String receiverPhone, String address, String note);

    /** Một trang lịch sử đơn hàng, lọc theo trạng thái (null = tất cả). */
    PageResult_24133059<Order_24133059> listMyOrders(int userId, OrderStatus_24133059 status,
                                                     int page);

    /** Số đơn theo từng trạng thái, dùng cho con số trên các tab bộ lọc. */
    Map<String, Integer> countByStatus(int userId);

    /** Chi tiết một đơn của chính người dùng; null nếu không phải đơn của họ. */
    Order_24133059 findMyOrder(int orderId, int userId);

    /** Khách tự hủy đơn còn mới. */
    boolean cancelMyOrder(int orderId, int userId);
}
