package edu.hcmute.webpr.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Model ánh xạ bảng {@code books}.
 *
 * Ngoài các cột của bảng, model mang thêm hai thông tin được tính khi truy vấn
 * để JSP hiển thị đúng mẫu của đề mà không phải gọi thêm query trong view:
 * <ul>
 *   <li>{@code authors} - danh sách tác giả (lấy qua bảng book_author)</li>
 *   <li>{@code reviewCount} - số review, dùng cho dòng "Review (10)"</li>
 * </ul>
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class Book_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private int bookId;
    private Integer isbn;
    private String title;
    private String publisher;
    private BigDecimal price;
    private String description;
    private LocalDate publishDate;
    private String coverImage;
    private Integer quantity;

    private List<Author_24133059> authors = new ArrayList<>();
    private int reviewCount;
    private Double averageRating;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Integer getIsbn() {
        return isbn;
    }

    public void setIsbn(Integer isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<Author_24133059> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author_24133059> authors) {
        this.authors = (authors == null) ? new ArrayList<>() : authors;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    /** Giá đã định dạng để hiển thị, ví dụ "95.000 ₫". */
    public String getPriceText() {
        return price == null ? "" : edu.hcmute.webpr.util.MoneyUtil_24133059.format(price);
    }

    /** publish_date dạng dd/MM/yyyy để hiển thị trên JSP. */
    public String getPublishDateText() {
        return publishDate == null ? ""
                : publishDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /** publish_date dạng yyyy-MM-dd để đổ vào ô &lt;input type="date"&gt;. */
    public String getPublishDateInput() {
        return publishDate == null ? "" : publishDate.toString();
    }

    /** Chuỗi tên các tác giả, ngăn cách bởi dấu phẩy - dùng cho dòng "Tác giả:". */
    public String getAuthorNames() {
        if (authors.isEmpty()) {
            return "(chưa có)";
        }
        StringBuilder sb = new StringBuilder();
        for (Author_24133059 a : authors) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(a.getAuthorName());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Book_24133059[" + bookId + " - " + title + "]";
    }
}
