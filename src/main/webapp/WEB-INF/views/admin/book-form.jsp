<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    CÂU 6 - Form dùng chung cho TẠO MỚI và CẬP NHẬT sách.
    Cùng một file JSP, khác nhau ở chỗ bookId có giá trị hay không.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title><c:out value="${formTitle}"/></title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isEdit" value="${book.bookId gt 0}"/>

<div class="page-title">
    <h1><c:out value="${formTitle}"/></h1>
    <a class="btn secondary" href="${ctx}/admin/books">&laquo; Về danh sách</a>
</div>

<c:if test="${not empty error}">
    <div class="alert error"><c:out value="${error}"/></div>
</c:if>

<div class="card">
    <%-- enctype multipart/form-data để gửi kèm được file ảnh bìa --%>
    <form method="post" enctype="multipart/form-data"
          action="${ctx}/admin/books/${isEdit ? 'edit' : 'add'}">

        <input type="hidden" name="bookId" value="${book.bookId}">

        <div class="form-row">
            <label for="title">Tiêu đề <span class="muted">(bắt buộc)</span></label>
            <input type="text" id="title" name="title" required maxlength="200"
                   aria-invalid="${errorField eq 'title'}"
                   value="<c:out value='${book.title}'/>">
            <c:if test="${errorField eq 'title'}"><div class="field-error" role="alert"><c:out value="${error}"/></div></c:if>
        </div>

        <div class="form-grid">
            <div class="form-row">
                <label for="isbn">Mã isbn</label>
                <input type="number" id="isbn" name="isbn" min="0" max="2147483647"
                       value="<c:out value='${book.isbn}'/>">
                <div class="hint">Cột isbn kiểu int nên chỉ nhận chữ số.</div>
            </div>

            <div class="form-row">
                <label for="publisher">Publisher</label>
                <input type="text" id="publisher" name="publisher" maxlength="100"
                       aria-invalid="${errorField eq 'publisher'}"
                       value="<c:out value='${book.publisher}'/>">
                <c:if test="${errorField eq 'publisher'}"><div class="field-error" role="alert"><c:out value="${error}"/></div></c:if>
            </div>

            <div class="form-row">
                <label for="publishDate">Publisher_date</label>
                <input type="date" id="publishDate" name="publishDate"
                       value="${book.publishDateInput}">
            </div>

            <div class="form-row">
                <label for="quantity">Quantity</label>
                <input type="number" id="quantity" name="quantity" min="0"
                       aria-invalid="${errorField eq 'quantity'}"
                       value="<c:out value='${book.quantity}'/>">
                <c:if test="${errorField eq 'quantity'}"><div class="field-error" role="alert"><c:out value="${error}"/></div></c:if>
            </div>

            <div class="form-row">
                <label for="price">Giá</label>
                <input type="number" id="price" name="price" step="0.01" min="0" max="9999.99"
                       aria-invalid="${errorField eq 'price'}"
                       value="<c:out value='${book.price}'/>">
                <c:if test="${errorField eq 'price'}"><div class="field-error" role="alert"><c:out value="${error}"/></div></c:if>
                <div class="hint">Cột price kiểu decimal(6,2) nên tối đa 9999.99.</div>
            </div>
        </div>

        <div class="form-row">
            <label>Tác giả <span class="muted">(chọn được nhiều người)</span></label>
            <div class="checkbox-list">
                <c:forEach var="a" items="${allAuthors}">
                    <label>
                        <input type="checkbox" name="authorIds" value="${a.authorId}"
                            <c:forEach var="sel" items="${selectedAuthorIds}">
                                <c:if test="${sel eq a.authorId}">checked</c:if>
                            </c:forEach>>
                        <c:out value="${a.authorName}"/>
                    </label>
                </c:forEach>
            </div>
            <c:if test="${errorField eq 'authorIds'}"><div class="field-error" role="alert"><c:out value="${error}"/></div></c:if>
            <div class="hint">Chọn tác giả thì sách mới xuất hiện ở khối tương ứng
                trên trang chủ (Câu 3).</div>
        </div>

        <div class="form-grid">
            <div class="form-row">
                <label for="coverFile">Ảnh bìa &ndash; tải file lên</label>
                <input type="file" id="coverFile" name="coverFile"
                       accept=".jpg,.jpeg,.png,.gif,.webp">
                <div class="hint">Tối đa 5MB. Để trống nếu không muốn đổi ảnh.</div>
            </div>

            <div class="form-row">
                <label for="coverImage">hoặc nhập tên file / URL ảnh</label>
                <input type="text" id="coverImage" name="coverImage" maxlength="100"
                       aria-invalid="${errorField eq 'coverImage'}"
                       value="<c:out value='${book.coverImage}'/>"
                       placeholder="book_01.webp hoặc https://...">
                <c:if test="${errorField eq 'coverImage'}"><div class="field-error" role="alert"><c:out value="${error}"/></div></c:if>
                <div class="hint">Cột cover_image kiểu varchar(100).</div>
            </div>
        </div>

        <div class="form-row">
            <label for="description">Mô tả</label>
            <textarea id="description" name="description"><c:out value="${book.description}"/></textarea>
        </div>

        <div class="actions">
            <button type="submit" class="btn">
                <c:out value="${isEdit ? 'Cập nhật' : 'Tạo mới'}"/>
            </button>
            <a class="btn secondary" href="${ctx}/admin/books">Huỷ</a>
        </div>
    </form>
</div>

</body>
</html>
