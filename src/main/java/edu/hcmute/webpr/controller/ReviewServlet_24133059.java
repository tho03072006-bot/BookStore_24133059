package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.User_24133059;
import edu.hcmute.webpr.service.IRatingService_24133059;
import edu.hcmute.webpr.service.RatingService_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CÂU 4 - xử lý nút [Submit] của "Form thêm reviews" ở trang chi tiết sách.
 * Chỉ nhận POST; phải đăng nhập mới được gửi review.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "ReviewServlet_24133059", urlPatterns = {"/review"})
public class ReviewServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IRatingService_24133059 ratingService = new RatingService_24133059();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int bookId = ServletUtil_24133059.intParam(req, "bookId", 0);
        String backToBook = req.getContextPath() + "/book?id=" + bookId;

        User_24133059 current = ServletUtil_24133059.currentUser(req);
        if (current == null) {
            ServletUtil_24133059.flash(req, "flashError",
                    "Bạn cần đăng nhập để gửi nhận xét cho cuốn sách này.");
            resp.sendRedirect(req.getContextPath() + "/login?back=" + bookId);
            return;
        }

        try {
            ratingService.saveReview(current.getId(), bookId,
                    req.getParameter("rating"), req.getParameter("reviewText"));
            ServletUtil_24133059.flash(req, "flashSuccess", "Đã lưu nhận xét của bạn.");
        } catch (IllegalArgumentException e) {
            ServletUtil_24133059.flash(req, "flashError", e.getMessage());
        }

        // Redirect sau POST để F5 không gửi lại form (mẫu Post/Redirect/Get).
        resp.sendRedirect(backToBook + "#reviews");
    }
}
