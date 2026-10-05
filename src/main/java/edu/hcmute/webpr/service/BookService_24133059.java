package edu.hcmute.webpr.service;

import java.util.ArrayList;
import java.util.List;

import edu.hcmute.webpr.dao.AuthorDao_24133059;
import edu.hcmute.webpr.dao.BookDao_24133059;
import edu.hcmute.webpr.dao.IAuthorDao_24133059;
import edu.hcmute.webpr.dao.IBookDao_24133059;
import edu.hcmute.webpr.model.Author_24133059;
import edu.hcmute.webpr.model.AuthorBooks_24133059;
import edu.hcmute.webpr.model.Book_24133059;
import edu.hcmute.webpr.model.PageResult_24133059;
import edu.hcmute.webpr.util.Constants_24133059;

/**
 * TẦNG BUSINESS - hiện thực nghiệp vụ sách: quy đổi "số trang" của người dùng
 * thành offset/limit cho tầng Data Access, kiểm tra dữ liệu trước khi ghi.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class BookService_24133059 implements IBookService_24133059 {

    private final IBookDao_24133059 bookDao = new BookDao_24133059();
    private final IAuthorDao_24133059 authorDao = new AuthorDao_24133059();

    // CÂU 3 - trang home: gom sách theo từng tác giả, 3 cuốn / trang

    @Override
    public List<AuthorBooks_24133059> buildHomeSections(Integer focusAuthorId, int page) {
        int pageSize = Constants_24133059.HOME_PAGE_SIZE;
        List<AuthorBooks_24133059> sections = new ArrayList<>();

        for (Author_24133059 author : authorDao.findAllHavingBooks()) {
            // Chỉ khối của tác giả vừa bấm mới đổi trang; các khối còn lại giữ
            // nguyên trang 1 để người dùng không bị mất ngữ cảnh.
            boolean focused = focusAuthorId != null && focusAuthorId == author.getAuthorId();
            int total = bookDao.countByAuthor(author.getAuthorId());
            int currentPage = normalizePage(focused ? page : 1, total, pageSize);
            int offset = (currentPage - 1) * pageSize;

            List<Book_24133059> books = bookDao.findByAuthor(author.getAuthorId(), offset, pageSize);
            sections.add(new AuthorBooks_24133059(author,
                    new PageResult_24133059<>(books, currentPage, pageSize, total)));
        }
        return sections;
    }

    @Override
    public PageResult_24133059<Book_24133059> listAll(int page, int pageSize) {
        int total = bookDao.countAll();
        int currentPage = normalizePage(page, total, pageSize);
        int offset = (currentPage - 1) * pageSize;
        List<Book_24133059> books = bookDao.findAll(offset, pageSize);
        return new PageResult_24133059<>(books, currentPage, pageSize, total);
    }

    @Override
    public Book_24133059 findById(int bookId) {
        return bookDao.findById(bookId);
    }

    // CÂU 6 - CRUD

    @Override
    public int create(Book_24133059 book, List<Integer> authorIds) {
        validate(book);
        validateAuthors(authorIds);
        return bookDao.insert(book, authorIds);
    }

    @Override
    public boolean update(Book_24133059 book, List<Integer> authorIds) {
        validate(book);
        validateAuthors(authorIds);
        return bookDao.update(book, authorIds);
    }

    @Override
    public boolean delete(int bookId) {
        return bookDao.delete(bookId);
    }

    /** Ném IllegalArgumentException với thông báo tiếng Việt để Controller hiện lên form. */
    private void validate(Book_24133059 book) {
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new IllegalArgumentException("Tiêu đề sách không được để trống.");
        }
        if (book.getTitle().length() > 200) {
            throw new IllegalArgumentException("Tiêu đề sách tối đa 200 ký tự.");
        }
        if (book.getPublisher() != null && book.getPublisher().length() > 100) {
            throw new IllegalArgumentException("Tên nhà xuất bản tối đa 100 ký tự.");
        }
        if (book.getQuantity() != null && book.getQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng không được là số âm.");
        }
        if (book.getPrice() != null) {
            // Cột price khai báo decimal(6,2) nên giá trị tối đa là 9999.99.
            if (book.getPrice().signum() < 0) {
                throw new IllegalArgumentException("Giá không được là số âm.");
            }
            if (book.getPrice().compareTo(new java.math.BigDecimal("9999.99")) > 0) {
                throw new IllegalArgumentException("Giá tối đa là 9999.99 nghìn đồng "
                        + "(tức 9.999.990 đ) vì cột price kiểu decimal(6,2).");
            }
        }
    }

    private void validateAuthors(List<Integer> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một tác giả để sách xuất hiện trên trang chủ.");
        }
    }

    /** Ép số trang về khoảng hợp lệ [1 .. tổng số trang]. */
    private int normalizePage(int page, int totalItems, int pageSize) {
        int totalPages = Math.max((totalItems + pageSize - 1) / pageSize, 1);
        if (page < 1) {
            return 1;
        }
        return Math.min(page, totalPages);
    }
}
