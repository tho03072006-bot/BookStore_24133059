package edu.hcmute.webpr.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * Cấu hình mẫu trong classpath; tài khoản thật đọc từ biến môi trường hoặc
 * BOOKSTORE_EMAIL_CONFIG (file ngoài WAR). Chế độ console chỉ dành cho demo.
 * SMTP đã cấu hình nhưng gửi lỗi thì không đưa mã thật vào log.
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
        String external = System.getenv("BOOKSTORE_EMAIL_CONFIG");
        String tomcatBase = System.getProperty("catalina.base");
        if ((external == null || external.isBlank()) && tomcatBase != null) {
            Path local = Path.of(tomcatBase, "conf", "bookstore-email.properties");
            if (Files.isRegularFile(local)) external = local.toString();
        }
        if (external != null && !external.isBlank()) {
            try (InputStream in = Files.newInputStream(Path.of(external))) {
                props.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("Khong doc duoc file cau hinh SMTP rieng", e);
            }
        }
        override(props, "mail.username", "BOOKSTORE_MAIL_USER");
        override(props, "mail.password", "BOOKSTORE_MAIL_PASSWORD");
        override(props, "mail.from", "BOOKSTORE_MAIL_FROM");
        return props;
    }

    private static void override(Properties props, String key, String env) {
        String value = System.getenv(env);
        if (value != null) props.setProperty(key, value);
    }

    /** Có đủ tài khoản SMTP để gửi mail thật hay không. */
    public static boolean isConfigured() {
        if ("console".equalsIgnoreCase(System.getenv("BOOKSTORE_MAIL_MODE"))) return false;
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
        String from = CONFIG.getProperty("mail.from", "").trim();
        if (from.isEmpty()) from = username;

        Properties smtp = new Properties();
        smtp.put("mail.smtp.host", host);
        smtp.put("mail.smtp.port", port);
        smtp.put("mail.smtp.auth", "true");
        smtp.put("mail.smtp.starttls.enable", "true");
        smtp.put("mail.smtp.starttls.required", "true");
        smtp.put("mail.smtp.connectiontimeout", "10000");
        smtp.put("mail.smtp.timeout", "10000");
        smtp.put("mail.smtp.writetimeout", "10000");

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
