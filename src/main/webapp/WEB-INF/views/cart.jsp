<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    GIá»Ž HÃ€NG - xem, sá»­a sá»‘ lÆ°á»£ng, xÃ³a tá»«ng dÃ²ng hoáº·c xÃ³a sáº¡ch giá».
    Tráº§n Minh Thá» - 24133059
--%>
<html>
<head>
    <title>Giá» hÃ ng</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Giá» hÃ ng</h1>
    <a class="btn secondary" href="${ctx}/products">&laquo; Tiáº¿p tá»¥c mua sáº¯m</a>
</div>

<c:if test="${not empty warning}">
    <div class="alert info" role="status"><c:out value="${warning}"/></div>
</c:if>

<c:choose>
    <%-- Viáº¿t "empty cart.items" chá»© KHÃ”NG viáº¿t "cart.empty": empty lÃ  tá»« khoÃ¡
         cá»§a EL nÃªn cart.empty sáº½ lá»—i "Failed to parse the expression". --%>
    <c:when test="${empty cart.items}">
        <div class="card" style="text-align:center;padding:40px 18px;">
            <p style="font-size:17px;margin:0 0 6px;">Giá» hÃ ng cá»§a báº¡n Ä‘ang trá»‘ng.</p>
            <p class="muted" style="margin:0 0 18px;">Chá»n vÃ i cuá»‘n sÃ¡ch rá»“i quay láº¡i nhÃ©.</p>
            <a class="btn" href="${ctx}/products">Xem danh sÃ¡ch sÃ¡ch</a>
        </div>
    </c:when>

    <c:otherwise>
        <div class="card" style="padding:0;overflow:auto;">
            <table class="data cart-table">
                <thead>
                <tr>
                    <th scope="col">BÃ¬a</th>
                    <th scope="col">TÃªn sÃ¡ch</th>
                    <th scope="col">ÄÆ¡n giÃ¡</th>
                    <th scope="col">Sá»‘ lÆ°á»£ng</th>
                    <th scope="col">ThÃ nh tiá»n</th>
                    <th scope="col">Thao tÃ¡c</th>
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
                                         alt="BÃ¬a <c:out value='${item.title}'/>">
                                </a>
                            </c:if>
                        </td>

                        <td>
                            <a href="${ctx}/book?id=${item.bookId}"><c:out value="${item.title}"/></a>
                            <div class="hint">CÃ²n ${item.stock} cuá»‘n trong kho</div>
                        </td>

                        <td>${item.priceText}</td>

                        <td>
                            <%-- Sá»¬A Sá» LÆ¯á»¢NG: Ã´ nháº­p bá»‹ cháº·n trong khoáº£ng 1..giá»›i háº¡n
                                 ngay trÃªn trÃ¬nh duyá»‡t, táº§ng Service kiá»ƒm tra láº¡i láº§n ná»¯a. --%>
                            <form class="qty-form" method="post" action="${ctx}/cart/update">
                                <input type="hidden" name="bookId" value="${item.bookId}">
                                <label class="sr-only" for="qty-${item.bookId}">
                                    Sá»‘ lÆ°á»£ng cá»§a <c:out value="${item.title}"/>
                                </label>
                                <input type="number" id="qty-${item.bookId}" name="quantity"
                                       value="${item.quantity}" min="1" max="${gioiHan}" required>
                                <button type="submit" class="btn small secondary">Cáº­p nháº­t</button>
                            </form>
                            <div class="hint">Tá»‘i Ä‘a ${gioiHan} cuá»‘n</div>
                        </td>

                        <td><strong>${item.subtotalText}</strong></td>

                        <td>
                            <form method="post" action="${ctx}/cart/remove" data-confirm-remove>
                                <input type="hidden" name="bookId" value="${item.bookId}">
                                <button type="submit" class="btn small danger">XÃ³a</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="card cart-summary">
            <div>
                <div class="muted">${cart.lineCount} Ä‘áº§u sÃ¡ch &middot; ${cart.totalQuantity} cuá»‘n</div>
                <div class="cart-total">Tá»•ng cá»™ng:
                    <strong>${cart.totalAmountText}</strong>
                </div>
            </div>

            <div class="actions">
                <form method="post" action="${ctx}/cart/clear" data-confirm-clear>
                    <button type="submit" class="btn secondary">XÃ³a toÃ n bá»™ giá»</button>
                </form>
                <a class="btn" href="${ctx}/checkout">Thanh toÃ¡n COD &raquo;</a>
            </div>
        </div>
    </c:otherwise>
</c:choose>

</body>
</html>
