package edu.hcmute.webpr.model;

import java.util.Arrays;
import java.util.List;

/**
 * 08 trạng thái của đơn hàng, đúng theo yêu cầu đề bài.
 *
 * Mỗi hằng số gồm mã lưu xuống cột {@code orders.status} (chữ không dấu, để
 * gõ tay trong SSMS không sợ sai dấu) và nhãn tiếng Việt hiển thị lên web.
 *
 * Trần Minh Thọ - 24133059
 */
public enum OrderStatus_24133059 {

    NEW("Đơn hàng mới"),
    CONFIRMED("Đã xác nhận"),
    PREPARING("Chuẩn bị hàng"),
    SHIPPING("Vận chuyển"),
    DELIVERING("Giao hàng"),
    DELIVERED("Đã giao"),
    CANCELLED("Đơn hàng hủy"),
    RETURNED("Đơn hàng hoàn");

    private final String label;

    OrderStatus_24133059(String label) {
        this.label = label;
    }

    /** Nhãn tiếng Việt hiển thị trên giao diện. */
    public String getLabel() {
        return label;
    }

    /** Mã lưu trong database, cũng là giá trị dùng trên URL bộ lọc. */
    public String getCode() {
        return name();
    }

    /** Đơn còn mới thì khách mới được tự hủy. */
    public boolean isCancellable() {
        return this == NEW;
    }

    public static List<OrderStatus_24133059> all() {
        return Arrays.asList(values());
    }

    /** Đọc mã từ database hoặc từ URL; sai hoặc thiếu thì trả về null. */
    public static OrderStatus_24133059 fromCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        for (OrderStatus_24133059 status : values()) {
            if (status.name().equalsIgnoreCase(code.trim())) {
                return status;
            }
        }
        return null;
    }
}
