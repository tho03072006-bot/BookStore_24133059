package edu.hcmute.webpr.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Một dòng sách trong đơn hàng, ánh xạ bảng {@code order_detail}.
 *
 * {@code title} và {@code price} là bản chép tại thời điểm đặt hàng nên hoá
 * đơn cũ không đổi theo khi admin sửa giá hay xoá sách. {@code bookId} có thể
 * null nếu cuốn sách đã bị xoá khỏi bảng books.
 *
 * Trần Minh Thọ - 24133059
 */
public class OrderDetail_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private int detailId;
    private int orderId;
    private Integer bookId;
    private String title;
    private BigDecimal price;
    private int quantity;
    private String coverImage;

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /** Ảnh bìa lấy kèm từ bảng books khi sách vẫn còn. */
    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public BigDecimal getSubtotal() {
        return price == null ? BigDecimal.ZERO : price.multiply(BigDecimal.valueOf(quantity));
    }
}
