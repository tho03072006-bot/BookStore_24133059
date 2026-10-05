package edu.hcmute.webpr.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Giỏ hàng của một người dùng, sống trong Session nên không cần bảng riêng
 * trong database. Chỉ khi bấm "Đặt hàng" thì giỏ mới được ghi xuống thành
 * một dòng {@code orders} kèm các dòng {@code order_detail}.
 *
 * Dùng LinkedHashMap để các dòng giữ nguyên thứ tự khách bỏ vào giỏ.
 *
 * Trần Minh Thọ - 24133059
 */
public class Cart_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Integer, CartItem_24133059> items = new LinkedHashMap<>();

    /** Các dòng trong giỏ, theo thứ tự đã thêm. */
    public List<CartItem_24133059> getItems() {
        return new ArrayList<>(items.values());
    }

    public CartItem_24133059 find(int bookId) {
        return items.get(bookId);
    }

    public boolean contains(int bookId) {
        return items.containsKey(bookId);
    }

    public void put(CartItem_24133059 item) {
        items.put(item.getBookId(), item);
    }

    public void remove(int bookId) {
        items.remove(bookId);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Số đầu sách khác nhau trong giỏ. */
    public int getLineCount() {
        return items.size();
    }

    /** Tổng số cuốn, dùng cho con số nhỏ trên biểu tượng giỏ hàng. */
    public int getTotalQuantity() {
        int total = 0;
        for (CartItem_24133059 item : items.values()) {
            total += item.getQuantity();
        }
        return total;
    }

    /** Tổng tiền của cả giỏ. */
    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem_24133059 item : items.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    /** Tổng tiền đã định dạng, ví dụ "160.000 ₫". */
    public String getTotalAmountText() {
        return edu.hcmute.webpr.util.MoneyUtil_24133059.format(getTotalAmount());
    }
}
