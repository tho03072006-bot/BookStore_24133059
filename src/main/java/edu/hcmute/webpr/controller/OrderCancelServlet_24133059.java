package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.User_24133059;
import edu.hcmute.webpr.service.IOrderService_24133059;
import edu.hcmute.webpr.service.OrderService_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * LỊCH SỬ ĐẶT HÀNG - khách tự HỦY đơn của mình.
 *
 * Chỉ hủy được khi đơn còn ở trạng thái "Đơn hàng mới"; đơn đã xác nhận trở đi
 * thì phải liên hệ cửa hàng. Số lượng đã trừ được cộng trả lại tồn kho.
 *
 * URL: {@code /orders/cancel} (POST)
 *
 * Trần Minh Thọ - 24133059
 */
@WebServlet(name = "OrderCancelServlet_24133059", urlPatterns = {"/orders/cancel"})
public class OrderCancelServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IOrderService_24133059 orderService = new OrderService_24133059();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User_24133059 me = ServletUtil_24133059.currentUser(req);
        if (me == null) {
            resp.sendRedirect(req.getContextPath() + "/login?next=orders");
            return;
        }

        int orderId = ServletUtil_24133059.intParam(req, "id", 0);

        if (orderId > 0 && orderService.cancelMyOrder(orderId, me.getId())) {
            ServletUtil_24133059.flash(req, "flashSuccess",
                    "Đã hủy đơn #" + orderId + ". Sách đã được trả lại kho.");
        } else {
            ServletUtil_24133059.flash(req, "flashError",
                    "Không hủy được đơn này. Chỉ đơn đang ở trạng thái "
                    + "\"Đơn hàng mới\" mới được tự hủy.");
        }

        resp.sendRedirect(req.getContextPath() + "/orders/detail?id=" + orderId);
    }
}
