<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%--
    GIỎ HÀNG - xem, sửa số lượng, xóa từng dòng hoặc xóa sạch giỏ.
    Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Giỏ hàng</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Giỏ hàng</h1>
    <a class="btn secondary" href="${ctx}/products">&laquo; Tiếp tục mua sắm</a>
</div>

<c:if test="${not empty warning}">
    <div class="alert info" role="status"><c:out value="${warning}"/></div>
</c:if>

<c:choose>
    <%-- Viết "empty cart.items" chứ KHÔNG viết "cart.empty": empty là từ khoá
         của EL nên cart.empty sẽ lỗi "Failed to parse the expression". --%>
    <c:when test="${empty cart.items}">
        <div class="card" style="text-align:center;padding:40px 18px;">
            <p style="font-size:17px;margin:0 0 6px;">Giỏ hàng của bạn đang trống.</p>
            <p class="muted" style="margin:0 0 18px;">Chọn vài cuốn sách rồi quay lại nhé.</p>
            <a class="btn" href="${ctx}/products">Xem danh sách sách</a>
        </div>
    </c:when>

    <c:otherwise>
        <div class="card" style="padding:0;overflow:auto;">
            <table class="data cart-table">
                <thead>
                <tr>
                    <th scope="col">Bìa</th>
                    <th scope="col">Tên sách</th>
                    <th scope="col">Đơn giá</th>
                    <th scope="col">Số lượng</th>
                    <th scope="col">Thành tiền</th>
                    <th scope="col">Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${cart.items}">
                    <c:set var="gioiHan" value="${item.stock lt maxPerBook ? item.stock : maxPerBook}"/>
                    <tr>
                        <td>
                            <c:if test="${not empty item.coverImage}">
                                <c:url var="coverUrl" value="/image">
                                    <c:param name="name" value="${item.coverImage}"/>
                                </c:url>
                                <a href="${ctx}/book?id=${item.bookId}">
                                    <img class="book-cover small" src="${coverUrl}"
                                         loading="lazy" decoding="async" width="50" height="70"
                                         alt="Bìa <c:out value='${item.title}'/>">
                                </a>
                            </c:if>
                        </td>

                        <td>
                            <a href="${ctx}/book?id=${item.bookId}"><c:out value="${item.title}"/></a>
                            <div class="hint">Còn ${item.stock} cuốn trong kho</div>
                        </td>

                        <td><fmt:formatNumber value="${item.price}" minFractionDigits="2"/></td>

                        <td>
                            <%-- SỬA SỐ LƯỢNG: ô nhập bị chặn trong khoảng 1..giới hạn
                                 ngay trên trình duyệt, tầng Service kiểm tra lại lần nữa. --%>
                            <form class="qty-form" method="post" action="${ctx}/cart/update">
                                <input type="hidden" name="bookId" value="${item.bookId}">
                                <label class="sr-only" for="qty-${item.bookId}">
                                    Số lượng của <c:out value="${item.title}"/>
                                </label>
                                <input type="number" id="qty-${item.bookId}" name="quantity"
                                       value="${item.quantity}" min="1" max="${gioiHan}" required>
                                <button type="submit" class="btn small secondary">Cập nhật</button>
                            </form>
                            <div class="hint">Tối đa ${gioiHan} cuốn</div>
                        </td>

                        <td><strong><fmt:formatNumber value="${item.subtotal}" minFractionDigits="2"/></strong></td>

                        <td>
                            <form method="post" action="${ctx}/cart/remove" data-confirm-remove>
                                <input type="hidden" name="bookId" value="${item.bookId}">
                                <button type="submit" class="btn small danger">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="card cart-summary">
            <div>
                <div class="muted">${cart.lineCount} đầu sách &middot; ${cart.totalQuantity} cuốn</div>
                <div class="cart-total">Tổng cộng:
                    <strong><fmt:formatNumber value="${cart.totalAmount}" minFractionDigits="2"/></strong>
                </div>
            </div>

            <div class="actions">
                <form method="post" action="${ctx}/cart/clear" data-confirm-clear>
                    <button type="submit" class="btn secondary">Xóa toàn bộ giỏ</button>
                </form>
                <a class="btn" href="${ctx}/checkout">Thanh toán COD &raquo;</a>
            </div>
        </div>
    </c:otherwise>
</c:choose>

</body>
</html>
