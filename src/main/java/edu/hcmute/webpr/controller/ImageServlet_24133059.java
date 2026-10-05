package edu.hcmute.webpr.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
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
        String safeName;
        try {
            Path fileName = Path.of(name).getFileName();
            if (fileName == null) { resp.sendError(404); return; }
            safeName = fileName.toString();
        } catch (InvalidPathException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        resp.setContentType(guessContentType(safeName));

        Path uploaded = Path.of(Constants_24133059.UPLOAD_DIR, safeName);
        if (Files.isRegularFile(uploaded) && Files.isReadable(uploaded)) {
            if (cached(req, resp, safeName, Files.getLastModifiedTime(uploaded).toMillis(), Files.size(uploaded))) return;
            resp.setContentLengthLong(Files.size(uploaded));
            try (OutputStream out = resp.getOutputStream()) {
                Files.copy(uploaded, out);
            }
            return;
        }

        String inWebapp = Constants_24133059.COVER_DIR_IN_WEBAPP + safeName;
        URL resource = getServletContext().getResource(inWebapp);
        if (resource == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        URLConnection connection = resource.openConnection();
        if (cached(req, resp, safeName, connection.getLastModified(), connection.getContentLengthLong())) return;
        if (connection.getContentLengthLong() >= 0) resp.setContentLengthLong(connection.getContentLengthLong());
        try (InputStream in = connection.getInputStream()) {
            try (OutputStream out = resp.getOutputStream()) {
                in.transferTo(out);
            }
        }
    }

    private boolean cached(HttpServletRequest req, HttpServletResponse resp, String name, long modified, long size) {
        String etag = "W/\"" + Integer.toHexString(name.hashCode()) + "-" + modified + "-" + size + "\"";
        resp.setHeader("Cache-Control", "public, max-age=3600");
        resp.setHeader("ETag", etag);
        if (modified > 0) resp.setDateHeader("Last-Modified", modified);
        String candidates = req.getHeader("If-None-Match");
        if (candidates != null && Arrays.stream(candidates.split(",")).map(String::trim)
                .anyMatch(value -> value.equals(etag) || value.equals("*"))) {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }
        return false;
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
