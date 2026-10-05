package edu.hcmute.webpr.controller;

import java.io.IOException;
import java.util.List;

import edu.hcmute.webpr.model.Book_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CÂU 6 - CẬP NHẬT: màn hình sửa thông tin một cuốn sách.
 *
 * URL: {@code /admin/books/edit?id=<bookid>}
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "AdminBookEditServlet_24133059", urlPatterns = {"/admin/books/edit"})
@MultipartConfig(fileSizeThreshold = 512 * 1024, maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024)
public class AdminBookEditServlet_24133059 extends AbstractBookFormServlet_24133059 {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int bookId = ServletUtil_24133059.intParam(req, "id", 0);
        Book_24133059 book = (bookId > 0) ? bookService.findById(bookId) : null;

        if (book == null) {
            ServletUtil_24133059.flash(req, "flashError", "Không tìm thấy cuốn sách cần sửa.");
            resp.sendRedirect(req.getContextPath() + "/admin/books");
            return;
        }

        showForm(req, resp, book, authorService.findAuthorIdsOfBook(bookId),
                "Sửa sách #" + bookId, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Book_24133059 book = null;
        List<Integer> authorIds = List.of();
        try {
            book = bindBook(req);
            if (book.getBookId() <= 0) {
                throw new IllegalArgumentException("Thiếu mã sách cần cập nhật.");
            }
            authorIds = bindAuthorIds(req);

            // Không chọn ảnh mới và không gõ đường dẫn -> giữ nguyên ảnh cũ.
            Book_24133059 existing = bookService.findById(book.getBookId());
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy cuốn sách cần sửa.");
            }
            book.setCoverImage(resolveCoverImage(req, existing.getCoverImage()));

            if (!bookService.update(book, authorIds)) {
                throw new IllegalArgumentException("Không thể cập nhật cuốn sách này.");
            }

            ServletUtil_24133059.flash(req, "flashSuccess",
                    "Đã cập nhật sách \"" + book.getTitle() + "\".");
            resp.sendRedirect(req.getContextPath() + "/admin/books");

        } catch (IllegalArgumentException e) {
            showForm(req, resp, (book != null ? book : new Book_24133059()), authorIds,
                    "Sửa sách", e.getMessage());
        }
    }
}
