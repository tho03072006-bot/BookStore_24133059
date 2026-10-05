package edu.hcmute.webpr.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Ép toàn bộ request/response dùng UTF-8 để tiếng Việt có dấu trong form
 * (tiêu đề sách, nội dung review, họ tên) không bị lỗi font khi POST lên.
 *
 * Filter này đồng thời lưu lại đường dẫn gốc của request vào attribute
 * {@code originalPath}: decorator của SiteMesh chạy bằng include nên bên trong
 * decorator không lấy được đúng URL đang xem để tô sáng menu đang chọn.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class EncodingFilter_24133059 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (request instanceof HttpServletRequest req) {
            String path = req.getRequestURI().substring(req.getContextPath().length());
            req.setAttribute("originalPath", path.isEmpty() ? "/" : path);
        }

        chain.doFilter(request, response);
    }
}
