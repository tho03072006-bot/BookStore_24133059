package edu.hcmute.webpr.service;

import edu.hcmute.webpr.model.PendingRegistration_24133059;
import edu.hcmute.webpr.model.User_24133059;

/**
 * TẦNG BUSINESS - nghiệp vụ Đăng ký (có OTP) / Đăng nhập / Đăng xuất (Câu 2).
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public interface IAuthService_24133059 {

    /**
     * Bước 1 của đăng ký: kiểm tra dữ liệu, băm mật khẩu, sinh mã OTP và gửi
     * email. Tài khoản CHƯA được ghi xuống database ở bước này.
     *
     * @return hồ sơ chờ kích hoạt, Controller sẽ cất vào Session
     * @throws IllegalArgumentException nếu dữ liệu nhập không hợp lệ
     */
    PendingRegistration_24133059 startRegistration(String email, String fullname,
                                                   String phone, String password,
                                                   String confirmPassword);

    /** Sinh mã OTP mới cho hồ sơ đang chờ và gửi lại email. */
    void resendOtp(PendingRegistration_24133059 pending);

    /**
     * Bước 2 của đăng ký: nhập đúng OTP thì mới INSERT vào bảng users.
     *
     * @return tài khoản vừa tạo
     */
    User_24133059 completeRegistration(PendingRegistration_24133059 pending);

    /**
     * Đăng nhập. Trả về null nếu sai email hoặc sai mật khẩu.
     * Đăng nhập thành công thì cập nhật cột last_login.
     */
    User_24133059 login(String email, String rawPassword);

    /** Mã OTP có được gửi đi qua email thật hay không (để báo cho người dùng). */
    boolean isMailConfigured();
}
