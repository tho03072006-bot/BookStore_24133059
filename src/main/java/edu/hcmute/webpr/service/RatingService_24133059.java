package edu.hcmute.webpr.service;

import java.util.List;

import edu.hcmute.webpr.dao.IRatingDao_24133059;
import edu.hcmute.webpr.dao.RatingDao_24133059;
import edu.hcmute.webpr.model.Review_24133059;

/**
 * TẦNG BUSINESS - hiện thực nghiệp vụ review (Câu 4).
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class RatingService_24133059 implements IRatingService_24133059 {

    private final IRatingDao_24133059 ratingDao = new RatingDao_24133059();

    @Override
    public List<Review_24133059> listByBook(int bookId) {
        return ratingDao.findByBook(bookId);
    }

    @Override
    public Review_24133059 findMyReview(int userId, int bookId) {
        return ratingDao.findByUserAndBook(userId, bookId);
    }

    @Override
    public void saveReview(int userId, int bookId, String ratingRaw, String reviewText) {
        int rating;
        try {
            rating = Integer.parseInt(ratingRaw == null ? "" : ratingRaw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Vui lòng chọn số sao đánh giá.");
        }
        if (rating < 1 || rating > 5) {
            // Cột rating kiểu tinyint, quy ước thang điểm 1..5 sao.
            throw new IllegalArgumentException("Điểm đánh giá phải từ 1 đến 5 sao.");
        }
        String text = reviewText == null ? "" : reviewText.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Nội dung nhận xét không được để trống.");
        }
        ratingDao.save(userId, bookId, rating, text);
    }
}
