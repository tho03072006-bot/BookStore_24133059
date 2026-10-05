package edu.hcmute.webpr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import edu.hcmute.webpr.model.User_24133059;

/**
 * TẦNG DATA ACCESS - hiện thực truy xuất bảng {@code users} bằng JDBC thuần.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class UserDao_24133059 implements IUserDao_24133059 {

    private final JDBCConnect_24133059 jdbc = new JDBCConnect_24133059();

    private static final String COLUMNS =
            "id, email, fullname, phone, passwd, signup_date, last_login, is_admin";

    @Override
    public User_24133059 findByEmail(String email) {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE email = ?";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm tài khoản theo email", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra email đã tồn tại", e);
        }
    }

    @Override
    public int insert(User_24133059 user) {
        String sql = "INSERT INTO users (email, fullname, phone, passwd, signup_date, "
                + "last_login, is_admin) VALUES (?,?,?,?,?,NULL,?)";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullname());
            if (user.getPhone() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, user.getPhone());
            }
            ps.setString(4, user.getPasswd());
            ps.setTimestamp(5, Timestamp.valueOf(
                    user.getSignupDate() != null ? user.getSignupDate()
                            : java.time.LocalDateTime.now()));
            ps.setBoolean(6, user.isAdmin());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tạo tài khoản mới", e);
        }
    }

    @Override
    public void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = GETDATE() WHERE id = ?";
        try (Connection conn = jdbc.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật last_login", e);
        }
    }

    private User_24133059 map(ResultSet rs) throws SQLException {
        User_24133059 u = new User_24133059();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setFullname(rs.getString("fullname"));
        int phone = rs.getInt("phone");
        u.setPhone(rs.wasNull() ? null : phone);
        u.setPasswd(rs.getString("passwd"));
        Timestamp signup = rs.getTimestamp("signup_date");
        u.setSignupDate(signup == null ? null : signup.toLocalDateTime());
        Timestamp last = rs.getTimestamp("last_login");
        u.setLastLogin(last == null ? null : last.toLocalDateTime());
        u.setAdmin(rs.getBoolean("is_admin"));
        return u;
    }
}
