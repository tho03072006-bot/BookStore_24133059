package edu.hcmute.webpr.service;

import edu.hcmute.webpr.model.Cart_24133059;

/**
 * TẦNG BUSINESS - nghiệp vụ giỏ hàng: thêm, sửa số lượng, xóa, xóa sạch.
 *
 * Mọi thao tác đều kiểm tra số lượng nằm trong giới hạn cho phép (tối thiểu 1,
 * tối đa là số tồn kho của cuốn sách và trần {@code CART_MAX_QUANTITY_PER_BOOK}).
 * Sai thì ném {@link IllegalArgumentException} kèm thông báo tiếng Việt để
 * Controller hiện lên cho người dùng.
 *
 * Trần Minh Thọ - 24133059
 */
public interface ICartService_24133059 {

    /** Thêm một cuốn vào giỏ; đã có sẵn trong giỏ thì cộng dồn số lượng. */
    void addToCart(Cart_24133059 cart, int bookId, int quantity);

    /** Đặt lại số lượng của một dòng trong giỏ. */
    void updateQuantity(Cart_24133059 cart, int bookId, int quantity);

    /** Bỏ một cuốn khỏi giỏ. */
    void removeFromCart(Cart_24133059 cart, int bookId);

    /** Xóa sạch giỏ hàng. */
    void clear(Cart_24133059 cart);

    /**
     * Đọc lại tồn kho và giá mới nhất cho mọi dòng trong giỏ, cắt bớt số lượng
     * nếu kho đã vơi đi. Gọi trước khi hiện trang giỏ hàng và trước khi đặt
     * hàng, vì giỏ nằm trong Session nên có thể đã cũ so với database.
     *
     * @return thông báo cho người dùng nếu có dòng bị điều chỉnh, ngược lại null
     */
    String refresh(Cart_24133059 cart);
}
