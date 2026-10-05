package edu.hcmute.webpr.service;

import java.util.List;

import edu.hcmute.webpr.model.AuthorBooks_24133059;
import edu.hcmute.webpr.model.BookFilter_24133059;
import edu.hcmute.webpr.model.Book_24133059;
import edu.hcmute.webpr.model.PageResult_24133059;

/**
 * TẦNG BUSINESS - nghiệp vụ liên quan tới sách.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public interface IBookService_24133059 {

    /**
     * CÂU 3: dựng nội dung trang home - mỗi tác giả một khối, mỗi khối hiển
     * thị 3 cuốn/trang và có thanh phân trang riêng.
     *
     * @param focusAuthorId tác giả vừa được bấm chuyển trang (null nếu chưa bấm)
     * @param page          số trang của riêng tác giả đó
     */
    List<AuthorBooks_24133059> buildHomeSections(Integer focusAuthorId, int page);

    /** Trang "Sản phẩm": toàn bộ sách, có phân trang. */
    PageResult_24133059<Book_24133059> listAll(int page, int pageSize);

    PageResult_24133059<Book_24133059> search(BookFilter_24133059 filter, int page, int pageSize);

    /** CÂU 4: chi tiết 01 cuốn sách. */
    Book_24133059 findById(int bookId);

    /** CÂU 6 - Tạo. Trả về bookid mới. */
    int create(Book_24133059 book, List<Integer> authorIds);

    /** CÂU 6 - Cập nhật. */
    boolean update(Book_24133059 book, List<Integer> authorIds);

    /** CÂU 6 - Xóa. */
    boolean delete(int bookId);
}
