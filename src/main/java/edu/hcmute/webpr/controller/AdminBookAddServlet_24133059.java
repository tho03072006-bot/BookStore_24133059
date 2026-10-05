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
 * CÂU 6 - TẠO: màn hình thêm sách mới vào bảng Books.
 *
 * URL: {@code /admin/books/add}. Form gửi kiểu multipart/form-data nên khai báo
 * @MultipartConfig để đọc được ô chọn ảnh bìa.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "AdminBookAddServlet_24133059", urlPatterns = {"/admin/books/add"})
@MultipartConfig(fileSizeThreshold = 512 * 1024, maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024)
public class AdminBookAddServlet_24133059 extends AbstractBookFormServlet_24133059 {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        showForm(req, resp, new Book_24133059(), List.of(), "Thêm sách mới", null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Book_24133059 book = null;
        List<Integer> authorIds = List.of();
        try {
            book = bindBook(req);
            authorIds = bindAuthorIds(req);
            book.setCoverImage(resolveCoverImage(req, null));

            int newId = bookService.create(book, authorIds);

            ServletUtil_24133059.flash(req, "flashSuccess",
                    "Đã thêm sách \"" + book.getTitle() + "\" (mã " + newId + ").");
            resp.sendRedirect(req.getContextPath() + "/admin/books");

        } catch (IllegalArgumentException e) {
            showForm(req, resp, (book != null ? book : new Book_24133059()), authorIds,
                    "Thêm sách mới", e.getMessage());
        }
    }
}
