<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
    CÃ‚U 1 - DECORATOR cho VAI TRÃ’ USER.

    Má»—i trang ná»™i dung trong /WEB-INF/views chá»‰ viáº¿t pháº§n thÃ¢n cá»§a nÃ³; SiteMesh
    Filter tá»± bá»c file nÃ y ra ngoÃ i. Tháº» <sitemesh:write> khÃ´ng pháº£i taglib mÃ 
    lÃ  tháº» Ä‘áº·c biá»‡t Ä‘Æ°á»£c SiteMesh thay tháº¿ sau khi JSP ná»™i dung render xong.

    Äá» sá»‘ 02 - Tráº§n Minh Thá» - 24133059
--%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property='title'/> &ndash; BookStore 24133059</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css?v=6">
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
