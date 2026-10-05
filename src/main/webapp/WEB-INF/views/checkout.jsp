<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    THANH TOÁN COD - nhập thông tin nhận hàng rồi chốt đơn.
    Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Thanh toán COD</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Thanh toán</h1>
    <a class="btn secondary" href="${ctx}/cart">&laquo; Quay lại giỏ hàng</a>
</div>

<c:if test="${not empty error}">
    <div class="alert error" role="alert"><c:out value="${error}"/></div>
</c:if>

<div class="checkout-grid">

    <%-- ----------------------- Thông tin nhận hàng ----------------------- --%>
    <div class="card">
        <h2>Thông tin nhận hàng</h2>

        <form method="post" action="${ctx}/checkout">

            <div class="form-row">
                <label for="receiverName">Người nhận <span class="muted">(bắt buộc)</span></label>
                <input type="text" id="receiverName" name="receiverName" required maxlength="50"
                       value="<c:out value='${receiverName}'/>" placeholder="Nguyễn Văn A">
            </div>

            <div class="form-row">
                <label for="receiverPhone">Số điện thoại <span class="muted">(bắt buộc)</span></label>
                <input type="tel" id="receiverPhone" name="receiverPhone" required
                       pattern="0[0-9]{9,10}" maxlength="11"
                       value="<c:out value='${receiverPhone}'/>" placeholder="0912345678">
                <div class="hint">10 hoặc 11 chữ số, bắt đầu bằng số 0.</div>
            </div>

            <div class="form-row">
                <label for="address">Địa chỉ nhận hàng <span class="muted">(bắt buộc)</span></label>
                <input type="text" id="address" name="address" required maxlength="200"
                       value="<c:out value='${address}'/>"
                       placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành">
            </div>

            <div class="form-row">
                <label for="note">Ghi chú cho người giao hàng</label>
                <textarea id="note" name="note" maxlength="200"
                          placeholder="Ví dụ: giao giờ hành chính"><c:out value="${note}"/></textarea>
            </div>

            <div class="form-row">
                <label>Hình thức thanh toán</label>
                <div class="payment-box">
                    <input type="radio" id="cod" name="paymentMethod" value="COD" checked>
                    <label for="cod">
                        <strong>COD &ndash; Thanh toán khi nhận hàng</strong>
                        <span class="hint">Bạn trả tiền mặt trực tiếp cho người giao hàng.
                            Cửa hàng hiện chỉ hỗ trợ hình thức này.</span>
                    </label>
                </div>
            </div>

            <div class="actions">
                <button type="submit" class="btn">Đặt hàng</button>
                <a class="btn secondary" href="${ctx}/cart">Huỷ</a>
            </div>
        </form>
    </div>

    <%-- --------------------------- Tóm tắt đơn --------------------------- --%>
    <div class="card">
        <h2>Đơn hàng của bạn</h2>

        <table class="data">
            <thead>
            <tr>
                <th scope="col">Sách</th>
                <th scope="col">SL</th>
                <th scope="col">Thành tiền</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${cart.items}">
                <tr>
                    <td><c:out value="${item.title}"/></td>
                    <td>${item.quantity}</td>
                    <td>${item.subtotalText}</td>
                </tr>
            </c:forEach>
            </tbody>
            <tfoot>
            <tr>
                <th scope="row" colspan="2">Tổng cộng</th>
                <th>${cart.totalAmountText}</th>
            </tr>
            </tfoot>
        </table>

        <p class="hint" style="margin-top:12px;">
            Đặt xong, đơn ở trạng thái <strong>Đơn hàng mới</strong>. Bạn theo dõi
            tiến trình ở mục <a href="${ctx}/orders">Đơn hàng của tôi</a>.
        </p>
    </div>
</div>

</body>
</html>
