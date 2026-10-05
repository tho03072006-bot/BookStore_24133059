package edu.hcmute.webpr.dao;

import edu.hcmute.webpr.model.User_24133059;

/**
 * TẦNG DATA ACCESS - hợp đồng truy xuất bảng {@code users} (Câu 2).
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public interface IUserDao_24133059 {

    User_24133059 findByEmail(String email);

    boolean existsByEmail(String email);

    /** Thêm tài khoản mới sau khi nhập đúng OTP. Trả về id vừa sinh. */
    int insert(User_24133059 user);

    /** Ghi lại thời điểm đăng nhập gần nhất vào cột last_login. */
    void updateLastLogin(int userId);
}
