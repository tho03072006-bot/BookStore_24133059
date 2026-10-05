package edu.hcmute.webpr.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.hcmute.webpr.model.Author_24133059;

/**
 * TẦNG DATA ACCESS - hiện thực truy xuất bảng {@code author} bằng JDBC thuần.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class AuthorDao_24133059 implements IAuthorDao_24133059 {

    private final JDBCConnect_24133059 jdbc = new JDBCConnect_24133059();

    @Override
    public List<Author_24133059> findAll() {
        String sql = "SELECT author_id, author_name, date_of_birth FROM author ORDER BY author_name";
        List<Author_24133059> list = new ArrayList<>();
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc danh sách tác giả", e);
        }
        return list;
    }

    @Override
    public List<Author_24133059> findAllHavingBooks() {
        String sql = "SELECT a.author_id, a.author_name, a.date_of_birth "
                + "FROM author a "
                + "WHERE EXISTS (SELECT 1 FROM book_author ba WHERE ba.author_id = a.author_id) "
                + "ORDER BY a.author_id";
        List<Author_24133059> list = new ArrayList<>();
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc danh sách tác giả có sách", e);
        }
        return list;
    }

    @Override
    public Author_24133059 findById(int authorId) {
        String sql = "SELECT author_id, author_name, date_of_birth FROM author WHERE author_id = ?";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc tác giả id=" + authorId, e);
        }
    }

    @Override
    public List<Integer> findAuthorIdsOfBook(int bookId) {
        String sql = "SELECT author_id FROM book_author WHERE bookid = ?";
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đọc tác giả của sách id=" + bookId, e);
        }
        return ids;
    }

    /** Đổ một dòng ResultSet thành đối tượng Author. */
    static Author_24133059 map(ResultSet rs) throws SQLException {
        Author_24133059 a = new Author_24133059();
        a.setAuthorId(rs.getInt("author_id"));
        a.setAuthorName(rs.getString("author_name"));
        Date dob = rs.getDate("date_of_birth");
        a.setDateOfBirth(dob == null ? null : dob.toLocalDate());
        return a;
    }
}
