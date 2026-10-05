<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    Trang báo lỗi dùng chung (khai báo trong web.xml cho mã 404 và 500).
    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Đã xảy ra lỗi</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="card" style="max-width:640px;margin:0 auto;">
    <h1>Rất tiếc, đã xảy ra lỗi</h1>

    <p>
        Mã lỗi: <strong>${requestScope['jakarta.servlet.error.status_code']}</strong>
    </p>
    <c:if test="${not empty requestScope['jakarta.servlet.error.message']}">
        <p class="muted"><c:out value="${requestScope['jakarta.servlet.error.message']}"/></p>
    </c:if>

    <div class="actions">
        <a class="btn" href="${ctx}/home">Về trang chủ</a>
        <a class="btn secondary" href="${ctx}/products">Xem sản phẩm</a>
    </div>
</div>

</body>
</html>
