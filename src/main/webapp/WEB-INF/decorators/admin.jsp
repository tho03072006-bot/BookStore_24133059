<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
    CÃ‚U 1 - DECORATOR cho VAI TRÃ’ ADMIN (Ã¡p dá»¥ng cho má»i URL /admin/*).

    KhÃ¡c decorator cá»§a User á»Ÿ chá»— cÃ³ thÃªm dáº£i menu con cá»§a khu vá»±c quáº£n trá»‹ vÃ 
    nhÃ£n nháº¯c ngÆ°á»i dÃ¹ng biáº¿t Ä‘ang Ä‘á»©ng trong Trang quáº£n trá»‹.

    Äá» sá»‘ 02 - Tráº§n Minh Thá» - 24133059
--%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property='title'/> &ndash; Quáº£n trá»‹ BookStore 24133059</title>
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
                <span class="muted">Trang quáº£n trá»‹ &raquo;</span>
                <a class="btn small ${uri eq '/admin/books' ? '' : 'secondary'}"
                   href="${ctx}/admin/books">Quáº£n lÃ½ SÃ¡ch</a>
                <a class="btn small ${uri eq '/admin/books/add' ? '' : 'secondary'}"
                   href="${ctx}/admin/books/add">ThÃªm sÃ¡ch má»›i</a>
                <a class="btn small secondary" href="${ctx}/home">Vá» trang ngÆ°á»i dÃ¹ng</a>
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
