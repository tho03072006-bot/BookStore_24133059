package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.PendingRegistration_24133059;
import edu.hcmute.webpr.service.AuthService_24133059;
import edu.hcmute.webpr.service.IAuthService_24133059;
import edu.hcmute.webpr.util.Constants_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * CÂU 2 - nút "Gửi lại mã" ở trang nhập OTP: cấp mã mới và gửi lại email.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "ResendOtpServlet_24133059", urlPatterns = {"/resend-otp"})
public class ResendOtpServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IAuthService_24133059 authService = new AuthService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession(false);
        Object attr = (session == null) ? null
                : session.getAttribute(Constants_24133059.SESSION_PENDING);

        if (!(attr instanceof PendingRegistration_24133059 pending)) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        authService.resendOtp(pending);
        ServletUtil_24133059.flash(req, "flashSuccess",
                "Đã gửi lại mã OTP tới " + pending.getEmail() + ".");
        resp.sendRedirect(req.getContextPath() + "/verify-otp");
    }
}
