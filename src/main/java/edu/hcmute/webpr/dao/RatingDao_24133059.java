package edu.hcmute.webpr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.hcmute.webpr.model.Review_24133059;

/**
 * TẦNG DATA ACCESS - hiện thực truy xuất bảng {@code rating} bằng JDBC thuần.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class RatingDao_24133059 implements IRatingDao_24133059 {

    private final JDBCConnect_24133059 jdbc = new JDBCConnect_24133059();

    @Override
    public List<Review_24133059> findByBook(int bookId) {
        String sql = "SELECT r.userid, r.bookid, r.rating, r.review_text, "
                + "       u.fullname, u.email "
                + "FROM rating r JOIN users u ON u.id = r.userid "
                + "WHERE r.bookid = ? "
                + "ORDER BY r.userid";
        List<Review_24133059> list = new ArrayList<>();
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc review của sách id=" + bookId, e);
        }
        return list;
    }

    @Override
    public Review_24133059 findByUserAndBook(int userId, int bookId) {
        String sql = "SELECT r.userid, r.bookid, r.rating, r.review_text, "
                + "       u.fullname, u.email "
                + "FROM rating r JOIN users u ON u.id = r.userid "
                + "WHERE r.userid = ? AND r.bookid = ?";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc review của user " + userId, e);
        }
    }

    @Override
    public void save(int userId, int bookId, int rating, String reviewText) {
        // Khoá chính là (userid, bookid): đã có thì UPDATE, chưa có thì INSERT.
        String update = "UPDATE rating SET rating = ?, review_text = ? "
                + "WHERE userid = ? AND bookid = ?";
        String insert = "INSERT INTO rating (userid, bookid, rating, review_text) "
                + "VALUES (?,?,?,?)";
        try (Connection conn = jdbc.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(update)) {
                ps.setInt(1, rating);
                ps.setString(2, reviewText);
                ps.setInt(3, userId);
                ps.setInt(4, bookId);
                if (ps.executeUpdate() > 0) {
                    return;
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setInt(1, userId);
                ps.setInt(2, bookId);
                ps.setInt(3, rating);
                ps.setString(4, reviewText);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu review", e);
        }
    }

    private Review_24133059 map(ResultSet rs) throws SQLException {
        Review_24133059 r = new Review_24133059();
        r.setUserId(rs.getInt("userid"));
        r.setBookId(rs.getInt("bookid"));
        int rating = rs.getInt("rating");
        r.setRating(rs.wasNull() ? null : rating);
        r.setReviewText(rs.getString("review_text"));
        String fullname = rs.getString("fullname");
        r.setUserDisplayName((fullname == null || fullname.isBlank())
                ? rs.getString("email") : fullname);
        return r;
    }
}
