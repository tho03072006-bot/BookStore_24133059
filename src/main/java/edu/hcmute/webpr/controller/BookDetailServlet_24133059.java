package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.Book_24133059;
import edu.hcmute.webpr.model.User_24133059;
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
 * CÂU 4 - Controller trang chi tiết 01 cuốn sách. Người dùng tới đây bằng cách
 * bấm vào TIÊU ĐỀ sách ở trang home (Câu 3).
 *
 * Trang hiển thị: ảnh bìa, tiêu đề, mã isbn, tác giả, publisher, publish_date,
 * quantity, số reviews; bên dưới là danh sách review dạng
 * "[users]: [review_text]" và form thêm review.
 *
 * URL: {@code /book?id=<bookid>}
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "BookDetailServlet_24133059", urlPatterns = {"/book"})
public class BookDetailServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IBookService_24133059 bookService = new BookService_24133059();
    private final IRatingService_24133059 ratingService = new RatingService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int bookId = ServletUtil_24133059.intParam(req, "id", 0);
        Book_24133059 book = (bookId > 0) ? bookService.findById(bookId) : null;

        if (book == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy cuốn sách này.");
            return;
        }

        req.setAttribute("book", book);
        req.setAttribute("reviews", ratingService.listByBook(bookId));

        // Nếu người dùng đã từng review cuốn này thì đổ sẵn vào form để sửa lại
        // (khoá chính của bảng rating là cặp userid + bookid).
        User_24133059 current = ServletUtil_24133059.currentUser(req);
        if (current != null) {
            req.setAttribute("myReview", ratingService.findMyReview(current.getId(), bookId));
        }

        req.getRequestDispatcher("/WEB-INF/views/book-detail.jsp").forward(req, resp);
    }
}
