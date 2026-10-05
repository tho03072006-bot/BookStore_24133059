package edu.hcmute.webpr.controller;

import java.io.IOException;
import java.util.Map;

import edu.hcmute.webpr.model.OrderStatus_24133059;
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
 * LỊCH SỬ ĐẶT HÀNG - danh sách đơn của chính người đang đăng nhập, LỌC THEO
 * TRẠNG THÁI qua tham số {@code status}.
 *
 * Không truyền status thì hiện tất cả. Mỗi tab lọc kèm số đơn đang có để nhìn
 * phát biết ngay, nhờ vậy khi vào database đổi trạng thái một đơn rồi F5 lại
 * trang này là thấy đơn nhảy sang tab khác.
 *
 * URL: {@code /orders?status=<MÃ>&page=<n>}
 *
 * Trần Minh Thọ - 24133059
 */
@WebServlet(name = "OrderHistoryServlet_24133059", urlPatterns = {"/orders"})
public class OrderHistoryServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IOrderService_24133059 orderService = new OrderService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User_24133059 me = ServletUtil_24133059.currentUser(req);
        if (me == null) {
            ServletUtil_24133059.flash(req, "flashError",
                    "Bạn cần đăng nhập để xem lịch sử đặt hàng.");
            resp.sendRedirect(req.getContextPath() + "/login?next=orders");
            return;
        }

        // status sai hoặc không có -> null, nghĩa là xem tất cả đơn.
        OrderStatus_24133059 status =
                OrderStatus_24133059.fromCode(req.getParameter("status"));
        int page = ServletUtil_24133059.intParam(req, "page", 1);

        Map<String, Integer> counts = orderService.countByStatus(me.getId());
        int totalAll = counts.values().stream().mapToInt(Integer::intValue).sum();

        req.setAttribute("result", orderService.listMyOrders(me.getId(), status, page));
        req.setAttribute("currentStatus", status);
        req.setAttribute("statuses", OrderStatus_24133059.all());
        req.setAttribute("counts", counts);
        req.setAttribute("totalAll", totalAll);

        req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, resp);
    }
}
