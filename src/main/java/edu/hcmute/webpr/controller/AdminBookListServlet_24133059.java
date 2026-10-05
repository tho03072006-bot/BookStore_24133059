package edu.hcmute.webpr.controller;

import java.io.IOException;

import edu.hcmute.webpr.model.Book_24133059;
import edu.hcmute.webpr.model.PageResult_24133059;
import edu.hcmute.webpr.service.BookService_24133059;
import edu.hcmute.webpr.service.IBookService_24133059;
import edu.hcmute.webpr.util.Constants_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CÂU 6 - XEM: bảng danh sách sách trong Trang quản trị, CÓ PHÂN TRANG
 * ({@link Constants_24133059#ADMIN_PAGE_SIZE} dòng / trang).
 *
 * URL: {@code /admin/books?page=<n>} - đã được AuthFilter bảo vệ nên chỉ tài
 * khoản có is_admin = 1 mới vào được.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "AdminBookListServlet_24133059", urlPatterns = {"/admin/books"})
public class AdminBookListServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IBookService_24133059 bookService = new BookService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page = ServletUtil_24133059.intParam(req, "page", 1);
        PageResult_24133059<Book_24133059> result =
                bookService.listAll(page, Constants_24133059.ADMIN_PAGE_SIZE);

        req.setAttribute("result", result);
        req.getRequestDispatcher("/WEB-INF/views/admin/book-list.jsp").forward(req, resp);
    }
}
