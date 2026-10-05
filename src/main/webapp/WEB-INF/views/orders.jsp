<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    Lá»ŠCH Sá»¬ Äáº¶T HÃ€NG - danh sÃ¡ch Ä‘Æ¡n cá»§a ngÆ°á»i dÃ¹ng, Lá»ŒC THEO TRáº NG THÃI.
    Má»—i tab lÃ  má»™t tráº¡ng thÃ¡i trong 08 tráº¡ng thÃ¡i Ä‘á» bÃ i yÃªu cáº§u.
    Tráº§n Minh Thá» - 24133059
--%>
<html>
<head>
    <title>ÄÆ¡n hÃ ng cá»§a tÃ´i</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>ÄÆ¡n hÃ ng cá»§a tÃ´i</h1>
    <a class="btn secondary" href="${ctx}/products">Mua thÃªm sÃ¡ch</a>
</div>

<%-- -------------------------- Bá»™ lá»c tráº¡ng thÃ¡i -------------------------- --%>
<nav class="status-filter" aria-label="Lá»c Ä‘Æ¡n hÃ ng theo tráº¡ng thÃ¡i">
    <a href="${ctx}/orders" class="chip ${empty currentStatus ? 'active' : ''}">
        Táº¥t cáº£ <span class="chip-count">${totalAll}</span>
    </a>
    <c:forEach var="st" items="${statuses}">
        <a href="${ctx}/orders?status=${st.code}"
           class="chip st-${st.code} ${currentStatus eq st ? 'active' : ''}">
            ${st.label}
            <span class="chip-count">${empty counts[st.code] ? 0 : counts[st.code]}</span>
        </a>
    </c:forEach>
</nav>

<%-- ---------------------------- Danh sÃ¡ch Ä‘Æ¡n ---------------------------- --%>
<c:choose>
    <c:when test="${empty result.items}">
        <div class="card" style="text-align:center;padding:36px 18px;">
            <c:choose>
                <c:when test="${empty currentStatus}">
                    <p style="font-size:17px;margin:0 0 6px;">Báº¡n chÆ°a cÃ³ Ä‘Æ¡n hÃ ng nÃ o.</p>
                    <p class="muted" style="margin:0 0 18px;">Äáº·t thá»­ má»™t cuá»‘n Ä‘á»ƒ xem tiáº¿n trÃ¬nh Ä‘Æ¡n hÃ ng.</p>
                    <a class="btn" href="${ctx}/products">Báº¯t Ä‘áº§u mua sáº¯m</a>
                </c:when>
                <c:otherwise>
                    <p style="font-size:17px;margin:0 0 6px;">
                        KhÃ´ng cÃ³ Ä‘Æ¡n nÃ o á»Ÿ tráº¡ng thÃ¡i &ldquo;${currentStatus.label}&rdquo;.
                    </p>
                    <a class="btn secondary" href="${ctx}/orders">Xem táº¥t cáº£ Ä‘Æ¡n</a>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <c:otherwise>
        <c:forEach var="o" items="${result.items}">
            <article class="card order-card">

                <div class="order-head">
                    <div>
                        <a class="order-code" href="${ctx}/orders/detail?id=${o.orderId}">
                            ÄÆ¡n #${o.orderId}
                        </a>
                        <span class="muted">&middot; Ä‘áº·t lÃºc ${o.orderDateText}</span>
                    </div>
                    <span class="badge st-${o.status.code}">${o.status.label}</span>
                </div>

                <ul class="order-lines">
                    <c:forEach var="d" items="${o.details}">
                        <li>
                            <c:if test="${not empty d.coverImage}">
                                <c:url var="coverUrl" value="/image">
                                    <c:param name="name" value="${d.coverImage}"/>
                                </c:url>
                                <img class="book-cover small" src="${coverUrl}"
                                     loading="lazy" decoding="async" width="50" height="70"
                                     alt="BÃ¬a <c:out value='${d.title}'/>">
                            </c:if>
                            <span class="order-line-title"><c:out value="${d.title}"/></span>
                            <span class="muted">&times; ${d.quantity}</span>
                            <span class="order-line-money">${d.subtotalText}</span>
                        </li>
                    </c:forEach>
                </ul>

                <div class="order-foot">
                    <div class="muted">${o.paymentMethodLabel}</div>
                    <div>
                        Tá»•ng tiá»n:
                        <strong>${o.totalAmountText}</strong>
                        <a class="btn small secondary" href="${ctx}/orders/detail?id=${o.orderId}">Xem chi tiáº¿t</a>
                    </div>
                </div>
            </article>
        </c:forEach>

        <%-- Giá»¯ nguyÃªn bá»™ lá»c khi chuyá»ƒn trang --%>
        <c:set var="filterQuery" value="${empty currentStatus ? '' : '&amp;status='.concat(currentStatus.code)}"/>
        <nav class="pagination" aria-label="PhÃ¢n trang Ä‘Æ¡n hÃ ng">
            <c:choose>
                <c:when test="${result.hasPrevious}">
                    <a href="${ctx}/orders?page=${result.previousPage}${filterQuery}">Trang trÆ°á»›c</a>
                </c:when>
                <c:otherwise><span class="disabled">Trang trÆ°á»›c</span></c:otherwise>
            </c:choose>

            <c:forEach var="i" begin="1" end="${result.totalPages}">
                <c:choose>
                    <c:when test="${i eq result.currentPage}">
                        <span class="current" aria-current="page">${i}</span>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/orders?page=${i}${filterQuery}">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:choose>
                <c:when test="${result.hasNext}">
                    <a href="${ctx}/orders?page=${result.nextPage}${filterQuery}">Trang sau</a>
                </c:when>
                <c:otherwise><span class="disabled">Trang sau</span></c:otherwise>
            </c:choose>
        </nav>
    </c:otherwise>
</c:choose>

</body>
</html>
