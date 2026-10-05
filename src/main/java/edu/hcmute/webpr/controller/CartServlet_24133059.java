package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.Cart_24133059;
import edu.hcmute.webpr.service.CartService_24133059;
import edu.hcmute.webpr.service.ICartService_24133059;
import edu.hcmute.webpr.util.Constants_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GIỎ HÀNG - trang xem giỏ.
 *
 * Trước khi hiện trang, giỏ được đối chiếu lại với tồn kho trong database:
 * sách đã hết hoặc đã bị xoá thì tự bỏ khỏi giỏ, số lượng vượt kho thì giảm
 * lại, kèm thông báo cho người dùng biết.
 *
 * URL: {@code /cart}
 *
 * Trần Minh Thọ - 24133059
 */
@WebServlet(name = "CartServlet_24133059", urlPatterns = {"/cart"})
public class CartServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ICartService_24133059 cartService = new CartService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Cart_24133059 cart = ServletUtil_24133059.cart(req);
        String adjusted = cartService.refresh(cart);
        if (adjusted != null) {
            req.setAttribute("warning", "Giỏ hàng vừa được cập nhật: " + adjusted);
        }

        req.setAttribute("cart", cart);
        req.setAttribute("maxPerBook", Constants_24133059.CART_MAX_QUANTITY_PER_BOOK);
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }
}
