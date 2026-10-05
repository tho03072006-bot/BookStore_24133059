<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    CÂU 2 - Trang ĐĂNG NHẬP (dùng Session).
    Sai email/mật khẩu thì quay lại chính trang này kèm thông báo lỗi.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Đăng nhập</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div style="max-width:460px;margin:0 auto;">
    <h1>Đăng nhập</h1>

    <c:if test="${not empty error}">
        <div class="alert error" role="alert" tabindex="-1" data-error-summary><c:out value="${error}"/></div>
    </c:if>

    <div class="card">
        <%-- Giữ lại tham số back để sau khi đăng nhập quay về đúng cuốn sách --%>
        <form method="post" action="${ctx}/login">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
            <c:if test="${not empty param.back}">
                <input type="hidden" name="back" value="<c:out value='${param.back}'/>">
            </c:if>
            <%-- Giữ tham số next để đăng nhập xong quay về đúng chỗ đang dở
                 (thanh toán, đơn hàng của tôi, giỏ hàng). --%>
            <c:if test="${not empty param.next}">
                <input type="hidden" name="next" value="<c:out value='${param.next}'/>">
            </c:if>

            <div class="form-row">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" autocomplete="username" maxlength="50" required autofocus
                       value="<c:out value='${email}'/>" placeholder="ban@example.com">
            </div>

            <div class="form-row">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" autocomplete="current-password" required
                       placeholder="Nhập mật khẩu">
            </div>

            <div class="actions">
                <button type="submit" class="btn">Đăng nhập</button>
                <a class="btn secondary" href="${ctx}/register">Chưa có tài khoản? Đăng ký</a>
            </div>
        </form>
    </div>

    <div class="alert info">
        <b>Tài khoản mẫu có sẵn trong database</b> (mật khẩu đều là <code>123456</code>):
        <br>&bull; Admin: <code>admin@bookstore.local</code>
        <br>&bull; User:&nbsp; <code>user@bookstore.local</code>
    </div>
</div>

</body>
</html>
