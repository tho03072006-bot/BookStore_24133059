package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.User_24133059;
import edu.hcmute.webpr.service.AuthService_24133059;
import edu.hcmute.webpr.service.IAuthService_24133059;
import edu.hcmute.webpr.util.Constants_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * CÂU 2 - Controller trang ĐĂNG NHẬP, dùng Session để giữ phiên làm việc.
 *
 * Theo yêu cầu của đề: đăng nhập với vai trò user thành công thì vào TRANG CHỦ
 * của User; sai thông tin thì QUAY LẠI trang đăng nhập kèm thông báo lỗi.
 * Riêng tài khoản admin (is_admin = 1) được đưa thẳng vào Trang quản trị.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "LoginServlet_24133059", urlPatterns = {"/login"})
public class LoginServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IAuthService_24133059 authService = new AuthService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (ServletUtil_24133059.currentUser(req) != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = ServletUtil_24133059.stringParam(req, "email");
        User_24133059 user = authService.login(email, req.getParameter("password"));

        if (user == null) {
            req.setAttribute("error", "Email hoặc mật khẩu không đúng.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        // Tạo Session mới sau khi xác thực để tránh tấn công session fixation.
        // Giỏ hàng phải được bê sang Session mới, nếu không thì khách chọn sách
        // xong mới đăng nhập là mất sạch giỏ.
        Object gioHangCu = null;
        HttpSession old = req.getSession(false);
        if (old != null) {
            gioHangCu = old.getAttribute(Constants_24133059.SESSION_CART);
            old.invalidate();
        }
        HttpSession session = req.getSession(true);
        session.setAttribute(Constants_24133059.SESSION_USER, user);
        if (gioHangCu != null) {
            session.setAttribute(Constants_24133059.SESSION_CART, gioHangCu);
        }
        session.setAttribute("flashSuccess", "Xin chào " + user.getDisplayName() + "!");

        resp.sendRedirect(req.getContextPath() + decideLandingPage(req, user));
    }

    private String decideLandingPage(HttpServletRequest req, User_24133059 user) {
        Integer back = ServletUtil_24133059.optionalIntParam(req, "back");
        if (back != null && back > 0) {
            return "/book?id=" + back;
        }
        // Người dùng bị chặn ở giữa chừng (bấm Thanh toán, xem Đơn hàng của
        // tôi...) thì đăng nhập xong đưa thẳng về chỗ đang dở.
        switch (ServletUtil_24133059.stringParam(req, "next")) {
            case "checkout":
                return "/checkout";
            case "orders":
                return "/orders";
            case "cart":
                return "/cart";
            default:
                break;
        }
        if (user.isAdmin()) {
            return "/admin/books";
        }
        return "/home";
    }
}
