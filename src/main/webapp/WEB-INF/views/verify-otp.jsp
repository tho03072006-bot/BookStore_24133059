<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    CÂU 2 - Trang NHẬP MÃ OTP kích hoạt tài khoản.
    Nhập đúng mã thì tài khoản mới được ghi vào bảng users.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Kích hoạt tài khoản</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div style="max-width:460px;margin:0 auto;">
    <h1>Kích hoạt tài khoản</h1>

    <c:if test="${not empty error}">
        <div class="alert error" role="alert" tabindex="-1" data-error-summary><c:out value="${error}"/></div>
    </c:if>

    <div class="alert info">
        Mã OTP gồm 6 chữ số để kích hoạt tài khoản
        <strong><c:out value="${pending.email}"/></strong>
        và có hiệu lực trong ${otpExpiryMinutes} phút.
        <c:if test="${not pending.emailSent}">
            <br>Chế độ demo: xem mã trong Console của máy chủ.
        </c:if>
    </div>

    <div class="card">
        <form method="post" action="${ctx}/verify-otp">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
            <div class="form-row">
                <label for="otp">Mã OTP</label>
                <input type="text" id="otp" name="otp" required autofocus
                       autocomplete="one-time-code" inputmode="numeric" pattern="[0-9]{6}" maxlength="6"
                       placeholder="123456"
                       style="letter-spacing:6px;font-size:20px;text-align:center;">
            </div>

            <div class="actions">
                <button type="submit" class="btn">Xác nhận</button>

                <a class="btn secondary" href="${ctx}/register">Nhập lại thông tin</a>
            </div>
        </form>
        <form method="post" action="${ctx}/resend-otp">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}"><button class="btn secondary" type="submit">Gửi lại mã</button><p class="hint">Có thể gửi lại sau 60 giây.</p></form>
    </div>
</div>

</body>
</html>
