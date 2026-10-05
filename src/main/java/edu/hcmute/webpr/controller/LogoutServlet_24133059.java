package edu.hcmute.webpr.controller;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * CÂU 2 - ĐĂNG XUẤT: huỷ Session hiện tại rồi quay về trang chủ.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "LogoutServlet_24133059", urlPatterns = {"/logout"})
public class LogoutServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate(); // xoá sạch dữ liệu phiên: user, giỏ thông báo...
        }

        // Tạo session mới chỉ để mang thông báo tạm biệt.
        req.getSession(true).setAttribute("flashSuccess", "Bạn đã đăng xuất.");
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
