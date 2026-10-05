package edu.hcmute.webpr.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Băm mật khẩu trước khi lưu xuống database.
 *
 * Vì sao dùng MD5: cấu trúc bảng users trong đề quy định cột
 * {@code passwd varchar(32)} - vừa đúng 32 ký tự hex của một chuỗi băm MD5.
 * Các thuật toán mạnh hơn (BCrypt 60 ký tự, SHA-256 64 ký tự) sẽ không vừa
 * cột, nên ở bài thi này giữ đúng thiết kế bảng của đề.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public final class PasswordUtil_24133059 {

    private PasswordUtil_24133059() {
    }

    /** Trả về chuỗi băm MD5 dạng hex thường, đúng 32 ký tự. */
    public static String md5(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Mọi JVM đều bắt buộc có MD5 nên nhánh này thực tế không xảy ra.
            throw new IllegalStateException("JVM không hỗ trợ MD5", e);
        }
    }

    /** So sánh mật khẩu người dùng gõ với chuỗi băm lấy từ database. */
    public static boolean matches(String rawPassword, String hashedInDb) {
        if (rawPassword == null || hashedInDb == null) {
            return false;
        }
        return md5(rawPassword).equalsIgnoreCase(hashedInDb.trim());
    }
}
