package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.OrderStatus_24133059;
import edu.hcmute.webpr.model.Order_24133059;
import edu.hcmute.webpr.model.User_24133059;
import edu.hcmute.webpr.service.IOrderService_24133059;
import edu.hcmute.webpr.service.OrderService_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * LỊCH SỬ ĐẶT HÀNG - chi tiết một đơn, kèm thanh tiến trình 06 bước trạng thái.
 *
 * Truy vấn luôn kèm điều kiện userid nên không thể xem đơn của người khác dù
 * có gõ thẳng mã đơn lên URL.
 *
 * URL: {@code /orders/detail?id=<order_id>}
 *
 * Trần Minh Thọ - 24133059
 */
@WebServlet(name = "OrderDetailServlet_24133059", urlPatterns = {"/orders/detail"})
public class OrderDetailServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IOrderService_24133059 orderService = new OrderService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User_24133059 me = ServletUtil_24133059.currentUser(req);
        if (me == null) {
            ServletUtil_24133059.flash(req, "flashError",
                    "Bạn cần đăng nhập để xem đơn hàng.");
            resp.sendRedirect(req.getContextPath() + "/login?next=orders");
            return;
        }

        int orderId = ServletUtil_24133059.intParam(req, "id", 0);
        Order_24133059 order = (orderId > 0) ? orderService.findMyOrder(orderId, me.getId()) : null;

        if (order == null) {
            ServletUtil_24133059.flash(req, "flashError", "Không tìm thấy đơn hàng này.");
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        req.setAttribute("order", order);
        req.setAttribute("statuses", OrderStatus_24133059.all());
        req.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(req, resp);
    }
}
