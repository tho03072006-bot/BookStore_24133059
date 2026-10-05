package edu.hcmute.webpr.dao;

import java.util.List;

import edu.hcmute.webpr.model.Review_24133059;

/**
 * TẦNG DATA ACCESS - hợp đồng truy xuất bảng {@code rating} (Câu 4).
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public interface IRatingDao_24133059 {

    /** Danh sách review của 01 cuốn sách, kèm tên người viết. */
    List<Review_24133059> findByBook(int bookId);

    /** Review của một user cho một cuốn sách (null nếu chưa có). */
    Review_24133059 findByUserAndBook(int userId, int bookId);

    /**
     * Thêm mới hoặc cập nhật review. Khoá chính của bảng rating là cặp
     * (userid, bookid) nên mỗi user chỉ có một review cho mỗi cuốn sách.
     */
    void save(int userId, int bookId, int rating, String reviewText);
}
