package edu.hcmute.webpr.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Một dòng trong giỏ hàng.
 *
 * Tên sách, giá và ảnh bìa được chép lại lúc bỏ vào giỏ nên giỏ hàng vẫn hiển
 * thị được dù sau đó admin có sửa sách. {@code stock} là số tồn kho đọc từ
 * {@code books.quantity}, dùng để chặn tăng số lượng vượt quá hàng đang có.
 *
 * Trần Minh Thọ - 24133059
 */
public class CartItem_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int bookId;
    private final String title;
    private final BigDecimal price;
    private final String coverImage;

    private int quantity;
    private int stock;

    public CartItem_24133059(Book_24133059 book, int quantity) {
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.price = book.getPrice() == null ? BigDecimal.ZERO : book.getPrice();
        this.coverImage = book.getCoverImage();
        this.stock = book.getQuantity() == null ? 0 : book.getQuantity();
        this.quantity = quantity;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    /** Thành tiền của dòng này. */
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public String getPriceText() {
        return edu.hcmute.webpr.util.MoneyUtil_24133059.format(price);
    }

    public String getSubtotalText() {
        return edu.hcmute.webpr.util.MoneyUtil_24133059.format(getSubtotal());
    }
}
