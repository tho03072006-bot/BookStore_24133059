package edu.hcmute.webpr.controller;

import java.io.IOException;
import java.util.List;

import edu.hcmute.webpr.model.AuthorBooks_24133059;
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
 * CÂU 3 - Controller trang home: hiển thị TẤT CẢ sách, gom theo từng tác giả,
 * mỗi tác giả phân trang 03 sách/trang.
 *
 * URL: {@code /home?author=<id>&page=<n>} - bấm số trang ở khối của tác giả
 * nào thì chỉ khối đó đổi trang, các khối còn lại giữ nguyên.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "HomeServlet_24133059", urlPatterns = {"/home"})
public class HomeServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IBookService_24133059 bookService = new BookService_24133059();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer focusAuthorId = ServletUtil_24133059.optionalIntParam(req, "author");
        int page = ServletUtil_24133059.intParam(req, "page", 1);

        List<AuthorBooks_24133059> sections = bookService.buildHomeSections(focusAuthorId, page);

        req.setAttribute("sections", sections);
        req.setAttribute("pageSize", Constants_24133059.HOME_PAGE_SIZE);
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}
