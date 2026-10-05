package edu.hcmute.webpr.model;

import java.io.Serializable;

/**
 * Model ánh xạ bảng {@code rating}, kèm tên người review lấy từ bảng
 * {@code users} để trang chi tiết in đúng mẫu "[users]: [review_text]"
 * của Câu 4.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class Review_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private int bookId;
    private Integer rating;
    private String reviewText;

    /** Lấy kèm từ bảng users (không phải cột của bảng rating). */
    private String userDisplayName;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }
}
