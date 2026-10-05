package edu.hcmute.webpr.service;

import java.util.List;

import edu.hcmute.webpr.model.Review_24133059;

/**
 * TẦNG BUSINESS - nghiệp vụ review / đánh giá sách (Câu 4).
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public interface IRatingService_24133059 {

    List<Review_24133059> listByBook(int bookId);

    Review_24133059 findMyReview(int userId, int bookId);

    /**
     * Lưu review của người dùng đang đăng nhập.
     *
     * @throws IllegalArgumentException nếu điểm ngoài khoảng 1..5 hoặc nội
     *                                  dung để trống
     */
    void saveReview(int userId, int bookId, String ratingRaw, String reviewText);
}
