package edu.hcmute.webpr.filter;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * CÂU 1 - Response giả dùng để HỨNG toàn bộ nội dung mà file decorator in ra,
 * thay vì để nó chảy thẳng xuống response thật.
 *
 * Vì sao cần: SiteMesh dựng decorator xong mới ghép với nội dung trang rồi mới
 * ghi ra response thật. Nếu decorator ghi thẳng xuống response thật thì phần
 * ghi đó bị Tomcat bỏ đi (response lúc đó đã bị đánh dấu "đã gửi xong") và
 * trình duyệt nhận về trang trắng.
 *
 * Mọi thao tác có thể làm "chốt" response thật (setContentType, setStatus,
 * flushBuffer...) đều bị chặn lại ở đây.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
class DecoratorCaptureResponse_24133059 extends HttpServletResponseWrapper {

    private final CharArrayWriter captured = new CharArrayWriter(4096);
    private final PrintWriter writer = new PrintWriter(captured);

    /** Chỉ được tạo nếu có thành phần nào đó ghi bằng byte thay vì ký tự. */
    private ByteArrayOutputStream capturedBytes;
    private ServletOutputStream stream;

    DecoratorCaptureResponse_24133059(HttpServletResponse original) {
        super(original);
    }

    /** Nội dung decorator đã in ra, sau khi flush hết bộ đệm. */
    String getCapturedText() {
        writer.flush();
        if (capturedBytes != null && capturedBytes.size() > 0) {
            return captured + capturedBytes.toString(StandardCharsets.UTF_8);
        }
        return captured.toString();
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (stream == null) {
            // Decorator của bài này là JSP nên luôn dùng getWriter(); nhánh này
            // chỉ để phòng trường hợp có thành phần khác ghi bằng byte.
            capturedBytes = new ByteArrayOutputStream(1024);
            stream = new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                    // Không dùng chế độ ghi bất đồng bộ.
                }

                @Override
                public void write(int b) {
                    capturedBytes.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) {
                    capturedBytes.write(b, off, len);
                }
            };
        }
        return stream;
    }

    // ---- Chặn mọi thao tác có thể đụng tới response thật ----

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void flushBuffer() {
        writer.flush();
    }

    @Override
    public void resetBuffer() {
        writer.flush();
        captured.reset();
    }

    @Override
    public void reset() {
        resetBuffer();
    }

    @Override
    public void setContentType(String type) {
        // Không đổi Content-Type của response thật.
    }

    @Override
    public void setContentLength(int len) {
        // Không đặt Content-Length theo độ dài của riêng decorator.
    }

    @Override
    public void setContentLengthLong(long len) {
        // Như trên.
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // Bảng mã do response thật quyết định.
    }

    @Override
    public void setStatus(int sc) {
        // Mã trạng thái do trang nội dung quyết định, không phải decorator.
    }
}
