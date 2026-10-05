package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.PendingRegistration_24133059;
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
 * CÂU 2 - Controller trang NHẬP MÃ OTP để kích hoạt tài khoản.
 *
 * Nhập đúng mã và mã còn hạn ({@link Constants_24133059#OTP_EXPIRY_MINUTES}
 * phút) thì tài khoản mới thật sự được INSERT vào bảng users.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "VerifyOtpServlet_24133059", urlPatterns = {"/verify-otp"})
public class VerifyOtpServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IAuthService_24133059 authService = new AuthService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (readPending(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }
        forwardToForm(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        PendingRegistration_24133059 pending = readPending(req);
        if (pending == null) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        synchronized (pending) {
            if (readPending(req) != pending) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            if (pending.getAttempts() >= 5) {
                req.setAttribute("error", "Bạn đã nhập sai 5 lần. Vui lòng gửi lại mã để tiếp tục.");
                forwardToForm(req, resp);
                return;
            }
            if (pending.isExpired()) {
                req.setAttribute("error", "Mã OTP đã hết hạn. Bấm \"Gửi lại mã\" để nhận mã mới.");
                forwardToForm(req, resp);
                return;
            }

            if (!pending.verify(req.getParameter("otp"))) {
                req.setAttribute("error", "Mã OTP không đúng. Bạn đã nhập sai "
                        + pending.getAttempts() + " lần.");
                forwardToForm(req, resp);
                return;
            }

            try {
                authService.completeRegistration(pending);
            } catch (IllegalArgumentException e) {
                req.setAttribute("error", e.getMessage());
                forwardToForm(req, resp);
                return;
            }

            req.getSession().removeAttribute(Constants_24133059.SESSION_PENDING);
        }
        ServletUtil_24133059.flash(req, "flashSuccess",
                "Kích hoạt tài khoản thành công! Mời bạn đăng nhập.");
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private PendingRegistration_24133059 readPending(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object attr = session.getAttribute(Constants_24133059.SESSION_PENDING);
        return (attr instanceof PendingRegistration_24133059 p) ? p : null;
    }

    private void forwardToForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("pending", readPending(req));
        req.setAttribute("mailConfigured", authService.isMailConfigured());
        req.setAttribute("otpExpiryMinutes", Constants_24133059.OTP_EXPIRY_MINUTES);
        req.getRequestDispatcher("/WEB-INF/views/verify-otp.jsp").forward(req, resp);
    }
}
