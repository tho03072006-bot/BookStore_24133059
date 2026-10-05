package edu.hcmute.webpr.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import edu.hcmute.webpr.util.Constants_24133059;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Phát ảnh bìa sách ra trình duyệt. Mọi trang JSP đều gọi
 * {@code /image?name=<cover_image>} nên view không phải quan tâm ảnh nằm ở đâu.
 *
 * Thứ tự tìm ảnh:
 * <ol>
 *   <li>cover_image là một URL http(s) -&gt; chuyển hướng thẳng tới URL đó</li>
 *   <li>file admin vừa tải lên, nằm trong thư mục ngoài {@code UPLOAD_DIR}</li>
 *   <li>ảnh bìa mẫu đóng gói sẵn trong webapp, thư mục /assets/covers/</li>
 * </ol>
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
@WebServlet(name = "ImageServlet_24133059", urlPatterns = {"/image"})
public class ImageServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("X-Content-Type-Options", "nosniff");

        String name = req.getParameter("name");
        if (name == null || name.isBlank()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        name = name.trim();

        if (name.startsWith("http://") || name.startsWith("https://")) {
            resp.sendRedirect(name);
            return;
        }

        // Chỉ giữ lại tên file: chặn kiểu "../../web.xml" đọc trộm file hệ thống.
        String safeName = Path.of(name).getFileName().toString();
        resp.setContentType(guessContentType(safeName));

        Path uploaded = Path.of(Constants_24133059.UPLOAD_DIR, safeName);
        if (Files.isReadable(uploaded)) {
            resp.setContentLengthLong(Files.size(uploaded));
            try (OutputStream out = resp.getOutputStream()) {
                Files.copy(uploaded, out);
            }
            return;
        }

        String inWebapp = Constants_24133059.COVER_DIR_IN_WEBAPP + safeName;
        try (InputStream in = getServletContext().getResourceAsStream(inWebapp)) {
            if (in == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try (OutputStream out = resp.getOutputStream()) {
                in.transferTo(out);
            }
        }
    }

    private String guessContentType(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }
}
