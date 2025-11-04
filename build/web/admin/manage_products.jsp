<%--
    File: /admin/manage_products.jsp
    ĐÃ SỬA: Thêm class 'table-hover'
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin - Quản lý Sản phẩm</title>
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
        .product-img-thumbnail { width: 50px; height: 50px; object-fit: cover; border-radius: 4px; }
    </style>
</head>
<body>

    <jsp:include page="header_admin.jsp"></jsp:include>

    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar_admin.jsp"></jsp:include>

            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">Quản lý Sản phẩm</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/admin/add-product" class="btn btn-sm btn-outline-primary me-2">
                            <i class="fas fa-plus me-1"></i> Thêm Sản phẩm Mới
                        </a>
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-sm btn-outline-secondary">
                           <i class="fas fa-home me-1"></i> Về Trang Chủ
                        </a>
                    </div>
                </div>

                <div class="table-responsive">
                    <%-- === SỬA Ở ĐÂY: Thêm class "table-hover" === --%>
                    <table class="table table-striped table-sm table-hover align-middle">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Ảnh</th>
                                <th>Tên Sản phẩm</th>
                                <th>Giá</th>
                                <th>Tồn kho</th>
                                <th>Danh mục ID</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${requestScope.productList}" var="p">
                                <tr>
                                    <td>${p.product_id}</td>
                                    <td>
                                        <img src="${pageContext.request.contextPath}/${p.image_url}" alt="${p.name}" class="product-img-thumbnail">
                                    </td>
                                    <td>${p.name}</td>
                                    <td><fmt:formatNumber value="${p.price}" type="currency" currencySymbol="đ"/></td>
                                    <td>${p.stock_quantity}</td>
                                    <td>${p.category_id}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/edit-product?pid=${p.product_id}" class="btn btn-sm btn-outline-secondary">Sửa</a>
                                        <a href="${pageContext.request.contextPath}/admin/delete-product?pid=${p.product_id}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');">Xóa</a>
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