package edu.hcmute.webpr.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Token đồng bộ trong session cho mọi form POST; không dùng URL để thay đổi dữ liệu. */
public class SecurityFilter_24133059 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        resp.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self'; "
                + "style-src 'self' 'unsafe-inline'; img-src 'self' https: http: data:; "
                + "object-src 'none'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.startsWith("/assets/") || path.equals("/image")) {
            chain.doFilter(request, response);
            return;
        }
        resp.setHeader("Cache-Control", "no-store");
        HttpSession session = req.getSession();
        String token;
        synchronized (session) {
            token = (String) session.getAttribute("csrfToken");
            if (token == null) {
                token = UUID.randomUUID().toString();
                session.setAttribute("csrfToken", token);
            }
        }
        if ("POST".equals(req.getMethod())) {
            String supplied = req.getParameter("_csrf");
            if (supplied == null || !MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),
                    supplied.getBytes(StandardCharsets.UTF_8))) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                req.setAttribute("securityError", "Phiên biểu mẫu đã hết hạn. Vui lòng tải lại trang và thử lại.");
                req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
