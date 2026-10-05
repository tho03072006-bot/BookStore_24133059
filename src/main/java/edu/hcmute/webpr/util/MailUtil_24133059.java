package edu.hcmute.webpr.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * CÂU 2: gửi mã OTP kích hoạt tài khoản qua email (Jakarta Mail + SMTP).
 *
 * Cấu hình SMTP đọc từ {@code src/main/resources/email.properties} chứ không
 * hard-code trong code. Nếu thiếu file cấu hình hoặc SMTP lỗi, hàm gửi sẽ ném
 * MessagingException để Servlet bắt và in mã OTP ra Console - nhờ vậy vẫn demo
 * được luồng đăng ký khi máy không có mạng.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public final class MailUtil_24133059 {

    private static final Properties CONFIG = loadConfig();

    private MailUtil_24133059() {
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = MailUtil_24133059.class.getClassLoader()
                .getResourceAsStream("email.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                // Thông báo cho Console viết không dấu: cửa sổ Console của
                // Tomcat trên Windows thường không dùng bảng mã UTF-8 nên chữ
                // có dấu in ra sẽ bị vỡ font.
                System.err.println("[MailUtil] Khong tim thay email.properties trong classpath"
                        + " - ma OTP se chi duoc in ra Console.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    /** Có đủ tài khoản SMTP để gửi mail thật hay không. */
    public static boolean isConfigured() {
        String user = CONFIG.getProperty("mail.username", "").trim();
        String pass = CONFIG.getProperty("mail.password", "").trim();
        return !user.isEmpty() && !pass.isEmpty();
    }

    /** Gửi một email chứa mã OTP tới địa chỉ toEmail. */
    public static void sendOtp(String toEmail, String otp) throws MessagingException {
        if (!isConfigured()) {
            throw new MessagingException("Chua cau hinh tai khoan SMTP trong email.properties");
        }

        String host = CONFIG.getProperty("mail.smtp.host", "smtp.gmail.com");
        String port = CONFIG.getProperty("mail.smtp.port", "587");
        final String username = CONFIG.getProperty("mail.username", "").trim();
        final String password = CONFIG.getProperty("mail.password", "").trim();
        String from = CONFIG.getProperty("mail.from", username).trim();

        Properties smtp = new Properties();
        smtp.put("mail.smtp.host", host);
        smtp.put("mail.smtp.port", port);
        smtp.put("mail.smtp.auth", "true");
        smtp.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(smtp, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from,
                    "BookStore " + Constants_24133059.SV_MSSV, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // Mọi JVM đều hỗ trợ UTF-8; nhánh này chỉ để thoả chữ ký hàm.
            message.setFrom(new InternetAddress(from));
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Mã OTP kích hoạt tài khoản BookStore", "UTF-8");
        message.setText(
                "Xin chào,\n\n"
                + "Mã OTP kích hoạt tài khoản BookStore của bạn là: " + otp + "\n\n"
                + "Mã có hiệu lực trong " + Constants_24133059.OTP_EXPIRY_MINUTES + " phút.\n"
                + "Nếu bạn không thực hiện đăng ký, vui lòng bỏ qua email này.\n\n"
                + "-- Đồ án môn Lập trình Web, đề số " + Constants_24133059.SV_MA_DE + "\n"
                + Constants_24133059.SV_HO_TEN + " - " + Constants_24133059.SV_MSSV,
                "UTF-8");

        Transport.send(message);
    }
}
