<%-- File: /admin/header_admin.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0 shadow">
    <a class="navbar-brand col-md-3 col-lg-2 me-0 px-3" href="${pageContext.request.contextPath}/admin/dashboard">MusicShop Admin</a>
    <button class="navbar-toggler position-absolute d-md-none collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#sidebarMenu">
        <span class="navbar-toggler-icon"></span>
    </button>
    
    <%-- Có thể thêm ô tìm kiếm admin sau --%>
    
    <div class="navbar-nav">
        <div class="nav-item text-nowrap">
             <span class="px-3 text-white">Chào, ${sessionScope.account.full_name}</span>
            <a class="nav-link px-3 d-inline" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
        </div>
    </div>
</nav>