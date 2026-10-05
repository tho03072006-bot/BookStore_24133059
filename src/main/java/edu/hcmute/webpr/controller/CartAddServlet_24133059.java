package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.service.CartService_24133059;
import edu.hcmute.webpr.service.ICartService_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GIỎ HÀNG - THÊM một cuốn sách vào giỏ.
 *
 * Chưa đăng nhập vẫn thêm được; hệ thống chỉ bắt đăng nhập ở bước thanh toán.
 * Sau khi xử lý luôn chuyển hướng (mẫu Post/Redirect/Get) để bấm F5 không gửi
 * lại form và cộng thêm lần nữa.
 *
 * URL: {@code /cart/add} (POST)
 *
 * Trần Minh Thọ - 24133059
 */
@WebServlet(name = "CartAddServlet_24133059", urlPatterns = {"/cart/add"})
public class CartAddServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ICartService_24133059 cartService = new CartService_24133059();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int bookId = ServletUtil_24133059.intParam(req, "bookId", 0);
        int quantity = ServletUtil_24133059.intParam(req, "quantity", -1);

        try {
            cartService.addToCart(ServletUtil_24133059.cart(req), bookId, quantity);
            ServletUtil_24133059.flash(req, "flashSuccess",
                    "Đã thêm " + quantity + " cuốn vào giỏ hàng.");
        } catch (IllegalArgumentException e) {
            ServletUtil_24133059.flash(req, "flashError", e.getMessage());
        }

        resp.sendRedirect(backTo(req));
    }

    /**
     * Quay lại đúng trang người dùng vừa bấm "Thêm vào giỏ": tham số returnUrl
     * do form gửi lên. Chỉ nhận đường dẫn nội bộ bắt đầu bằng dấu "/" để không
     * ai lợi dụng chuyển hướng sang website khác.
     */
    private String backTo(HttpServletRequest req) {
        String returnUrl = ServletUtil_24133059.stringParam(req, "returnUrl");
        if (returnUrl.startsWith("/") && !returnUrl.startsWith("//")) {
            return req.getContextPath() + returnUrl;
        }
        return req.getContextPath() + "/cart";
    }
}
