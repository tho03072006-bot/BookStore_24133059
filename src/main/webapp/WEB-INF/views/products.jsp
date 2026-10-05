<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    CÂU 1 - mục "Sản phẩm" trên menu Header: toàn bộ sách của cửa hàng,
    có phân trang.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Sản phẩm</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Sản phẩm</h1>
    <span class="muted">Tổng cộng ${result.totalItems} cuốn &middot;
        trang ${result.currentPage}/${result.totalPages}</span>
</div>

<c:if test="${empty result.items}">
    <div class="alert info">Chưa có sách nào trong database.</div>
</c:if>

<div class="book-grid wide">
    <c:forEach var="b" items="${result.items}">
        <article class="book-card">
            <a href="${ctx}/book?id=${b.bookId}">
                <c:choose>
                    <c:when test="${not empty b.coverImage}">
                        <c:url var="coverUrl" value="/image">
                            <c:param name="name" value="${b.coverImage}"/>
                        </c:url>
                        <img class="book-cover" src="${coverUrl}"
                             loading="lazy" decoding="async" width="400" height="600"
                             alt="Bìa sách <c:out value='${b.title}'/>">
                    </c:when>
                    <c:otherwise>
                        <div class="book-cover"></div>
                    </c:otherwise>
                </c:choose>
            </a>

            <h3 class="title">
                <a href="${ctx}/book?id=${b.bookId}"><c:out value="${b.title}"/></a>
            </h3>

            <div class="field-list">
                <div><span class="label">Mã isbn:</span> <c:out value="${b.isbn}"/></div>
                <div><span class="label">Tác giả:</span> <c:out value="${b.authorNames}"/></div>
                <div><span class="label">Publisher:</span> <c:out value="${b.publisher}"/></div>
                <div><span class="label">Publisher_date:</span> ${b.publishDateText}</div>
                <div><span class="label">Quantity:</span> <c:out value="${b.quantity}"/></div>
            </div>

            <a class="review-count" href="${ctx}/book?id=${b.bookId}#reviews">
                Review (${b.reviewCount})
            </a>

            <%-- GIỎ HÀNG: thêm nhanh 1 cuốn, xong quay lại đúng trang đang xem --%>
            <c:choose>
                <c:when test="${b.quantity gt 0}">
                    <form method="post" action="${ctx}/cart/add" class="add-to-cart">
                        <input type="hidden" name="bookId" value="${b.bookId}">
                        <input type="hidden" name="quantity" value="1">
                        <input type="hidden" name="returnUrl" value="/products?page=${result.currentPage}">
                        <button type="submit" class="btn small">Thêm vào giỏ</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <span class="btn small secondary" aria-disabled="true">Hết hàng</span>
                </c:otherwise>
            </c:choose>
        </article>
    </c:forEach>
</div>

<nav class="pagination" aria-label="Phân trang danh sách sản phẩm">
    <c:choose>
        <c:when test="${result.hasPrevious}">
            <a href="${ctx}/products?page=${result.previousPage}">Trang trước</a>
        </c:when>
        <c:otherwise><span class="disabled">Trang trước</span></c:otherwise>
    </c:choose>

    <c:set var="lastPrinted" value="0"/>
    <c:forEach var="i" begin="1" end="${result.totalPages}">
        <c:if test="${result.totalPages le 7 or i le 2 or i ge result.totalPages - 1 or (i ge result.currentPage - 1 and i le result.currentPage + 1)}">
            <c:if test="${i gt lastPrinted + 1}"><span class="disabled" aria-hidden="true">&hellip;</span></c:if>
            <c:choose>
                <c:when test="${i eq result.currentPage}"><span class="current" aria-current="page">${i}</span></c:when>
                <c:otherwise><a href="${ctx}/products?page=${i}">${i}</a></c:otherwise>
            </c:choose>
            <c:set var="lastPrinted" value="${i}"/>
        </c:if>
    </c:forEach>

    <c:choose>
        <c:when test="${result.hasNext}">
            <a href="${ctx}/products?page=${result.nextPage}">Trang sau</a>
        </c:when>
        <c:otherwise><span class="disabled">Trang sau</span></c:otherwise>
    </c:choose>
</nav>

</body>
</html>
