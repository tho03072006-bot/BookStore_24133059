<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    CÂU 6 - Bảng quản lý Books: XEM danh sách CÓ PHÂN TRANG, kèm nút
    Tạo / Xem / Cập nhật / Xóa.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Quản lý sách</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Quản lý sách (CRUD)</h1>
    <a class="btn" href="${ctx}/admin/books/add">+ Thêm sách mới</a>
</div>

<div class="card" style="padding:0;overflow:auto;">
    <table class="data">
        <thead>
        <tr>
            <th>Bìa</th>
            <th>Mã</th>
            <th>Tiêu đề</th>
            <th>Mã isbn</th>
            <th>Tác giả</th>
            <th>Publisher</th>
            <th>Publisher_date</th>
            <th>Giá</th>
            <th>Quantity</th>
            <th>Review</th>
            <th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty result.items}">
            <tr>
                <td colspan="11" class="muted" style="text-align:center;padding:24px;">
                    Chưa có sách nào. Bấm "Thêm sách mới" để tạo.
                </td>
            </tr>
        </c:if>

        <c:forEach var="b" items="${result.items}">
            <tr>
                <td>
                    <c:if test="${not empty b.coverImage}">
                        <c:url var="coverUrl" value="/image">
                            <c:param name="name" value="${b.coverImage}"/>
                        </c:url>
                        <img class="book-cover small" src="${coverUrl}"
                             loading="lazy" decoding="async" width="50" height="70"
                             alt="Bìa <c:out value='${b.title}'/>">
                    </c:if>
                </td>
                <td>${b.bookId}</td>
                <td><c:out value="${b.title}"/></td>
                <td><c:out value="${b.isbn}"/></td>
                <td><c:out value="${b.authorNames}"/></td>
                <td><c:out value="${b.publisher}"/></td>
                <td>${b.publishDateText}</td>
                <td>${b.priceText}</td>
                <td><c:out value="${b.quantity}"/></td>
                <td>${b.reviewCount}</td>
                <td>
                    <div class="actions">
                        <a class="btn small secondary"
                           href="${ctx}/admin/books/view?id=${b.bookId}">Xem</a>
                        <a class="btn small secondary"
                           href="${ctx}/admin/books/edit?id=${b.bookId}">Sửa</a>

                        <%-- Xóa gửi bằng POST + hỏi xác nhận để tránh xóa nhầm --%>
                        <form method="post" action="${ctx}/admin/books/delete"
                              style="display:inline" data-confirm-delete>
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="id" value="${b.bookId}">
                            <input type="hidden" name="page" value="${result.currentPage}">
                            <button type="submit" class="btn small danger">Xóa</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <nav class="pagination" aria-label="Phân trang bảng sách">
        <c:choose>
            <c:when test="${result.hasPrevious}">
                <a href="${ctx}/admin/books?page=${result.previousPage}">Trang trước</a>
            </c:when>
            <c:otherwise><span class="disabled">Trang trước</span></c:otherwise>
        </c:choose>

        <c:set var="lastPrinted" value="0"/>
        <c:forEach var="i" begin="1" end="${result.totalPages}">
            <c:if test="${result.totalPages le 7 or i le 2 or i ge result.totalPages - 1 or (i ge result.currentPage - 1 and i le result.currentPage + 1)}">
                <c:if test="${i gt lastPrinted + 1}"><span class="disabled" aria-hidden="true">&hellip;</span></c:if>
                <c:choose>
                    <c:when test="${i eq result.currentPage}"><span class="current" aria-current="page">${i}</span></c:when>
                    <c:otherwise><a href="${ctx}/admin/books?page=${i}">${i}</a></c:otherwise>
                </c:choose>
                <c:set var="lastPrinted" value="${i}"/>
            </c:if>
        </c:forEach>

        <c:choose>
            <c:when test="${result.hasNext}">
                <a href="${ctx}/admin/books?page=${result.nextPage}">Trang sau</a>
            </c:when>
            <c:otherwise><span class="disabled">Trang sau</span></c:otherwise>
        </c:choose>
    </nav>
</div>

<p class="muted">Tổng cộng ${result.totalItems} cuốn &middot;
    ${result.pageSize} dòng / trang.</p>

</body>
</html>
