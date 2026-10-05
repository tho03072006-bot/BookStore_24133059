package edu.hcmute.webpr.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.hcmute.webpr.model.Book_24133059;
import edu.hcmute.webpr.service.AuthorService_24133059;
import edu.hcmute.webpr.service.BookService_24133059;
import edu.hcmute.webpr.service.IAuthorService_24133059;
import edu.hcmute.webpr.service.IBookService_24133059;
import edu.hcmute.webpr.util.Constants_24133059;
import edu.hcmute.webpr.util.ServletUtil_24133059;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 * CÂU 6 - phần dùng chung của hai màn hình THÊM và SỬA sách: đọc dữ liệu từ
 * form, xử lý ảnh bìa tải lên và mở lại form khi dữ liệu không hợp lệ.
 *
 * Lớp này KHÔNG có @WebServlet vì nó chỉ là lớp cha, không map với URL nào.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public abstract class AbstractBookFormServlet_24133059 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final List<String> ALLOWED_EXTENSIONS =
            List.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    protected final IBookService_24133059 bookService = new BookService_24133059();
    protected final IAuthorService_24133059 authorService = new AuthorService_24133059();

    protected Book_24133059 bindBook(HttpServletRequest req) {
        Book_24133059 book = new Book_24133059();
        book.setBookId(ServletUtil_24133059.intParam(req, "bookId", 0));
        book.setIsbn(ServletUtil_24133059.optionalIntParam(req, "isbn"));
        book.setTitle(ServletUtil_24133059.stringParam(req, "title"));
        book.setPublisher(ServletUtil_24133059.stringParam(req, "publisher"));
        book.setPrice(ServletUtil_24133059.optionalDecimalParam(req, "price"));
        book.setDescription(ServletUtil_24133059.stringParam(req, "description"));
        book.setPublishDate(ServletUtil_24133059.optionalDateParam(req, "publishDate"));
        book.setQuantity(ServletUtil_24133059.optionalIntParam(req, "quantity"));
        return book;
    }

    protected List<Integer> bindAuthorIds(HttpServletRequest req) {
        String[] raw = req.getParameterValues("authorIds");
        List<Integer> ids = new ArrayList<>();
        if (raw != null) {
            for (String value : raw) {
                try {
                    ids.add(Integer.valueOf(value.trim()));
                } catch (NumberFormatException ignored) {
                    // Bỏ qua giá trị lạ, không làm hỏng cả thao tác lưu.
                }
            }
        }
        return ids;
    }

    /**
     * Xác định giá trị cho cột cover_image:
     * <ul>
     *   <li>có chọn file -&gt; lưu file vào thư mục ngoài và dùng tên file đó</li>
     *   <li>không chọn file -&gt; dùng ô nhập tay (tên file mẫu hoặc URL ảnh)</li>
     * </ul>
     */
    protected String resolveCoverImage(HttpServletRequest req, String currentValue)
            throws IOException, ServletException {

        Part part = req.getPart("coverFile");
        if (part != null && part.getSize() > 0) {
            return storeUploadedFile(part);
        }

        String typed = ServletUtil_24133059.stringParam(req, "coverImage");
        if (!typed.isEmpty()) {
            if (typed.length() > 100) {
                throw new IllegalArgumentException(
                        "Đường dẫn ảnh bìa tối đa 100 ký tự (cột cover_image varchar(100)).");
            }
            return typed;
        }
        return currentValue;
    }

    private String storeUploadedFile(Part part) throws IOException {
        String original = Path.of(part.getSubmittedFileName()).getFileName().toString();
        String extension = extensionOf(original);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Ảnh bìa tải lên chỉ nhận các đuôi: jpg, jpeg, png, gif, webp.");
        }

        Path dir = Path.of(Constants_24133059.UPLOAD_DIR);
        Files.createDirectories(dir);

        // Thêm mốc thời gian vào tên file để lần tải lên sau không đè lần trước.
        String storedName = System.currentTimeMillis() + "_"
                + original.replaceAll("[^A-Za-z0-9._-]", "_");
        if (storedName.length() > 100) {
            storedName = storedName.substring(storedName.length() - 100);
        }

        try (InputStream in = part.getInputStream()) {
            Files.copy(in, dir.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
        }
        return storedName;
    }

    private String extensionOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot < 0) ? "" : fileName.substring(dot).toLowerCase(Locale.ROOT);
    }

    protected void showForm(HttpServletRequest req, HttpServletResponse resp,
                            Book_24133059 book, List<Integer> selectedAuthorIds,
                            String formTitle, String errorMessage)
            throws ServletException, IOException {

        req.setAttribute("book", book);
        req.setAttribute("selectedAuthorIds", selectedAuthorIds);
        req.setAttribute("allAuthors", authorService.findAll());
        req.setAttribute("formTitle", formTitle);
        if (errorMessage != null) {
            req.setAttribute("error", errorMessage);
            req.setAttribute("errorField", fieldForError(errorMessage));
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/book-form.jsp").forward(req, resp);
    }

    private String fieldForError(String message) {
        if (message.startsWith("Tiêu đề")) return "title";
        if (message.contains("tác giả")) return "authorIds";
        if (message.startsWith("Giá")) return "price";
        if (message.startsWith("Số lượng")) return "quantity";
        if (message.startsWith("Tên nhà xuất bản")) return "publisher";
        if (message.contains("ảnh bìa")) return "coverImage";
        return "";
    }
}
