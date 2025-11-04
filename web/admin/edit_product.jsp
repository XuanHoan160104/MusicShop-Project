<%--
    File: /admin/edit_product.jsp
    ĐÃ SỬA: Hỗ trợ Upload file ảnh mới
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin - Sửa Sản phẩm: ${requestScope.product.name}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        body { font-size: .875rem; }
        .sidebar { position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; padding: 48px 0 0; box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1); }
        main { padding-top: 1rem; }
        .current-product-img { max-width: 150px; height: auto; margin-top: 10px; border-radius: 4px; }
    </style>
</head>
<body>

    <jsp:include page="header_admin.jsp"></jsp:include>

    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar_admin.jsp"></jsp:include>

            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">Sửa Sản phẩm</h1>
                </div>

                <c:set var="p" value="${requestScope.product}" />

                <%-- === SỬA TẠI ĐÂY: Thêm enctype="multipart/form-data" === --%>
                <form action="${pageContext.request.contextPath}/admin/edit-product" method="post" enctype="multipart/form-data">
                    
                    <input type="hidden" name="id" value="${p.product_id}">
                    
                    <div class="mb-3">
                        <label for="name" class="form-label">Tên Sản phẩm (*):</label>
                        <input type="text" class="form-control" id="name" name="name" value="${p.name}" required>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Mô tả:</label>
                        <textarea class="form-control" id="description" name="description" rows="3">${p.description}</textarea>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="price" class="form-label">Giá (*):</label>
                            <input type="number" step="1000" class="form-control" id="price" name="price" value="${p.price}" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="stock" class="form-label">Số lượng tồn kho (*):</label>
                            <input type="number" class="form-control" id="stock" name="stock" value="${p.stock_quantity}" required>
                        </div>
                    </div>
                    
                    <%-- === SỬA TẠI ĐÂY: Thay đổi ô nhập ảnh === --%>
                    <div class="mb-3">
                        <label class="form-label">Ảnh hiện tại:</label><br>
                        <img src="${pageContext.request.contextPath}/${p.image_url}" alt="${p.name}" class="current-product-img">
                        <%-- Trường ẩn này lưu đường dẫn ảnh cũ --%>
                        <input type="hidden" name="oldImageUrl" value="${p.image_url}">
                    </div>
                    <div class="mb-3">
                        <label for="imageFile" class="form-label">Upload ảnh mới (nếu muốn thay đổi):</label>
                        <input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/png, image/jpeg">
                    </div>
                    <%-- ======================================= --%>
                    
                    <div class="mb-3">
                        <label for="category" class="form-label">Danh mục (*):</label>
                        <select class="form-select" id="category" name="category" required>
                            <option value="">-- Chọn Danh mục --</option>
                            <c:forEach items="${requestScope.categoryList}" var="cat">
                                <option value="${cat.category_id}" ${cat.category_id == p.category_id ? 'selected' : ''}>
                                    ${cat.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="btn btn-primary">Lưu Thay đổi</button>
                    <a href="${pageContext.request.contextPath}/admin/manage-products" class="btn btn-secondary">Hủy</a>
                </form>

            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>