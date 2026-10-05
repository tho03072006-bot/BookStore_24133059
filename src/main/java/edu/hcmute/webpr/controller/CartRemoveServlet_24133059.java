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
 * GIỎ HÀNG - XÓA một dòng khỏi giỏ, hoặc xóa sạch cả giỏ.
 *
 * URL: {@code /cart/remove} và {@code /cart/clear} (POST)
 *
 * Trần Minh Thọ - 24133059
 */
@WebServlet(name = "CartRemoveServlet_24133059", urlPatterns = {"/cart/remove", "/cart/clear"})
public class CartRemoveServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ICartService_24133059 cartService = new CartService_24133059();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean clearAll = req.getServletPath().endsWith("/clear");

        try {
            if (clearAll) {
                cartService.clear(ServletUtil_24133059.cart(req));
                ServletUtil_24133059.flash(req, "flashSuccess", "Đã xóa toàn bộ giỏ hàng.");
            } else {
                int bookId = ServletUtil_24133059.intParam(req, "bookId", 0);
                cartService.removeFromCart(ServletUtil_24133059.cart(req), bookId);
                ServletUtil_24133059.flash(req, "flashSuccess", "Đã bỏ sách khỏi giỏ hàng.");
            }
        } catch (IllegalArgumentException e) {
            ServletUtil_24133059.flash(req, "flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}
