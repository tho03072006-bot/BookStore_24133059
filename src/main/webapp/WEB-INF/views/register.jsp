<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    CÂU 2 - Trang ĐĂNG KÝ. Bấm "Đăng ký" chưa tạo tài khoản ngay mà gửi mã OTP
    tới email vừa nhập; tài khoản chỉ được tạo sau khi nhập đúng mã.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<html>
<head>
    <title>Đăng ký</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div style="max-width:560px;margin:0 auto;">
    <h1>Đăng ký tài khoản</h1>

    <c:if test="${not empty error}">
        <div class="alert error" role="alert" tabindex="-1" data-error-summary><c:out value="${error}"/></div>
    </c:if>

    <c:if test="${not mailConfigured}">
        <div class="alert info">
            Chế độ demo: mã kích hoạt được xem trong Console của máy chủ.
        </div>
    </c:if>

    <div class="card">
        <form method="post" action="${ctx}/register">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">

            <div class="form-row">
                <label for="email">Email <span class="muted">(dùng để nhận mã OTP)</span></label>
                <input type="email" id="email" autocomplete="email" name="email" required autofocus maxlength="50"
                       aria-invalid="${errorField eq 'email'}" aria-describedby="email-error"
                       value="<c:out value='${email}'/>" placeholder="ban@example.com">
                <div class="field-error" id="email-error"><c:if test="${errorField eq 'email'}"><c:out value="${error}"/></c:if></div>
            </div>

            <div class="form-row">
                <label for="fullname">Họ và tên</label>
                <input type="text" id="fullname" autocomplete="name" name="fullname" required maxlength="50"
                       aria-invalid="${errorField eq 'fullname'}" aria-describedby="fullname-error"
                       value="<c:out value='${fullname}'/>" placeholder="Nguyễn Văn A">
                <div class="field-error" id="fullname-error"><c:if test="${errorField eq 'fullname'}"><c:out value="${error}"/></c:if></div>
            </div>

            <div class="form-row">
                <label for="phone">Số điện thoại</label>
                <input type="tel" inputmode="tel" autocomplete="tel" id="phone" name="phone" maxlength="20"
                       aria-invalid="${errorField eq 'phone'}" aria-describedby="phone-error"
                       value="<c:out value='${phone}'/>" placeholder="0912345678">
                <div class="field-error" id="phone-error"><c:if test="${errorField eq 'phone'}"><c:out value="${error}"/></c:if></div>
                <div class="hint">Không bắt buộc. Nhập 10 chữ số, bắt đầu bằng 0.</div>
            </div>

            <div class="form-grid">
                <div class="form-row">
                    <label for="password">Mật khẩu</label>
                    <input type="password" id="password" autocomplete="new-password" name="password" required minlength="6"
                           aria-invalid="${errorField eq 'password'}" aria-describedby="password-error">
                    <div class="field-error" id="password-error"><c:if test="${errorField eq 'password'}"><c:out value="${error}"/></c:if></div>
                </div>
                <div class="form-row">
                    <label for="confirmPassword">Nhập lại mật khẩu</label>
                    <input type="password" id="confirmPassword" autocomplete="new-password" name="confirmPassword"
                           aria-invalid="${errorField eq 'confirmPassword'}" aria-describedby="confirmPassword-error"
                           required minlength="6">
                    <div class="field-error" id="confirmPassword-error"><c:if test="${errorField eq 'confirmPassword'}"><c:out value="${error}"/></c:if></div>
                </div>
            </div>

            <div class="actions">
                <button type="submit" class="btn">Đăng ký &amp; gửi mã OTP</button>
                <a class="btn secondary" href="${ctx}/login">Đã có tài khoản? Đăng nhập</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>
