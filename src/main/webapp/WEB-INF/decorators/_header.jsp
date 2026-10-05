<%--
    CÂU 1 - Phần HEADER dùng chung cho cả 02 decorator (user.jsp và admin.jsp).
    Menu theo đúng yêu cầu của đề: Trang Chủ, Sản phẩm, Đăng nhập,
    Trang quản trị (chỉ admin mới thấy mục này).

    Bài tập tiếp theo bổ sung thêm Giỏ hàng và Đơn hàng của tôi; bốn mục
    của đề thi giữ nguyên.

    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="uri" value="${empty requestScope.originalPath ? '/' : requestScope.originalPath}"/>
<c:set var="me" value="${sessionScope.currentUser}"/>
<c:set var="soCuonTrongGio" value="${empty sessionScope.cart ? 0 : sessionScope.cart.totalQuantity}"/>

<header class="site-header">
    <div class="container">
        <a class="brand" href="${ctx}/home">BookStore &middot; Đề 02</a>

        <ul class="main-menu">
            <li><a href="${ctx}/home" class="${uri eq '/home' or uri eq '/' ? 'active' : ''}">Trang Chủ</a></li>
            <li><a href="${ctx}/products" class="${uri eq '/products' ? 'active' : ''}">Sản phẩm</a></li>

            <li>
                <a href="${ctx}/cart" class="${fn:startsWith(uri, '/cart') ? 'active' : ''}">
                    Giỏ hàng
                    <c:if test="${soCuonTrongGio gt 0}">
                        <span class="cart-count" aria-label="${soCuonTrongGio} cuốn trong giỏ">${soCuonTrongGio}</span>
                    </c:if>
                </a>
            </li>

            <c:if test="${not empty me}">
                <li>
                    <a href="${ctx}/orders" class="${fn:startsWith(uri, '/orders') ? 'active' : ''}">Đơn hàng của tôi</a>
                </li>
            </c:if>

            <c:choose>
                <c:when test="${empty me}">
                    <li><a href="${ctx}/login" class="${uri eq '/login' ? 'active' : ''}">Đăng nhập</a></li>
                    <li><a href="${ctx}/register" class="${uri eq '/register' ? 'active' : ''}">Đăng ký</a></li>
                </c:when>
                <c:otherwise>
                    <%-- Chỉ tài khoản có is_admin = 1 mới nhìn thấy Trang quản trị --%>
                    <c:if test="${me.admin}">
                        <li class="badge-admin">
                            <a href="${ctx}/admin/books"
                               class="${fn:startsWith(uri, '/admin') ? 'active' : ''}">Trang quản trị</a>
                        </li>
                    </c:if>
                    <li class="user-chip">Xin chào, <b><c:out value="${me.displayName}"/></b></li>
                    <li><a href="${ctx}/logout" class="btn-logout">Đăng xuất</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</header>
