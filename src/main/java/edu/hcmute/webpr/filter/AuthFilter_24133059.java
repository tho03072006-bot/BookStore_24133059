package edu.hcmute.webpr.filter;

import java.io.IOException;

import edu.hcmute.webpr.model.User_24133059;
import edu.hcmute.webpr.util.Constants_24133059;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * CÂU 1 + CÂU 2 - chặn mọi request vào "/admin/*":
 * <ul>
 *   <li>chưa đăng nhập  -&gt; chuyển về trang đăng nhập</li>
 *   <li>đăng nhập nhưng không phải admin -&gt; báo lỗi và về trang đăng nhập</li>
 * </ul>
 * Nhờ filter này mà mục "Trang quản trị" trên header chỉ admin mới vào được,
 * kể cả khi người dùng gõ thẳng URL.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class AuthFilter_24133059 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        Object attr = (session == null) ? null
                : session.getAttribute(Constants_24133059.SESSION_USER);

        if (!(attr instanceof User_24133059 user)) {
            resp.sendRedirect(req.getContextPath() + "/login?next=admin");
            return; // QUAN TRỌNG: dừng ở đây, không gọi chain.doFilter() nữa
        }

        if (!user.isAdmin()) {
            HttpSession s = req.getSession();
            s.setAttribute("flashError", "Tài khoản của bạn không có quyền vào trang quản trị.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        chain.doFilter(request, response);
    }
}
