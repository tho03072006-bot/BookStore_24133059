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

/**
 * CÂU 2 - Controller trang ĐĂNG KÝ.
 *
 * Bấm "Đăng ký" KHÔNG ghi ngay vào database: hệ thống sinh mã OTP 6 số, gửi tới
 * email vừa nhập và cất hồ sơ vào Session, rồi chuyển sang trang nhập OTP.
 * Tài khoản chỉ được tạo thật khi nhập đúng mã (xem VerifyOtpServlet).
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "RegisterServlet_24133059", urlPatterns = {"/register"})
public class RegisterServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IAuthService_24133059 authService = new AuthService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forwardToForm(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = ServletUtil_24133059.stringParam(req, "email");
        String fullname = ServletUtil_24133059.stringParam(req, "fullname");
        String phone = ServletUtil_24133059.stringParam(req, "phone");

        try {
            Object previous = req.getSession().getAttribute(Constants_24133059.SESSION_PENDING);
            if (previous instanceof PendingRegistration_24133059 p
                    && p.getEmail().equalsIgnoreCase(email) && !p.canResend()) {
                throw new IllegalArgumentException("Vui lòng chờ 60 giây trước khi yêu cầu mã mới.");
            }
            PendingRegistration_24133059 pending = authService.startRegistration(
                    email, fullname, phone,
                    req.getParameter("password"), req.getParameter("confirmPassword"));

            req.getSession().setAttribute(Constants_24133059.SESSION_PENDING, pending);
            resp.sendRedirect(req.getContextPath() + "/verify-otp");

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("errorField", fieldForError(e.getMessage()));
            req.setAttribute("email", email);
            req.setAttribute("fullname", fullname);
            req.setAttribute("phone", phone);
            forwardToForm(req, resp);
        }
    }

    private void forwardToForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("mailConfigured", authService.isMailConfigured());
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    private String fieldForError(String message) {
        if (message.startsWith("Email")) return "email";
        if (message.startsWith("Họ tên")) return "fullname";
        if (message.startsWith("Số điện thoại")) return "phone";
        if (message.startsWith("Mật khẩu nhập lại")) return "confirmPassword";
        if (message.startsWith("Mật khẩu")) return "password";
        return "";
    }
}
