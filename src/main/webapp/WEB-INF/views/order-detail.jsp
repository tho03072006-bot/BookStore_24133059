<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    LỊCH SỬ ĐẶT HÀNG - chi tiết một đơn, kèm thanh tiến trình trạng thái.
    Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Đơn hàng #${order.orderId}</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Đơn hàng #${order.orderId}</h1>
    <a class="btn secondary" href="${ctx}/orders">&laquo; Về danh sách đơn</a>
</div>

<div class="card">
    <div class="order-head">
        <div>
            <div class="muted">Đặt lúc ${order.orderDateText}</div>
            <div class="muted">${order.paymentMethodLabel}</div>
        </div>
        <span class="badge st-${order.status.code}">${order.status.label}</span>
    </div>

    <%-- Tiến trình 06 bước của đơn đi suôn sẻ. Đơn đã hủy hoặc đã hoàn thì
         không đi theo mạch này nữa nên hiện thông báo riêng. --%>
    <c:set var="luong" value="NEW,CONFIRMED,PREPARING,SHIPPING,DELIVERING,DELIVERED"/>
    <c:choose>
        <c:when test="${order.status.code eq 'CANCELLED' or order.status.code eq 'RETURNED'}">
            <div class="alert info" style="margin-top:14px;">
                Đơn này đang ở trạng thái <strong>${order.status.label}</strong> nên không
                còn đi theo tiến trình giao hàng thông thường.
            </div>
        </c:when>
        <c:otherwise>
            <c:set var="viTri" value="0"/>
            <c:forEach var="ma" items="${luong}" varStatus="vs">
                <c:if test="${ma eq order.status.code}"><c:set var="viTri" value="${vs.index}"/></c:if>
            </c:forEach>

            <ol class="timeline">
                <c:forEach var="ma" items="${luong}" varStatus="vs">
                    <c:forEach var="st" items="${statuses}">
                        <c:if test="${st.code eq ma}">
                            <li class="${vs.index le viTri ? 'done' : ''} ${vs.index eq viTri ? 'current' : ''}">
                                <span class="dot" aria-hidden="true"></span>
                                <span class="timeline-label">${st.label}</span>
                            </li>
                        </c:if>
                    </c:forEach>
                </c:forEach>
            </ol>
        </c:otherwise>
    </c:choose>
</div>

<div class="checkout-grid">
    <div class="card">
        <h2>Sách đã đặt</h2>
        <table class="data">
            <thead>
            <tr>
                <th scope="col">Sách</th>
                <th scope="col">Đơn giá</th>
                <th scope="col">SL</th>
                <th scope="col">Thành tiền</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="d" items="${order.details}">
                <tr>
                    <td>
                        <c:choose>
                            <%-- bookid null nghĩa là cuốn sách đã bị xoá khỏi
                                 cửa hàng; tên và giá trên hoá đơn vẫn giữ nguyên. --%>
                            <c:when test="${empty d.bookId}">
                                <c:out value="${d.title}"/>
                                <div class="hint">Sách này không còn được bán</div>
                            </c:when>
                            <c:otherwise>
                                <a href="${ctx}/book?id=${d.bookId}"><c:out value="${d.title}"/></a>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${d.priceText}</td>
                    <td>${d.quantity}</td>
                    <td>${d.subtotalText}</td>
                </tr>
            </c:forEach>
            </tbody>
            <tfoot>
            <tr>
                <th scope="row" colspan="3">Tổng cộng (${order.totalQuantity} cuốn)</th>
                <th>${order.totalAmountText}</th>
            </tr>
            </tfoot>
        </table>
    </div>

    <div class="card">
        <h2>Thông tin nhận hàng</h2>
        <table class="data">
            <tr><th scope="row">Người nhận</th><td><c:out value="${order.receiverName}"/></td></tr>
            <tr><th scope="row">Điện thoại</th><td><c:out value="${order.receiverPhone}"/></td></tr>
            <tr><th scope="row">Địa chỉ</th><td><c:out value="${order.address}"/></td></tr>
            <tr>
                <th scope="row">Ghi chú</th>
                <td><c:out value="${empty order.note ? '(không có)' : order.note}"/></td>
            </tr>
            <tr><th scope="row">Thanh toán</th><td>${order.paymentMethodLabel}</td></tr>
        </table>

        <c:if test="${order.status.cancellable}">
            <div class="actions" style="margin-top:16px;">
                <form method="post" action="${ctx}/orders/cancel" data-confirm-cancel>
                    <input type="hidden" name="id" value="${order.orderId}">
                    <button type="submit" class="btn danger">Hủy đơn hàng</button>
                </form>
            </div>
            <div class="hint">Chỉ hủy được khi đơn còn ở trạng thái &ldquo;Đơn hàng mới&rdquo;.</div>
        </c:if>
    </div>
</div>

</body>
</html>
