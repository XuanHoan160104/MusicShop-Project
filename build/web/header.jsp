<%--
    File: header.jsp
    ĐÃ SỬA: Cập nhật link trên thanh menu màu cam (Giới thiệu, Liên hệ...)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Link CSS Font Awesome (Cần thiết để hiển thị icon) --%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

<div class="container">
    <header class="row align-items-center py-3 mb-4 border-bottom">

        <%-- Cột 1: Logo --%>
        <div class="col-md-2 text-center text-md-start mb-3 mb-md-0">
             <a href="${pageContext.request.contextPath}/home" class="d-inline-block text-dark text-decoration-none">
                 <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" style="max-height: 55px; width: auto;">
             </a>
        </div>

        <%-- Cột 2: Tìm kiếm --%>
        <div class="col mb-3 mb-md-0">
            <form action="${pageContext.request.contextPath}/search" method="get">
                <div class="input-group">
                    <input type="search" name="searchQuery" class="form-control rounded-pill me-1" placeholder="Tìm kiếm sản phẩm..." aria-label="Search" value="${param.searchQuery}">
                    <button class="btn btn-outline-primary rounded-pill" type="submit" style="border-color: #ced4da;">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </form>
        </div>

        <%-- Cột 3: Thông tin --%>
        <div class="col-md-auto text-end">
            <div class="d-flex justify-content-end align-items-center">
            
                <div class="me-4 d-none d-lg-block"> 
                    <strong style="color: red; font-size: 1.1rem;"><i class="fas fa-phone me-1"></i>0355.962.008</strong><br>
                    <small class="text-muted">Tư vấn bán hàng</small>
                </div>

                <div class="d-flex align-items-center" style="white-space: nowrap;">
                    <c:if test="${sessionScope.account == null}">
                        <a href="${pageContext.request.contextPath}/login.jsp" class="me-2"><i class="fas fa-sign-in-alt me-1"></i>Đăng nhập</a> /
                        <a href="${pageContext.request.contextPath}/register.jsp" class="ms-1"><i class="fas fa-user-plus me-1"></i>Đăng Ký</a>
                    </c:if>

                    <c:if test="${sessionScope.account != null}">
                        <c:choose>
                            <c:when test="${sessionScope.account.role == 'admin'}">
                                <a href="${pageContext.request.contextPath}/admin/dashboard" class="me-2">
                                   <i class="fas fa-user-shield me-1"></i> Chào, <strong>${sessionScope.account.full_name}</strong>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/profile" class="me-2">
                                   <i class="fas fa-user me-1"></i> Chào, <strong>${sessionScope.account.full_name}</strong>
                                </a>
                            </c:otherwise>
                        </c:choose>
                        | <a href="${pageContext.request.contextPath}/logout" class="ms-1">Đăng xuất</a>
                    </c:if>

                    <a href="${pageContext.request.contextPath}/cart.jsp" class="ms-3">
                        <i class="fas fa-shopping-cart me-1"></i>Giỏ hàng
                        <c:if test="${not empty sessionScope.cart and sessionScope.cart.size() > 0}">
                             (<span class="badge bg-primary rounded-pill">${sessionScope.cart.size()}</span>)
                        </c:if>
                    </a>
                 </div>
            </div>
        </div>
        
    </header>
</div>

<%-- === SỬA THANH MENU MÀU CAM TẠI ĐÂY === --%>
<nav class="navbar navbar-expand-lg sticky-top" style="background-color: #f58220;">
    <div class="container">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link text-white fw-bold" href="${pageContext.request.contextPath}/home">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white fw-bold" href="${pageContext.request.contextPath}/gioi-thieu">Giới thiệu</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white fw-bold" href="${pageContext.request.contextPath}/he-thong-cua-hang">Hệ thống cửa hàng</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white fw-bold" href="${pageContext.request.contextPath}/chinh-sach-bao-hanh">Chính sách bảo hành</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white fw-bold" href="${pageContext.request.contextPath}/tin-tuc">Tin tức</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white fw-bold" href="${pageContext.request.contextPath}/lien-he">Liên hệ</a>
                </li>
            </ul>
        </div>
    </div>
</nav>