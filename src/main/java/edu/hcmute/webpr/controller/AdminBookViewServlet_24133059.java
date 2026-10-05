package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.Book_24133059;
import edu.hcmute.webpr.service.BookService_24133059;
import edu.hcmute.webpr.service.IBookService_24133059;
import edu.hcmute.webpr.service.IRatingService_24133059;
import edu.hcmute.webpr.service.RatingService_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CÂU 6 - XEM chi tiết 01 dòng trong bảng Books (chế độ chỉ đọc của admin).
 *
 * URL: {@code /admin/books/view?id=<bookid>}
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "AdminBookViewServlet_24133059", urlPatterns = {"/admin/books/view"})
public class AdminBookViewServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IBookService_24133059 bookService = new BookService_24133059();
    private final IRatingService_24133059 ratingService = new RatingService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int bookId = ServletUtil_24133059.intParam(req, "id", 0);
        Book_24133059 book = (bookId > 0) ? bookService.findById(bookId) : null;

        if (book == null) {
            ServletUtil_24133059.flash(req, "flashError", "Không tìm thấy cuốn sách cần xem.");
            resp.sendRedirect(req.getContextPath() + "/admin/books");
            return;
        }

        req.setAttribute("book", book);
        req.setAttribute("reviews", ratingService.listByBook(bookId));
        req.getRequestDispatcher("/WEB-INF/views/admin/book-view.jsp").forward(req, resp);
    }
}
