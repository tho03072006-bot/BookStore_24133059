package edu.hcmute.webpr.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Một đơn hàng, ánh xạ bảng {@code orders}.
 *
 * Trần Minh Thọ - 24133059
 */
public class Order_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int orderId;
    private int userId;
    private LocalDateTime orderDate;
    private String receiverName;
    private String receiverPhone;
    private String address;
    private String note;
    private String paymentMethod;
    private OrderStatus_24133059 status;
    private BigDecimal totalAmount;

    private List<OrderDetail_24133059> details = new ArrayList<>();

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public OrderStatus_24133059 getStatus() {
        return status;
    }

    public void setStatus(OrderStatus_24133059 status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderDetail_24133059> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail_24133059> details) {
        this.details = (details == null) ? new ArrayList<>() : details;
    }

    /** Ngày đặt dạng dd/MM/yyyy HH:mm để in ra JSP. */
    public String getOrderDateText() {
        return orderDate == null ? "" : orderDate.format(DATE_TIME);
    }

    /** Hình thức thanh toán viết đầy đủ cho người dùng dễ hiểu. */
    public String getPaymentMethodLabel() {
        return "COD".equalsIgnoreCase(paymentMethod)
                ? "COD - Thanh toán khi nhận hàng" : paymentMethod;
    }

    public int getTotalQuantity() {
        int total = 0;
        for (OrderDetail_24133059 detail : details) {
            total += detail.getQuantity();
        }
        return total;
    }
}
