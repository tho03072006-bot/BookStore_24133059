package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.service.BookService_24133059;
import edu.hcmute.webpr.service.IBookService_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CÂU 6 - XÓA một cuốn sách khỏi bảng Books.
 *
 * Chỉ nhận POST (nút Xóa nằm trong một form nhỏ có xác nhận ở JSP) để tránh
 * trường hợp chỉ cần mở URL là dữ liệu bị xóa.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "AdminBookDeleteServlet_24133059", urlPatterns = {"/admin/books/delete"})
public class AdminBookDeleteServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IBookService_24133059 bookService = new BookService_24133059();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int bookId = ServletUtil_24133059.intParam(req, "id", 0);
        int page = ServletUtil_24133059.intParam(req, "page", 1);

        if (bookId <= 0) {
            ServletUtil_24133059.flash(req, "flashError", "Thiếu mã sách cần xóa.");
        } else if (bookService.delete(bookId)) {
            ServletUtil_24133059.flash(req, "flashSuccess", "Đã xóa sách mã " + bookId + ".");
        } else {
            ServletUtil_24133059.flash(req, "flashError", "Không tìm thấy sách mã " + bookId + ".");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/books?page=" + page);
    }
}
