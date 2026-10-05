<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    CÂU 3 - Trang home: hiển thị TẤT CẢ sách, gom theo TỪNG TÁC GIẢ,
    mỗi tác giả phân trang 03 sách/trang, đúng mẫu bố cục trong đề:

        Tác giả : Author_name
        [cover_image]   [cover_image]   [cover_image]
        Tiêu đề / Mã isbn / Tác giả / Publisher / Publisher_date /
        Quantity / Review (n)
        Trang trước – 1 2 3 4 – Trang sau

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Trang chủ</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Tất cả sách theo từng tác giả</h1>
    <span class="muted">Mỗi tác giả hiển thị ${pageSize} cuốn / trang</span>
</div>

<c:if test="${empty sections}">
    <div class="alert info">Chưa có dữ liệu sách trong database.
        Hãy chạy file <code>database/01_schema.sql</code> rồi
        <code>database/02_seed.sql</code>.</div>
</c:if>

<c:forEach var="section" items="${sections}">
    <c:set var="author" value="${section.author}"/>
    <c:set var="page" value="${section.page}"/>

    <section class="author-block" id="author-${author.authorId}">

        <div class="author-head">
            <div>
                <h2>Tác giả : <c:out value="${author.authorName}"/></h2>
                <c:if test="${not empty author.dateOfBirthText}">
                    <span class="muted">(sinh ngày ${author.dateOfBirthText})</span>
                </c:if>
            </div>
            <span class="muted">${page.totalItems} cuốn &middot;
                trang ${page.currentPage}/${page.totalPages}</span>
        </div>

        <div class="book-grid">
            <c:forEach var="b" items="${page.items}">
                <article class="book-card">

                    <%-- [cover_image] --%>
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

                    <%-- Bấm vào TIÊU ĐỀ để sang trang chi tiết (Câu 4) --%>
                    <h3 class="title">
                        <a href="${ctx}/book?id=${b.bookId}"><c:out value="${b.title}"/></a>
                    </h3>

                    <div class="price-tag">${b.priceText}</div>

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

                    <%-- GIỎ HÀNG: thêm nhanh 1 cuốn, xong quay lại đúng khối
                         tác giả và đúng trang đang xem --%>
                    <c:choose>
                        <c:when test="${b.quantity gt 0}">
                            <form method="post" action="${ctx}/cart/add" class="add-to-cart">
                                <input type="hidden" name="bookId" value="${b.bookId}">
                                <input type="hidden" name="quantity" value="1">
                                <input type="hidden" name="returnUrl"
                                       value="/home?author=${author.authorId}&amp;page=${page.currentPage}#author-${author.authorId}">
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

        <%-- Thanh phân trang RIÊNG của tác giả này: đổi trang khối này không
             làm đổi trang của các tác giả khác. --%>
        <nav class="pagination" aria-label="Phân trang sách của <c:out value='${author.authorName}'/>">
            <c:choose>
                <c:when test="${page.hasPrevious}">
                    <a href="${ctx}/home?author=${author.authorId}&amp;page=${page.previousPage}#author-${author.authorId}">Trang trước</a>
                </c:when>
                <c:otherwise><span class="disabled">Trang trước</span></c:otherwise>
            </c:choose>

            <c:set var="lastPrinted" value="0"/>
            <c:forEach var="i" begin="1" end="${page.totalPages}">
                <c:if test="${page.totalPages le 7 or i le 2 or i ge page.totalPages - 1 or (i ge page.currentPage - 1 and i le page.currentPage + 1)}">
                    <c:if test="${i gt lastPrinted + 1}"><span class="disabled" aria-hidden="true">&hellip;</span></c:if>
                    <c:choose>
                        <c:when test="${i eq page.currentPage}"><span class="current" aria-current="page">${i}</span></c:when>
                        <c:otherwise><a href="${ctx}/home?author=${author.authorId}&amp;page=${i}#author-${author.authorId}">${i}</a></c:otherwise>
                    </c:choose>
                    <c:set var="lastPrinted" value="${i}"/>
                </c:if>
            </c:forEach>

            <c:choose>
                <c:when test="${page.hasNext}">
                    <a href="${ctx}/home?author=${author.authorId}&amp;page=${page.nextPage}#author-${author.authorId}">Trang sau</a>
                </c:when>
                <c:otherwise><span class="disabled">Trang sau</span></c:otherwise>
            </c:choose>
        </nav>

    </section>
</c:forEach>

</body>
</html>
