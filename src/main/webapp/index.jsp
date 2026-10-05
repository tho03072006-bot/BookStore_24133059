<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
    Trang mặc định của ứng dụng: chuyển thẳng sang trang chủ /home (Câu 3).
    Đề số 02 - Trần Minh Thọ - 24133059
--%>
<% response.sendRedirect(request.getContextPath() + "/home"); %>
