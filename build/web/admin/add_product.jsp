<%--
    File: /admin/add_product.jsp
    ĐÃ SỬA: Thêm enctype="multipart/form-data" và <input type="file">
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin - Thêm Sản phẩm</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        body { font-size: .875rem; }
        .sidebar { position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; padding: 48px 0 0; box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1); }
        main { padding-top: 1rem; }
    </style>
</head>
<body>

    <jsp:include page="header_admin.jsp"></jsp:include>

    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar_admin.jsp"></jsp:include>

            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">Thêm Sản phẩm Mới</h1>
                </div>

                <%-- 
                    === SỬA TẠI ĐÂY ===
                    1. Thêm: enctype="multipart/form-data"
                       (Bắt buộc để form có thể gửi file)
                --%>
                <form action="${pageContext.request.contextPath}/admin/add-product" method="post" enctype="multipart/form-data">
                    <div class="mb-3">
                        <label for="name" class="form-label">Tên Sản phẩm (*):</label>
                        <input type="text" class="form-control" id="name" name="name" required>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Mô tả:</label>
                        <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="price" class="form-label">Giá (*):</label>
                            <input type="number" step="1000" class="form-control" id="price" name="price" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="stock" class="form-label">Số lượng tồn kho (*):</label>
                            <input type="number" class="form-control" id="stock" name="stock" required>
                        </div>
                    </div>
                    
                    <%-- 
                        === SỬA TẠI ĐÂY ===
                        2. Đổi: type="text" thành type="file"
                           Đổi: name="imageUrl" thành name="imageFile"
                    --%>
                    <div class="mb-3">
                        <label for="imageFile" class="form-label">Hình ảnh sản phẩm:</label>
                        <input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/png, image/jpeg">
                    </div>
                    <%-- ================== --%>
                    
                    <div class="mb-3">
                        <label for="category" class="form-label">Danh mục (*):</label>
                        <select class="form-select" id="category" name="category" required>
                            <option value="">-- Chọn Danh mục --</option>
                            <%-- Dùng JSTL để lặp qua categoryList mà Servlet gửi sang --%>
                            <c:forEach items="${requestScope.categoryList}" var="cat">
                                <option value="${cat.category_id}">${cat.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="btn btn-primary">Lưu Sản phẩm</button>
                    <a href="${pageContext.request.contextPath}/admin/manage-products" class="btn btn-secondary">Hủy</a>
                </form>

            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>