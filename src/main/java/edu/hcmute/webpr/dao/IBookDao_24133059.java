package edu.hcmute.webpr.dao;

import java.util.List;

import edu.hcmute.webpr.model.Book_24133059;
import edu.hcmute.webpr.model.BookFilter_24133059;

/**
 * TẦNG DATA ACCESS - hợp đồng truy xuất bảng {@code books}.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public interface IBookDao_24133059 {

    /** Câu 3: lấy 1 trang sách của MỘT tác giả (offset/limit tính sẵn). */
    List<Book_24133059> findByAuthor(int authorId, int offset, int limit);

    /** Câu 3: tổng số sách của một tác giả, để tính tổng số trang. */
    int countByAuthor(int authorId);

    /** Trang "Sản phẩm" và Câu 6: lấy 1 trang trong toàn bộ sách. */
    List<Book_24133059> findAll(int offset, int limit);

    int countAll();

    List<Book_24133059> search(BookFilter_24133059 filter, int offset, int limit);

    int countSearch(BookFilter_24133059 filter);

    /** Câu 4: chi tiết 01 cuốn sách (kèm tác giả, số review, điểm trung bình). */
    Book_24133059 findById(int bookId);

    /** Câu 6 - Tạo. Trả về bookid vừa sinh tự động. */
    int insert(Book_24133059 book, List<Integer> authorIds);

    /** Câu 6 - Cập nhật. */
    boolean update(Book_24133059 book, List<Integer> authorIds);

    /** Câu 6 - Xóa. */
    boolean delete(int bookId);
}
