<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    LỊCH SỬ ĐẶT HÀNG - danh sách đơn của người dùng, LỌC THEO TRẠNG THÁI.
    Mỗi tab là một trạng thái trong 08 trạng thái đề bài yêu cầu.
    Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Đơn hàng của tôi</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Đơn hàng của tôi</h1>
    <a class="btn secondary" href="${ctx}/products">Mua thêm sách</a>
</div>

<%-- -------------------------- Bộ lọc trạng thái -------------------------- --%>
<nav class="status-filter" aria-label="Lọc đơn hàng theo trạng thái">
    <a href="${ctx}/orders" class="chip ${empty currentStatus ? 'active' : ''}">
        Tất cả <span class="chip-count">${totalAll}</span>
    </a>
    <c:forEach var="st" items="${statuses}">
        <a href="${ctx}/orders?status=${st.code}"
           class="chip st-${st.code} ${currentStatus eq st ? 'active' : ''}">
            ${st.label}
            <span class="chip-count">${empty counts[st.code] ? 0 : counts[st.code]}</span>
        </a>
    </c:forEach>
</nav>

<%-- ---------------------------- Danh sách đơn ---------------------------- --%>
<c:choose>
    <c:when test="${empty result.items}">
        <div class="card" style="text-align:center;padding:36px 18px;">
            <c:choose>
                <c:when test="${empty currentStatus}">
                    <p style="font-size:17px;margin:0 0 6px;">Bạn chưa có đơn hàng nào.</p>
                    <p class="muted" style="margin:0 0 18px;">Đặt thử một cuốn để xem tiến trình đơn hàng.</p>
                    <a class="btn" href="${ctx}/products">Bắt đầu mua sắm</a>
                </c:when>
                <c:otherwise>
                    <p style="font-size:17px;margin:0 0 6px;">
                        Không có đơn nào ở trạng thái &ldquo;${currentStatus.label}&rdquo;.
                    </p>
                    <a class="btn secondary" href="${ctx}/orders">Xem tất cả đơn</a>
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
                            Đơn #${o.orderId}
                        </a>
                        <span class="muted">&middot; đặt lúc ${o.orderDateText}</span>
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
                                     alt="Bìa <c:out value='${d.title}'/>">
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
                        Tổng tiền:
                        <strong>${o.totalAmountText}</strong>
                        <a class="btn small secondary" href="${ctx}/orders/detail?id=${o.orderId}">Xem chi tiết</a>
                    </div>
                </div>
            </article>
        </c:forEach>

        <%-- Giữ nguyên bộ lọc khi chuyển trang --%>
        <c:set var="filterQuery" value="${empty currentStatus ? '' : '&amp;status='.concat(currentStatus.code)}"/>
        <nav class="pagination" aria-label="Phân trang đơn hàng">
            <c:choose>
                <c:when test="${result.hasPrevious}">
                    <a href="${ctx}/orders?page=${result.previousPage}${filterQuery}">Trang trước</a>
                </c:when>
                <c:otherwise><span class="disabled">Trang trước</span></c:otherwise>
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
