<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
    CÂU 1 - DECORATOR cho VAI TRÒ ADMIN (áp dụng cho mọi URL /admin/*).

    Khác decorator của User ở chỗ có thêm dải menu con của khu vực quản trị và
    nhãn nhắc người dùng biết đang đứng trong Trang quản trị.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property='title'/> &ndash; Quản trị BookStore 24133059</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css?v=6">
    <script src="${pageContext.request.contextPath}/assets/app.js" defer></script>
    <sitemesh:write property='head'/>
</head>
<body>

<%@ include file="_header.jsp" %>

<main>
    <div class="container">

        <div class="card" style="padding:12px 18px;">
            <div class="actions">
                <span class="muted">Trang quản trị &raquo;</span>
                <a class="btn small ${uri eq '/admin/books' ? '' : 'secondary'}"
                   href="${ctx}/admin/books">Quản lý Sách</a>
                <a class="btn small ${uri eq '/admin/books/add' ? '' : 'secondary'}"
                   href="${ctx}/admin/books/add">Thêm sách mới</a>
                <a class="btn small secondary" href="${ctx}/home">Về trang người dùng</a>
            </div>
        </div>

        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert success" role="status"><c:out value="${sessionScope.flashSuccess}"/></div>
            <c:remove var="flashSuccess" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert error" role="alert"><c:out value="${sessionScope.flashError}"/></div>
            <c:remove var="flashError" scope="session"/>
        </c:if>

        <sitemesh:write property='body'/>
    </div>
</main>

<%@ include file="_footer.jsp" %>

</body>
</html>
