<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
    CÂU 1 - DECORATOR cho VAI TRÒ USER.

    Mỗi trang nội dung trong /WEB-INF/views chỉ viết phần thân của nó; SiteMesh
    Filter tự bọc file này ra ngoài. Thẻ <sitemesh:write> không phải taglib mà
    là thẻ đặc biệt được SiteMesh thay thế sau khi JSP nội dung render xong.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property='title'/> &ndash; BookStore 24133059</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css?v=5">
    <script src="${pageContext.request.contextPath}/assets/app.js" defer></script>
    <sitemesh:write property='head'/>
</head>
<body>

<%@ include file="_header.jsp" %>

<main>
    <div class="container">
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
