<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%--
    CÂU 4 - Trang chi tiết 01 cuốn sách, mở ra khi bấm vào tiêu đề sách ở
    trang home. Bố cục đúng mẫu của đề:

        [cover_image] | Tiêu đề / Mã isbn / Tác giả / Publisher /
                        Publisher_date / Quantity / Reviews (10)
        Reviews
        [users]: [review_text]
        Form thêm reviews   [Submit]

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title><c:out value="${book.title}"/></title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="me" value="${sessionScope.currentUser}"/>

<div class="page-title">
    <h1>Chi tiết sách</h1>
    <a class="btn secondary small" href="${ctx}/home">&laquo; Về trang chủ</a>
</div>

<div class="card">
    <div class="detail-grid">

        <%-- [cover_image] --%>
        <div>
            <c:choose>
                <c:when test="${not empty book.coverImage}">
                    <c:url var="coverUrl" value="/image">
                        <c:param name="name" value="${book.coverImage}"/>
                    </c:url>
                    <img class="book-cover" src="${coverUrl}"
                         decoding="async" width="400" height="600"
                         alt="Bìa sách <c:out value='${book.title}'/>">
                </c:when>
                <c:otherwise>
                    <div class="book-cover"></div>
                </c:otherwise>
            </c:choose>
        </div>

        <div>
            <div class="field-list" style="font-size:15px;">
                <div><span class="label">Tiêu đề:</span>
                    <strong><c:out value="${book.title}"/></strong></div>
                <div><span class="label">Mã isbn:</span> <c:out value="${book.isbn}"/></div>
                <div><span class="label">Tác giả:</span> <c:out value="${book.authorNames}"/></div>
                <div><span class="label">Nhà xuất bản:</span> <c:out value="${book.publisher}"/></div>
                <div><span class="label">Ngày xuất bản:</span> ${book.publishDateText}</div>
                <div><span class="label">Còn trong kho:</span> <c:out value="${book.quantity}"/></div>
                <c:if test="${not empty book.price}">
                    <div><span class="label">Giá:</span> <strong class="price">${book.priceText}</strong></div>
                </c:if>
                <div>
                    <span class="review-count">Reviews (${book.reviewCount})</span>
                    <c:if test="${not empty book.averageRating}">
                        <span class="stars" role="img" aria-label="Điểm trung bình ${book.averageRating} trên 5">
                            <c:forEach var="star" begin="1" end="5">
                                <c:choose><c:when test="${star le book.averageRating}">&#9733;</c:when><c:otherwise>&#9734;</c:otherwise></c:choose>
                            </c:forEach>
                        </span>
                        <span class="muted"> &mdash; điểm trung bình
                            <fmt:formatNumber value="${book.averageRating}"
                                              maxFractionDigits="1"/>/5</span>
                    </c:if>
                </div>
            </div>

            <c:if test="${not empty book.description}">
                <p style="margin-top:14px;"><c:out value="${book.description}"/></p>
            </c:if>

            <%-- GIỎ HÀNG: chọn số lượng rồi thêm vào giỏ. Ô nhập chặn sẵn
                 trong khoảng 1..tồn kho, tầng Service kiểm tra lại lần nữa. --%>
            <c:choose>
                <c:when test="${book.quantity gt 0 and not empty book.price and book.price ge 0}">
                    <form method="post" action="${ctx}/cart/add" class="buy-box">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="bookId" value="${book.bookId}">
                        <input type="hidden" name="returnUrl" value="/book?id=${book.bookId}">

                        <label for="quantity">Số lượng</label>
                        <input type="number" id="quantity" name="quantity" value="1"
                               min="1" max="${book.quantity lt 20 ? book.quantity : 20}" required>

                        <button type="submit" class="btn">Thêm vào giỏ hàng</button>
                        <a class="btn secondary" href="${ctx}/cart">Xem giỏ hàng</a>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="alert info" style="margin-top:16px;">
                        ${empty book.price or book.price lt 0 ? 'Cuốn sách này chưa niêm giá, chưa thể đặt hàng.' : 'Cuốn sách này hiện đã hết hàng.'}
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%-- ------------------------------ Reviews ------------------------------ --%>
<div class="card" id="reviews">
    <h2>Reviews</h2>

    <c:choose>
        <c:when test="${empty reviews}">
            <p class="muted">Chưa có nhận xét nào cho cuốn sách này.</p>
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

<%-- --------------------------- Form thêm review ------------------------ --%>
<div class="card">
    <h2>Form thêm reviews</h2>

    <c:choose>
        <c:when test="${empty me}">
            <div class="alert info">
                Bạn cần <a href="${ctx}/login?back=${book.bookId}">đăng nhập</a>
                để gửi nhận xét cho cuốn sách này.
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty myReview}">
                <div class="alert info">
                    Bạn đã đánh giá cuốn sách này. Gửi lại để cập nhật đánh giá của bạn.
                </div>
            </c:if>

            <form method="post" action="${ctx}/review">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                <input type="hidden" name="bookId" value="${book.bookId}">

                <div class="form-row" style="max-width:260px;">
                    <label for="rating">Đánh giá</label>
                    <select id="rating" name="rating">
                        <c:forEach var="star" begin="1" end="5">
                            <option value="${star}"
                                <c:if test="${(empty myReview and star eq 5) or (not empty myReview and myReview.rating eq star)}">selected</c:if>>
                                ${star} sao
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-row">
                    <label for="reviewText">Nội dung nhận xét</label>
                    <textarea id="reviewText" name="reviewText"
                              placeholder="Cảm nhận của bạn về cuốn sách này..."
                              required><c:out value="${myReview.reviewText}"/></textarea>
                </div>

                <button type="submit" class="btn">Gửi đánh giá</button>
            </form>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>
