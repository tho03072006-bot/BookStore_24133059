<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    CÂU 6 - XEM: chi tiết một dòng của bảng Books ở chế độ chỉ đọc.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Xem sách #${book.bookId}</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Xem sách #${book.bookId}</h1>
    <div class="actions">
        <a class="btn" href="${ctx}/admin/books/edit?id=${book.bookId}">Sửa</a>
        <a class="btn secondary" href="${ctx}/admin/books">&laquo; Về danh sách</a>
    </div>
</div>

<div class="card">
    <div class="detail-grid">
        <div>
            <c:choose>
                <c:when test="${not empty book.coverImage}">
                    <c:url var="coverUrl" value="/image">
                        <c:param name="name" value="${book.coverImage}"/>
                    </c:url>
                    <img class="book-cover" src="${coverUrl}"
                         decoding="async" width="400" height="600"
                         alt="Bìa <c:out value='${book.title}'/>">
                </c:when>
                <c:otherwise><div class="book-cover"></div></c:otherwise>
            </c:choose>
        </div>

        <div>
            <table class="data">
                <tr><th>bookid</th><td>${book.bookId}</td></tr>
                <tr><th>title</th><td><c:out value="${book.title}"/></td></tr>
                <tr><th>isbn</th><td><c:out value="${book.isbn}"/></td></tr>
                <tr><th>publisher</th><td><c:out value="${book.publisher}"/></td></tr>
                <tr>
                    <th>price</th>
                    <td><c:out value="${book.price}"/>
                        <span class="muted">(nghìn đồng) = <strong>${book.priceText}</strong></span></td>
                </tr>
                <tr><th>publish_date</th><td>${book.publishDateText}</td></tr>
                <tr><th>quantity</th><td><c:out value="${book.quantity}"/></td></tr>
                <tr><th>cover_image</th><td><c:out value="${book.coverImage}"/></td></tr>
                <tr><th>Tác giả</th><td><c:out value="${book.authorNames}"/></td></tr>
                <tr><th>description</th><td><c:out value="${book.description}"/></td></tr>
            </table>
        </div>
    </div>
</div>

<div class="card">
    <h2>Reviews (${book.reviewCount})</h2>
    <c:choose>
        <c:when test="${empty reviews}">
            <p class="muted">Chưa có nhận xét nào.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="r" items="${reviews}">
                <div class="review-item">
                    <span class="review-user"><c:out value="${r.userDisplayName}"/></span>:
                    <c:out value="${r.reviewText}"/>
                    <c:if test="${not empty r.rating}">
                        <span class="stars">
                            <c:forEach begin="1" end="${r.rating}">&#9733;</c:forEach>
                        </span>
                    </c:if>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>
