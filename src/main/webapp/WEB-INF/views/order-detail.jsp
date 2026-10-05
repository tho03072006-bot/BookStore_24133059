<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    Lá»ŠCH Sá»¬ Äáº¶T HÃ€NG - chi tiáº¿t má»™t Ä‘Æ¡n, kÃ¨m thanh tiáº¿n trÃ¬nh tráº¡ng thÃ¡i.
    Tráº§n Minh Thá» - 24133059
--%>
<html>
<head>
    <title>ÄÆ¡n hÃ ng #${order.orderId}</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>ÄÆ¡n hÃ ng #${order.orderId}</h1>
    <a class="btn secondary" href="${ctx}/orders">&laquo; Vá» danh sÃ¡ch Ä‘Æ¡n</a>
</div>

<div class="card">
    <div class="order-head">
        <div>
            <div class="muted">Äáº·t lÃºc ${order.orderDateText}</div>
            <div class="muted">${order.paymentMethodLabel}</div>
        </div>
        <span class="badge st-${order.status.code}">${order.status.label}</span>
    </div>

    <%-- Tiáº¿n trÃ¬nh 06 bÆ°á»›c cá»§a Ä‘Æ¡n Ä‘i suÃ´n sáº». ÄÆ¡n Ä‘Ã£ há»§y hoáº·c Ä‘Ã£ hoÃ n thÃ¬
         khÃ´ng Ä‘i theo máº¡ch nÃ y ná»¯a nÃªn hiá»‡n thÃ´ng bÃ¡o riÃªng. --%>
    <c:set var="luong" value="NEW,CONFIRMED,PREPARING,SHIPPING,DELIVERING,DELIVERED"/>
    <c:choose>
        <c:when test="${order.status.code eq 'CANCELLED' or order.status.code eq 'RETURNED'}">
            <div class="alert info" style="margin-top:14px;">
                ÄÆ¡n nÃ y Ä‘ang á»Ÿ tráº¡ng thÃ¡i <strong>${order.status.label}</strong> nÃªn khÃ´ng
                cÃ²n Ä‘i theo tiáº¿n trÃ¬nh giao hÃ ng thÃ´ng thÆ°á»ng.
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
        <h2>SÃ¡ch Ä‘Ã£ Ä‘áº·t</h2>
        <table class="data">
            <thead>
            <tr>
                <th scope="col">SÃ¡ch</th>
                <th scope="col">ÄÆ¡n giÃ¡</th>
                <th scope="col">SL</th>
                <th scope="col">ThÃ nh tiá»n</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="d" items="${order.details}">
                <tr>
                    <td>
                        <c:choose>
                            <%-- bookid null nghÄ©a lÃ  cuá»‘n sÃ¡ch Ä‘Ã£ bá»‹ xoÃ¡ khá»i
                                 cá»­a hÃ ng; tÃªn vÃ  giÃ¡ trÃªn hoÃ¡ Ä‘Æ¡n váº«n giá»¯ nguyÃªn. --%>
                            <c:when test="${empty d.bookId}">
                                <c:out value="${d.title}"/>
                                <div class="hint">SÃ¡ch nÃ y khÃ´ng cÃ²n Ä‘Æ°á»£c bÃ¡n</div>
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
                <th scope="row" colspan="3">Tá»•ng cá»™ng (${order.totalQuantity} cuá»‘n)</th>
                <th>${order.totalAmountText}</th>
            </tr>
            </tfoot>
        </table>
    </div>

    <div class="card">
        <h2>ThÃ´ng tin nháº­n hÃ ng</h2>
        <table class="data">
            <tr><th scope="row">NgÆ°á»i nháº­n</th><td><c:out value="${order.receiverName}"/></td></tr>
            <tr><th scope="row">Äiá»‡n thoáº¡i</th><td><c:out value="${order.receiverPhone}"/></td></tr>
            <tr><th scope="row">Äá»‹a chá»‰</th><td><c:out value="${order.address}"/></td></tr>
            <tr>
                <th scope="row">Ghi chÃº</th>
                <td><c:out value="${empty order.note ? '(khÃ´ng cÃ³)' : order.note}"/></td>
            </tr>
            <tr><th scope="row">Thanh toÃ¡n</th><td>${order.paymentMethodLabel}</td></tr>
        </table>

        <c:if test="${order.status.cancellable}">
            <div class="actions" style="margin-top:16px;">
                <form method="post" action="${ctx}/orders/cancel" data-confirm-cancel>
                    <input type="hidden" name="id" value="${order.orderId}">
                    <button type="submit" class="btn danger">Há»§y Ä‘Æ¡n hÃ ng</button>
                </form>
            </div>
            <div class="hint">Chá»‰ há»§y Ä‘Æ°á»£c khi Ä‘Æ¡n cÃ²n á»Ÿ tráº¡ng thÃ¡i &ldquo;ÄÆ¡n hÃ ng má»›i&rdquo;.</div>
        </c:if>
    </div>
</div>

</body>
</html>
