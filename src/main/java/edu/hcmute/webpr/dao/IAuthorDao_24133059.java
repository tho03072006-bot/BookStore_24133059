package edu.hcmute.webpr.dao;

import java.util.List;

import edu.hcmute.webpr.model.Author_24133059;

/**
 * TẦNG DATA ACCESS - hợp đồng truy xuất bảng {@code author}.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public interface IAuthorDao_24133059 {

    /** Tất cả tác giả, sắp xếp theo tên. */
    List<Author_24133059> findAll();

    /** Chỉ những tác giả đang có ít nhất 01 cuốn sách (dùng cho trang home). */
    List<Author_24133059> findAllHavingBooks();

    Author_24133059 findById(int authorId);

    /** Danh sách author_id gắn với một cuốn sách - dùng khi sửa sách. */
    List<Integer> findAuthorIdsOfBook(int bookId);
}
