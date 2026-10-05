package edu.hcmute.webpr.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import edu.hcmute.webpr.util.Constants_24133059;

/**
 * CÂU 2: hồ sơ đăng ký đang CHỜ kích hoạt bằng OTP.
 *
 * Bảng {@code users} trong đề không có cột lưu mã OTP hay trạng thái kích
 * hoạt, mà đề yêu cầu "tạo cấu trúc database như trên" nên không được thêm
 * cột. Vì vậy hồ sơ đăng ký + mã OTP được giữ tạm trong Session; chỉ khi nhập
 * đúng OTP thì mới INSERT thật xuống bảng users. Cách này vừa đúng schema của
 * đề, vừa bảo đảm tài khoản chưa kích hoạt không tồn tại trong database.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class PendingRegistration_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String email;
    private final String fullname;
    private final Integer phone;
    private final String hashedPassword;

    private String otp;
    private LocalDateTime otpCreatedAt;
    private int attempts;

    public PendingRegistration_24133059(String email, String fullname, Integer phone,
                                        String hashedPassword, String otp) {
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.hashedPassword = hashedPassword;
        renewOtp(otp);
    }

    /** Cấp mã OTP mới và tính lại thời điểm hết hạn (dùng khi bấm "Gửi lại mã"). */
    public final void renewOtp(String newOtp) {
        this.otp = newOtp;
        this.otpCreatedAt = LocalDateTime.now();
        this.attempts = 0;
    }

    public boolean isExpired() {
        return otpCreatedAt.plusMinutes(Constants_24133059.OTP_EXPIRY_MINUTES)
                .isBefore(LocalDateTime.now());
    }

    /** Kiểm tra mã người dùng nhập, đồng thời đếm số lần nhập sai. */
    public boolean verify(String input) {
        attempts++;
        return otp.equals(input == null ? "" : input.trim());
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public Integer getPhone() {
        return phone;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getOtp() {
        return otp;
    }

    public int getAttempts() {
        return attempts;
    }
}
