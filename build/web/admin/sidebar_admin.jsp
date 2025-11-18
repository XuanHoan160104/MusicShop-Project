<%--
    File: /admin/sidebar_admin.jsp
    ĐÃ SỬA: Thêm logic "activePage" và thay emoji bằng Font Awesome
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%-- Thêm class "admin-sidebar" để áp dụng CSS tùy chỉnh (nếu có) --%>
<nav id="sidebarMenu" class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse admin-sidebar">
    <div class="position-sticky pt-3">
        <ul class="nav flex-column">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/home">
                    <%-- Icon Font Awesome --%>
                    <i class="fas fa-home me-2"></i>Về Trang Chủ
                </a>
            </li>
            <hr class="my-2"> 
            
            <li class="nav-item">
                <%-- 
                    Kiểm tra JSTL: Nếu 'activePage' là 'dashboard' thì thêm class 'active'
                --%>
                <a class="nav-link ${activePage == 'dashboard' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-tachometer-alt me-2"></i>Quản lý Đơn hàng
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${activePage == 'products' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/admin/manage-products">
                    <i class="fas fa-box-open me-2"></i>Quản lý Sản phẩm
                </a>
            </li>
            <li class="nav-item">
                 <a class="nav-link ${activePage == 'users' ? 'active' : ''}" 
                    href="${pageContext.request.contextPath}/admin/manage-users">
                    <i class="fas fa-users me-2"></i>Quản lý Người dùng
                </a>
            </li>
            <li class="nav-item">
                 <a class="nav-link ${activePage == 'news' ? 'active' : ''}" 
                    href="${pageContext.request.contextPath}/admin/news">
                    <i class="fas fa-newspaper me-2"></i>Quản lý Tin tức
                </a>
            </li>
            <li class="nav-item">
                 <a class="nav-link ${activePage == 'vouchers' ? 'active' : ''}" 
                    href="${pageContext.request.contextPath}/admin/vouchers">
                    <i class="fas fa-ticket-alt me-2"></i>Quản lý Mã giảm giá
                </a>
            </li>
            <li class="nav-item">
                 <a class="nav-link ${activePage == 'chat' ? 'active' : ''}" 
                    href="${pageContext.request.contextPath}/admin/chat">
                    <i class="fas fa-comments me-2"></i>Quản lý Chat
                    <c:choose>
                        <c:when test="${not empty requestScope.unreadCount && requestScope.unreadCount > 0}">
                            <span class="badge bg-danger ms-2">${requestScope.unreadCount}</span>
                        </c:when>
                        <c:when test="${not empty sessionScope.unreadCount && sessionScope.unreadCount > 0}">
                            <span class="badge bg-danger ms-2">${sessionScope.unreadCount}</span>
                        </c:when>
                    </c:choose>
                </a>
            </li>
             <%-- 
                Link Quản lý Danh mục (chưa tạo)
            <li class="nav-item">
                 <a class="nav-link ${activePage == 'categories' ? 'active' : ''}" 
                    href="${pageContext.request.contextPath}/admin/manage-categories">
                    <i class="fas fa-list me-2"></i>Quản lý Danh mục
                </a>
            </li>
            --%>
        </ul>
    </div>
</nav>