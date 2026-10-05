package edu.hcmute.webpr.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.hcmute.webpr.dao.IOrderDao_24133059;
import edu.hcmute.webpr.dao.OrderDao_24133059;
import edu.hcmute.webpr.model.CartItem_24133059;
import edu.hcmute.webpr.model.Cart_24133059;
import edu.hcmute.webpr.model.OrderDetail_24133059;
import edu.hcmute.webpr.model.OrderStatus_24133059;
import edu.hcmute.webpr.model.Order_24133059;
import edu.hcmute.webpr.model.PageResult_24133059;
import edu.hcmute.webpr.model.User_24133059;
import edu.hcmute.webpr.util.Constants_24133059;

/**
 * TẦNG BUSINESS - hiện thực nghiệp vụ đặt hàng COD và lịch sử đơn hàng.
 *
 * Trần Minh Thọ - 24133059
 */
public class OrderService_24133059 implements IOrderService_24133059 {

    private final IOrderDao_24133059 orderDao = new OrderDao_24133059();
    private final ICartService_24133059 cartService = new CartService_24133059();

    @Override
    public int placeCodOrder(User_24133059 buyer, Cart_24133059 cart, String receiverName,
                             String receiverPhone, String address, String note) {

        if (buyer == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để đặt hàng.");
        }
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống.");
        }

        String name = trim(receiverName);
        String phone = trim(receiverPhone).replaceAll("\\s+", "");
        String addr = trim(address);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập tên người nhận.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Tên người nhận tối đa 50 ký tự.");
        }
        if (!phone.matches("0\\d{9,10}")) {
            throw new IllegalArgumentException("Số điện thoại phải có 10 hoặc 11 chữ số và bắt đầu bằng số 0.");
        }
        if (addr.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập địa chỉ nhận hàng.");
        }
        if (addr.length() > 200) {
            throw new IllegalArgumentException("Địa chỉ nhận hàng tối đa 200 ký tự.");
        }
        String comment = trim(note);
        if (comment.length() > 200) {
            throw new IllegalArgumentException("Ghi chú tối đa 200 ký tự.");
        }

        // Giỏ nằm trong Session nên có thể đã cũ; đọc lại kho ngay trước khi
        // chốt đơn để không bán quá số hàng đang có.
        String adjusted = cartService.refresh(cart);
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng không còn sản phẩm nào bán được.");
        }
        if (adjusted != null) {
            throw new IllegalArgumentException("Giỏ hàng vừa thay đổi: " + adjusted
                    + " Vui lòng kiểm tra lại rồi đặt hàng.");
        }

        Order_24133059 order = new Order_24133059();
        order.setUserId(buyer.getId());
        order.setOrderDate(LocalDateTime.now());
        order.setReceiverName(name);
        order.setReceiverPhone(phone);
        order.setAddress(addr);
        order.setNote(comment.isEmpty() ? null : comment);
        order.setPaymentMethod("COD");
        order.setStatus(OrderStatus_24133059.NEW);
        order.setTotalAmount(cart.getTotalAmount());

        List<OrderDetail_24133059> details = new ArrayList<>();
        for (CartItem_24133059 item : cart.getItems()) {
            OrderDetail_24133059 detail = new OrderDetail_24133059();
            detail.setBookId(item.getBookId());
            detail.setTitle(item.getTitle());
            detail.setPrice(item.getPrice());
            detail.setQuantity(item.getQuantity());
            details.add(detail);
        }
        order.setDetails(details);

        int orderId;
        try {
            orderId = orderDao.insert(order);
        } catch (IllegalStateException e) {
            // Hết hàng ngay lúc ghi đơn - đổi sang lỗi nhập liệu để Controller
            // hiện lại form thay vì báo lỗi hệ thống.
            throw new IllegalArgumentException(e.getMessage());
        }

        cartService.clear(cart);
        return orderId;
    }

    @Override
    public PageResult_24133059<Order_24133059> listMyOrders(int userId, OrderStatus_24133059 status,
                                                            int page) {
        int pageSize = Constants_24133059.ORDER_PAGE_SIZE;
        int total = orderDao.countByUser(userId, status);
        int totalPages = Math.max((total + pageSize - 1) / pageSize, 1);
        int currentPage = Math.min(Math.max(page, 1), totalPages);
        int offset = (currentPage - 1) * pageSize;

        List<Order_24133059> orders = orderDao.findByUser(userId, status, offset, pageSize);
        return new PageResult_24133059<>(orders, currentPage, pageSize, total);
    }

    @Override
    public Map<String, Integer> countByStatus(int userId) {
        return orderDao.countGroupedByStatus(userId);
    }

    @Override
    public Order_24133059 findMyOrder(int orderId, int userId) {
        return orderDao.findByIdAndUser(orderId, userId);
    }

    @Override
    public boolean cancelMyOrder(int orderId, int userId) {
        return orderDao.cancelByUser(orderId, userId);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
