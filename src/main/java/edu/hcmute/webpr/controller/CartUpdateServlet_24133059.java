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
 * GIỎ HÀNG - SỬA số lượng của một dòng trong giỏ.
 *
 * Số lượng mới phải nằm trong giới hạn: tối thiểu 1 cuốn, tối đa là số tồn kho
 * còn lại của cuốn đó. Vượt giới hạn thì giữ nguyên giỏ và báo lỗi.
 *
 * URL: {@code /cart/update} (POST)
 *
 * Trần Minh Thọ - 24133059
 */
@WebServlet(name = "CartUpdateServlet_24133059", urlPatterns = {"/cart/update"})
public class CartUpdateServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ICartService_24133059 cartService = new CartService_24133059();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int bookId = ServletUtil_24133059.intParam(req, "bookId", 0);
        int quantity = ServletUtil_24133059.intParam(req, "quantity", -1);

        try {
            cartService.updateQuantity(ServletUtil_24133059.cart(req), bookId, quantity);
            ServletUtil_24133059.flash(req, "flashSuccess", "Đã cập nhật số lượng.");
        } catch (IllegalArgumentException e) {
            ServletUtil_24133059.flash(req, "flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}
