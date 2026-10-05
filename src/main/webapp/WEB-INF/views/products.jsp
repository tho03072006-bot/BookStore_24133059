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

<form method="get" action="${ctx}/products" class="card catalog-filter" role="search" aria-label="Tìm và lọc sách">
    <div class="form-row search-query">
        <label for="q">Tên sách, tác giả hoặc ISBN</label>
        <input type="search" id="q" name="q" maxlength="100" value="<c:out value='${filter.query}'/>"
               placeholder="Ví dụ: Mắt Biếc, Nguyễn Nhật Ánh...">
    </div>
    <div class="form-row">
        <label for="author">Tác giả</label>
        <select id="author" name="author">
            <option value="">Tất cả tác giả</option>
            <c:forEach var="a" items="${authors}">
                <option value="${a.authorId}" ${filter.authorId eq a.authorId ? 'selected' : ''}><c:out value="${a.authorName}"/></option>
            </c:forEach>
        </select>
    </div>
    <div class="form-row">
        <label for="sort">Sắp xếp</label>
        <select id="sort" name="sort">
            <option value="latest" ${filter.sort eq 'latest' ? 'selected' : ''}>Mới thêm</option>
            <option value="price_asc" ${filter.sort eq 'price_asc' ? 'selected' : ''}>Giá tăng dần</option>
            <option value="price_desc" ${filter.sort eq 'price_desc' ? 'selected' : ''}>Giá giảm dần</option>
            <option value="title" ${filter.sort eq 'title' ? 'selected' : ''}>Tên sách A–Z</option>
        </select>
    </div>
    <div class="actions search-actions">
        <label class="inline-check" for="stock"><input type="checkbox" id="stock" name="stock" value="1" ${filter.inStock ? 'checked' : ''}> Chỉ sách còn hàng</label>
        <button class="btn" type="submit">Tìm sách</button>
        <a class="btn secondary" href="${ctx}/products">Xóa bộ lọc</a>
    </div>
</form>
<c:if test="${empty result.items}">
    <div class="card empty-state" role="status"><h2>Không tìm thấy sách phù hợp</h2>
        <p>Thử tên sách ngắn hơn hoặc chọn tác giả khác.</p><a class="btn secondary" href="${ctx}/products">Xem tất cả sách</a>
    </div>
</c:if>

<c:url var="returnToProducts" value="/products">
    <c:param name="q" value="${filter.query}"/><c:param name="author" value="${filter.authorId}"/>
    <c:param name="sort" value="${filter.sort}"/><c:param name="stock" value="${filter.inStock ? '1' : ''}"/>
    <c:param name="page" value="${result.currentPage}"/>
</c:url>

<div class="book-grid wide">
    <c:forEach var="b" items="${result.items}" varStatus="position">
        <article class="book-card">
            <a href="${ctx}/book?id=${b.bookId}">
                <c:choose>
                    <c:when test="${not empty b.coverImage}">
                        <c:url var="coverUrl" value="/image">
                            <c:param name="name" value="${b.coverImage}"/>
                        </c:url>
                        <img class="book-cover" src="${coverUrl}"
                             loading="${position.index lt 3 ? 'eager' : 'lazy'}" decoding="async" width="400" height="600"
                             alt="Bìa sách <c:out value='${b.title}'/>">
                    </c:when>
                    <c:otherwise>
                        <div class="book-cover"></div>
                    </c:otherwise>
                </c:choose>
            </a>

            <h2 class="title">
                <a href="${ctx}/book?id=${b.bookId}"><c:out value="${b.title}"/></a>
            </h2>

            <div class="price-tag">${b.priceText}</div>

            <div class="field-list">
                <div><span class="label">Mã isbn:</span> <c:out value="${b.isbn}"/></div>
                <div><span class="label">Tác giả:</span> <c:out value="${b.authorNames}"/></div>
                <div><span class="label">Nhà xuất bản:</span> <c:out value="${b.publisher}"/></div>
                <div><span class="label">Ngày xuất bản:</span> ${b.publishDateText}</div>
                <div><span class="label">Còn trong kho:</span> <c:out value="${b.quantity}"/></div>
            </div>

            <a class="review-count" href="${ctx}/book?id=${b.bookId}#reviews">
                Đánh giá (${b.reviewCount})
            </a>

            <%-- GIỎ HÀNG: thêm nhanh 1 cuốn, xong quay lại đúng trang đang xem --%>
            <c:choose>
                <c:when test="${b.quantity gt 0 and not empty b.price and b.price ge 0}">
                    <form method="post" action="${ctx}/cart/add" class="add-to-cart">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="bookId" value="${b.bookId}">
                        <input type="hidden" name="quantity" value="1">
                        <input type="hidden" name="returnUrl" value="<c:out value='${returnToProducts.substring(ctx.length())}'/>">
                        <button type="submit" class="btn small">Thêm vào giỏ</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <span class="btn small secondary" aria-disabled="true">${empty b.price or b.price lt 0 ? 'Chưa niêm giá' : 'Hết hàng'}</span>
                </c:otherwise>
            </c:choose>
        </article>
    </c:forEach>
</div>

<nav class="pagination" aria-label="Phân trang danh sách sản phẩm">
    <c:choose>
        <c:when test="${result.hasPrevious}">
            <c:url var="previousUrl" value="/products"><c:param name="page" value="${result.previousPage}"/><c:param name="q" value="${filter.query}"/><c:param name="author" value="${filter.authorId}"/><c:param name="sort" value="${filter.sort}"/><c:param name="stock" value="${filter.inStock ? '1' : ''}"/></c:url>
            <a href="${previousUrl}">Trang trước</a>
        </c:when>
        <c:otherwise><span class="disabled">Trang trước</span></c:otherwise>
    </c:choose>

    <c:set var="lastPrinted" value="0"/>
    <c:forEach var="i" begin="1" end="${result.totalPages}">
        <c:if test="${result.totalPages le 7 or i le 2 or i ge result.totalPages - 1 or (i ge result.currentPage - 1 and i le result.currentPage + 1)}">
            <c:if test="${i gt lastPrinted + 1}"><span class="disabled" aria-hidden="true">&hellip;</span></c:if>
            <c:choose>
                <c:when test="${i eq result.currentPage}"><span class="current" aria-current="page">${i}</span></c:when>
                <c:otherwise>
                    <c:url var="pageUrl" value="/products"><c:param name="page" value="${i}"/><c:param name="q" value="${filter.query}"/><c:param name="author" value="${filter.authorId}"/><c:param name="sort" value="${filter.sort}"/><c:param name="stock" value="${filter.inStock ? '1' : ''}"/></c:url>
                    <a href="${pageUrl}" aria-label="Trang ${i}">${i}</a>
                </c:otherwise>
            </c:choose>
            <c:set var="lastPrinted" value="${i}"/>
        </c:if>
    </c:forEach>

    <c:choose>
        <c:when test="${result.hasNext}">
            <c:url var="nextUrl" value="/products"><c:param name="page" value="${result.nextPage}"/><c:param name="q" value="${filter.query}"/><c:param name="author" value="${filter.authorId}"/><c:param name="sort" value="${filter.sort}"/><c:param name="stock" value="${filter.inStock ? '1' : ''}"/></c:url>
            <a href="${nextUrl}">Trang sau</a>
        </c:when>
        <c:otherwise><span class="disabled">Trang sau</span></c:otherwise>
    </c:choose>
</nav>

</body>
</html>
