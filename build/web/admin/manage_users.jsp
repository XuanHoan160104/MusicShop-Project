<%--
    File: /admin/manage_users.jsp
    ĐÃ SỬA: Thêm class 'table-hover'
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin - Quản lý Người dùng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        body { font-size: .875rem; }
        .sidebar { position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; padding: 48px 0 0; box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1); }
        main { padding-top: 1rem; }
        .admin-sidebar .nav-link { font-weight: 500; color: #333; }
        .admin-sidebar .nav-link.active { color: #004a99; }
        .admin-sidebar .nav-link:hover { color: #f58220; }
        .table-hover tbody tr:hover { background-color: #f1f1f1; }
    </style>
</head>
<body>

    <jsp:include page="header_admin.jsp"></jsp:include>

    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar_admin.jsp"></jsp:include>

            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">Quản lý Người dùng</h1>
                     <div class="btn-toolbar mb-2 mb-md-0">
                         <a href="${pageContext.request.contextPath}/home" class="btn btn-sm btn-outline-secondary">
                            <i class="fas fa-home me-1"></i> Về Trang Chủ
                         </a>
                     </div>
                 </div>

                <%-- Thông báo Xóa/Sửa vai trò --%>
                <c:if test="${not empty sessionScope.deleteSuccess}"><div class="alert alert-success alert-dismissible fade show" role="alert">${sessionScope.deleteSuccess}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><c:remove var="deleteSuccess" scope="session"/></c:if>
                <c:if test="${not empty sessionScope.deleteError}"><div class="alert alert-danger alert-dismissible fade show" role="alert">${sessionScope.deleteError}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><c:remove var="deleteError" scope="session"/></c:if>
                <c:if test="${not empty sessionScope.updateRoleSuccess}"><div class="alert alert-success alert-dismissible fade show" role="alert">${sessionScope.updateRoleSuccess}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><c:remove var="updateRoleSuccess" scope="session"/></c:if>
                <c:if test="${not empty sessionScope.updateRoleError}"><div class="alert alert-danger alert-dismissible fade show" role="alert">${sessionScope.updateRoleError}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><c:remove var="updateRoleError" scope="session"/></c:if>

                <div class="table-responsive">
                    <%-- === SỬA Ở ĐÂY: Thêm class "table-hover" === --%>
                    <table class="table table-striped table-sm table-hover align-middle">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Họ tên</th>
                                <th>Email</th>
                                <th>Địa chỉ</th>
                                <th>Vai trò</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${requestScope.userList}" var="u">
                                <tr>
                                    <td>${u.user_id}</td>
                                    <td>${u.username}</td>
                                    <td>${u.full_name}</td>
                                    <td>${u.email}</td>
                                    <td>${u.address}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${u.role == 'admin'}"><span class="badge bg-danger">${u.role}</span></c:when>
                                            <c:otherwise><span class="badge bg-secondary">${u.role}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${sessionScope.account.user_id != u.user_id}">
                                            <c:choose>
                                                <c:when test="${u.role == 'customer'}">
                                                    <a href="${pageContext.request.contextPath}/admin/update-role?uid=${u.user_id}&role=admin"
                                                       class="btn btn-sm btn-outline-warning"
                                                       onclick="return confirm('Bạn có chắc muốn cấp quyền Admin cho \'${u.username}\'?');">Set Admin</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${pageContext.request.contextPath}/admin/update-role?uid=${u.user_id}&role=customer"
                                                       class="btn btn-sm btn-outline-info"
                                                       onclick="return confirm('Bạn có chắc muốn hạ quyền Admin của \'${u.username}\'?');">Set Customer</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                        <c:if test="${sessionScope.account.user_id != u.user_id}">
                                            <a href="${pageContext.request.contextPath}/admin/delete-user?uid=${u.user_id}"
                                               class="btn btn-sm btn-outline-danger"
                                               onclick="return confirm('Bạn có chắc chắn muốn xóa người dùng \'${u.username}\'? Hành động này sẽ xóa hết đơn hàng của họ!');">Xóa</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>