package edu.hcmute.webpr.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import edu.hcmute.webpr.model.Cart_24133059;
import edu.hcmute.webpr.model.User_24133059;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Các hàm nhỏ dùng đi dùng lại trong tầng Presentation: đọc tham số từ URL,
 * lấy người dùng đang đăng nhập trong Session.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public final class ServletUtil_24133059 {

    private ServletUtil_24133059() {
    }

    /** Đọc tham số kiểu int; sai định dạng hoặc thiếu thì trả về giá trị mặc định. */
    public static int intParam(HttpServletRequest req, String name, int defaultValue) {
        String raw = req.getParameter(name);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Đọc tham số kiểu int nhưng cho phép "không có" (trả về null). */
    public static Integer optionalIntParam(HttpServletRequest req, String name) {
        String raw = req.getParameter(name);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static BigDecimal optionalDecimalParam(HttpServletRequest req, String name) {
        String raw = req.getParameter(name);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(raw.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá trị \"" + raw + "\" không phải là số hợp lệ.");
        }
    }

    public static LocalDate optionalDateParam(HttpServletRequest req, String name) {
        String raw = req.getParameter(name);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày \"" + raw + "\" không đúng định dạng yyyy-MM-dd.");
        }
    }

    /** Chuỗi đã cắt khoảng trắng; null thì trả về chuỗi rỗng. */
    public static String stringParam(HttpServletRequest req, String name) {
        String raw = req.getParameter(name);
        return raw == null ? "" : raw.trim();
    }

    /** Người dùng đang đăng nhập, hoặc null nếu chưa đăng nhập. */
    public static User_24133059 currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object attr = session.getAttribute(Constants_24133059.SESSION_USER);
        return (attr instanceof User_24133059 user) ? user : null;
    }

    /** Ghi thông báo một lần vào Session để hiện sau khi redirect. */
    public static void flash(HttpServletRequest req, String key, String message) {
        req.getSession().setAttribute(key, message);
    }

    /**
     * Giỏ hàng trong Session; chưa có thì tạo mới và gắn vào Session luôn.
     * Giỏ gắn với Session nên đăng xuất là mất giỏ, đúng như mong đợi.
     */
    public static Cart_24133059 cart(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        synchronized (session) {
            Object attr = session.getAttribute(Constants_24133059.SESSION_CART);
            if (attr instanceof Cart_24133059 cart) {
                return cart;
            }
            Cart_24133059 cart = new Cart_24133059();
            session.setAttribute(Constants_24133059.SESSION_CART, cart);
            return cart;
        }
    }
}
