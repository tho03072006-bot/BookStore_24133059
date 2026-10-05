package edu.hcmute.webpr.util;

/**
 * Hằng số dùng chung cho toàn project - gom một chỗ để không phải sửa rải rác.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public final class Constants_24133059 {

    private Constants_24133059() {
    }

    /** Thông tin hiển thị ở Footer theo yêu cầu Câu 1. */
    public static final String SV_HO_TEN = "Trần Minh Thọ";
    public static final String SV_MSSV = "24133059";
    public static final String SV_MA_DE = "02";

    /** Câu 3: trang home phân trang 03 sách / trang cho mỗi tác giả. */
    public static final int HOME_PAGE_SIZE = 3;

    /** Trang "Sản phẩm" trên menu - lưới sách, 9 cuốn / trang. */
    public static final int PRODUCT_PAGE_SIZE = 9;

    /** Câu 6: bảng CRUD Books trong trang quản trị - 5 dòng / trang. */
    public static final int ADMIN_PAGE_SIZE = 5;

    /** Câu 2: số phút mã OTP còn hiệu lực kể từ lúc gửi. */
    public static final int OTP_EXPIRY_MINUTES = 5;

    /** Thư mục vật lý lưu ảnh bìa do admin tải lên (nằm NGOÀI project để
     *  không bị xoá mỗi lần build lại file .war). */
    public static final String UPLOAD_DIR = "D:\\WEB\\uploads\\BookStore_24133059";

    /** Thư mục ảnh bìa mẫu nằm sẵn trong webapp. */
    public static final String COVER_DIR_IN_WEBAPP = "/assets/covers/";

    /** Số đơn hàng hiển thị trên một trang ở "Đơn hàng của tôi". */
    public static final int ORDER_PAGE_SIZE = 5;

    /**
     * Giới hạn số lượng tối đa cho MỘT đầu sách trong giỏ. Giới hạn thật sự
     * còn phụ thuộc số tồn kho của cuốn đó (cột books.quantity); hệ thống lấy
     * giá trị nhỏ hơn trong hai cái.
     */
    public static final int CART_MAX_QUANTITY_PER_BOOK = 20;

    // ---- Tên các thuộc tính lưu trong Session ----
    public static final String SESSION_USER = "currentUser";
    public static final String SESSION_PENDING = "pendingRegistration";
    public static final String SESSION_CART = "cart";
}
