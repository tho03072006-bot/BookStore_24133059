package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.Cart_24133059;
import edu.hcmute.webpr.model.User_24133059;
import edu.hcmute.webpr.service.CartService_24133059;
import edu.hcmute.webpr.service.ICartService_24133059;
import edu.hcmute.webpr.service.IOrderService_24133059;
import edu.hcmute.webpr.service.OrderService_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * THANH TOÁN COD - nhập thông tin nhận hàng rồi chốt đơn.
 *
 * Phải đăng nhập mới vào được; chưa đăng nhập thì chuyển sang trang đăng nhập
 * và sau khi đăng nhập xong quay lại đúng đây.
 *
 * Hình thức thanh toán duy nhất là COD (trả tiền khi nhận hàng) nên không có
 * bước chuyển khoản hay nhập thẻ.
 *
 * URL: {@code /checkout}
 *
 * Trần Minh Thọ - 24133059
 */
@WebServlet(name = "CheckoutServlet_24133059", urlPatterns = {"/checkout"})
public class CheckoutServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ICartService_24133059 cartService = new CartService_24133059();
    private final IOrderService_24133059 orderService = new OrderService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User_24133059 me = ServletUtil_24133059.currentUser(req);
        if (me == null) {
            ServletUtil_24133059.flash(req, "flashError", "Bạn cần đăng nhập để thanh toán.");
            resp.sendRedirect(req.getContextPath() + "/login?next=checkout");
            return;
        }

        Cart_24133059 cart = ServletUtil_24133059.cart(req);
        String adjusted = cartService.refresh(cart);
        if (cart.isEmpty()) {
            ServletUtil_24133059.flash(req, "flashError",
                    "Giỏ hàng đang trống, chưa thể thanh toán.");
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }
        if (adjusted != null) {
            ServletUtil_24133059.flash(req, "flashError", "Giỏ hàng vừa thay đổi: " + adjusted);
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Điền sẵn tên và số điện thoại trong hồ sơ để khách đỡ phải gõ lại.
        req.setAttribute("receiverName", me.getFullname());
        req.setAttribute("receiverPhone", me.getPhoneDisplay());
        forwardToForm(req, resp, cart);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User_24133059 me = ServletUtil_24133059.currentUser(req);
        if (me == null) {
            ServletUtil_24133059.flash(req, "flashError", "Phiên đăng nhập đã hết hạn.");
            resp.sendRedirect(req.getContextPath() + "/login?next=checkout");
            return;
        }

        Cart_24133059 cart = ServletUtil_24133059.cart(req);
        String receiverName = ServletUtil_24133059.stringParam(req, "receiverName");
        String receiverPhone = ServletUtil_24133059.stringParam(req, "receiverPhone");
        String address = ServletUtil_24133059.stringParam(req, "address");
        String note = ServletUtil_24133059.stringParam(req, "note");

        try {
            int orderId = orderService.placeCodOrder(me, cart, receiverName,
                    receiverPhone, address, note);

            ServletUtil_24133059.flash(req, "flashSuccess", "Đặt hàng thành công! "
                    + "Đơn #" + orderId + " đang ở trạng thái \"Đơn hàng mới\". "
                    + "Bạn thanh toán bằng tiền mặt khi nhận hàng.");
            resp.sendRedirect(req.getContextPath() + "/orders/detail?id=" + orderId);

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("receiverName", receiverName);
            req.setAttribute("receiverPhone", receiverPhone);
            req.setAttribute("address", address);
            req.setAttribute("note", note);
            forwardToForm(req, resp, cart);
        }
    }

    private void forwardToForm(HttpServletRequest req, HttpServletResponse resp,
                               Cart_24133059 cart) throws ServletException, IOException {
        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }
}
