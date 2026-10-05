package edu.hcmute.webpr.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import edu.hcmute.webpr.dao.IUserDao_24133059;
import edu.hcmute.webpr.dao.UserDao_24133059;
import edu.hcmute.webpr.model.PendingRegistration_24133059;
import edu.hcmute.webpr.model.User_24133059;
import edu.hcmute.webpr.util.Constants_24133059;
import edu.hcmute.webpr.util.MailUtil_24133059;
import edu.hcmute.webpr.util.PasswordUtil_24133059;
import jakarta.mail.MessagingException;

/**
 * TẦNG BUSINESS - hiện thực nghiệp vụ Đăng ký có OTP / Đăng nhập (Câu 2).
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class AuthService_24133059 implements IAuthService_24133059 {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private static final SecureRandom RANDOM = new SecureRandom();

    private final IUserDao_24133059 userDao = new UserDao_24133059();

    @Override
    public PendingRegistration_24133059 startRegistration(String email, String fullname,
                                                          String phone, String password,
                                                          String confirmPassword) {
        String mail = trim(email);
        String name = trim(fullname);
        String phoneRaw = trim(phone);

        if (mail.isEmpty() || !EMAIL_PATTERN.matcher(mail).matches()) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }
        if (mail.length() > 50) {
            throw new IllegalArgumentException("Email tối đa 50 ký tự.");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Họ tên tối đa 50 ký tự.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu nhập lại không khớp.");
        }
        if (userDao.existsByEmail(mail)) {
            throw new IllegalArgumentException("Email này đã được đăng ký. Mời bạn đăng nhập.");
        }

        Integer phoneValue = parsePhone(phoneRaw);

        PendingRegistration_24133059 pending = new PendingRegistration_24133059(
                mail, name, phoneValue, PasswordUtil_24133059.md5(password), generateOtp());
        deliverOtp(pending);
        return pending;
    }

    @Override
    public void resendOtp(PendingRegistration_24133059 pending) {
        pending.renewOtp(generateOtp());
        deliverOtp(pending);
    }

    @Override
    public User_24133059 completeRegistration(PendingRegistration_24133059 pending) {
        if (userDao.existsByEmail(pending.getEmail())) {
            // Trường hợp hiếm: email được đăng ký ở phiên khác trong lúc chờ OTP.
            throw new IllegalArgumentException("Email này vừa được đăng ký ở nơi khác.");
        }
        User_24133059 user = new User_24133059();
        user.setEmail(pending.getEmail());
        user.setFullname(pending.getFullname());
        user.setPhone(pending.getPhone());
        user.setPasswd(pending.getHashedPassword());
        user.setSignupDate(LocalDateTime.now());
        user.setAdmin(false); // tài khoản tự đăng ký luôn là vai trò User

        int newId = userDao.insert(user);
        user.setId(newId);
        return user;
    }

    @Override
    public User_24133059 login(String email, String rawPassword) {
        String mail = trim(email);
        if (mail.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            return null;
        }
        User_24133059 user = userDao.findByEmail(mail);
        if (user == null || !PasswordUtil_24133059.matches(rawPassword, user.getPasswd())) {
            return null;
        }
        userDao.updateLastLogin(user.getId());
        return user;
    }

    @Override
    public boolean isMailConfigured() {
        return MailUtil_24133059.isConfigured();
    }

    /** Sinh mã OTP 6 chữ số bằng SecureRandom. */
    private String generateOtp() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    /**
     * Chỉ in OTP trong chế độ demo. Khi SMTP đã cấu hình, lỗi gửi được báo lại
     * cho người dùng, không làm lộ mã trong log.
     */
    private void deliverOtp(PendingRegistration_24133059 pending) {
        if (!MailUtil_24133059.isConfigured()) {
            pending.setEmailSent(false);
            System.err.println("[OTP-DEMO] Ma OTP cho " + pending.getEmail() + " la: " + pending.getOtp());
            return;
        }
        try {
            MailUtil_24133059.sendOtp(pending.getEmail(), pending.getOtp());
            pending.setEmailSent(true);
            System.out.println("[OTP] Da gui ma OTP toi " + pending.getEmail());
        } catch (MessagingException e) {
            pending.setEmailSent(false);
            System.err.println("[OTP] SMTP khong gui duoc email: " + e.getClass().getSimpleName());
            throw new IllegalArgumentException("Chưa gửi được mã kích hoạt. Vui lòng thử lại sau.");
        }
    }

    /**
     * Cột phone kiểu int nên số 0 đứng đầu không lưu được: "0912345678" sẽ được
     * quy về 912345678. Để trống thì trả về null.
     */
    private Integer parsePhone(String phoneRaw) {
        if (phoneRaw.isEmpty()) {
            return null;
        }
        String digits = phoneRaw.replaceAll("[\\s.-]", "");
        if (!digits.matches("0[0-9]{9}")) {
            throw new IllegalArgumentException("Số điện thoại cần gồm 10 chữ số, bắt đầu bằng 0.");
        }
        return Integer.valueOf(digits.substring(1));
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

}
