package edu.hcmute.webpr.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Định dạng tiền tệ cho toàn bộ giao diện.
 *
 * <p><b>Quy ước đơn vị.</b> Đề thi quy định cột {@code books.price} kiểu
 * {@code decimal(6,2)}, tức giá trị tối đa chỉ là 9999.99 - không đủ để lưu giá
 * sách tính bằng đồng (một cuốn 95.000đ đã vượt trần). Vì không được sửa cấu
 * trúc bảng, giá trị trong cột được hiểu là <b>nghìn đồng</b>:</p>
 *
 * <pre>
 *     price = 95.00   -&gt;  hiển thị  95.000 &#8363;
 *     price = 78.50   -&gt;  hiển thị  78.500 &#8363;
 *     trần 9999.99    -&gt;  tương đương 9.999.990 &#8363;
 * </pre>
 *
 * <p>Nhờ vậy cấu trúc database giữ đúng 100% theo đề mà giá vẫn hiển thị hợp lý.
 * Dùng dấu chấm ngăn cách hàng nghìn theo cách viết của tiếng Việt, không phụ
 * thuộc Locale của máy chủ.</p>
 *
 * Trần Minh Thọ - 24133059
 */
public final class MoneyUtil_24133059 {

    /** 1 đơn vị trong cột price = 1.000 đồng. */
    private static final BigDecimal DON_VI = BigDecimal.valueOf(1000);

    private static final String KY_HIEU = "₫"; // ₫

    private MoneyUtil_24133059() {
    }

    /** Đổi giá trị trong database sang số tiền thật, tính bằng đồng. */
    public static BigDecimal toDong(BigDecimal giaTrongDatabase) {
        return giaTrongDatabase == null ? BigDecimal.ZERO : giaTrongDatabase.multiply(DON_VI);
    }

    /**
     * Chuỗi tiền đầy đủ để in ra màn hình, ví dụ {@code "95.000 ₫"}.
     * Giá trị null được coi là 0 để JSP không phải kiểm tra thêm.
     */
    public static String format(BigDecimal giaTrongDatabase) {
        return formatter().format(toDong(giaTrongDatabase)) + " " + KY_HIEU;
    }

    /** Như {@link #format} nhưng không kèm ký hiệu, dùng khi cột đã ghi rõ đơn vị. */
    public static String formatNoSymbol(BigDecimal giaTrongDatabase) {
        return formatter().format(toDong(giaTrongDatabase));
    }

    /*
     * DecimalFormat KHÔNG an toàn khi nhiều luồng dùng chung, mà Servlet thì
     * mỗi request một luồng - nên tạo mới mỗi lần gọi thay vì giữ một đối tượng
     * static dùng chung.
     */
    private static DecimalFormat formatter() {
        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols();
        kyHieu.setGroupingSeparator('.');
        DecimalFormat dinhDang = new DecimalFormat("#,##0", kyHieu);
        dinhDang.setGroupingUsed(true);
        return dinhDang;
    }
}
