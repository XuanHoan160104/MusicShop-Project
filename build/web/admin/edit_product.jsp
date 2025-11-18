<%--
    File: /admin/edit_product.jsp
    ĐÃ SỬA: Hỗ trợ Upload file ảnh mới
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="model.Product" %>

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
                            <label for="stock" class="form-label">Trong kho (*):</label>
                            <input type="number" class="form-control" id="stock" name="stock" value="${p.stock_quantity}" required>
                            <small class="text-muted">Số lượng sản phẩm hiện có trong kho</small>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="warehouse_date" class="form-label">Ngày nhập kho:</label>
                            <input type="date" class="form-control" id="warehouse_date" name="warehouse_date" 
                                   value="<c:if test='${p.warehouse_date != null}'><fmt:formatDate value='${p.warehouse_date}' pattern='yyyy-MM-dd'/></c:if>">
                            <small class="text-muted">Ngày sản phẩm được nhập vào kho</small>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="inventory_days_threshold" class="form-label">Số ngày để tính tồn kho:</label>
                            <input type="number" class="form-control" id="inventory_days_threshold" name="inventory_days_threshold" 
                                   value="${p.inventory_days_threshold > 0 ? p.inventory_days_threshold : 30}" min="1" required>
                            <small class="text-muted">Số ngày tùy chỉnh để hệ thống tự động tính tồn kho (ví dụ: 1, 30, 60 ngày...)</small>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12 mb-3">
                            <label class="form-label">Tồn kho (tự động tính):</label>
                            <div class="form-control bg-light" style="padding: 0.375rem 0.75rem;">
                                <c:choose>
                                    <c:when test="${p.warehouse_date != null}">
                                        <%
                                            model.Product product = (model.Product) pageContext.getAttribute("p");
                                            long daysInStock = 0;
                                            int threshold = product.getInventory_days_threshold() > 0 ? product.getInventory_days_threshold() : 30;
                                            if (product.getWarehouse_date() != null) {
                                                java.util.Date now = new java.util.Date();
                                                java.sql.Date warehouseDate = product.getWarehouse_date();
                                                long diffInMillis = now.getTime() - warehouseDate.getTime();
                                                daysInStock = diffInMillis / (1000 * 60 * 60 * 24);
                                            }
                                            pageContext.setAttribute("daysInStock", daysInStock);
                                            pageContext.setAttribute("threshold", threshold);
                                        %>
                                        <c:choose>
                                            <c:when test="${daysInStock >= threshold && p.stock_quantity > 0}">
                                                <span class="text-success"><strong>${p.stock_quantity}</strong> (Đã trong kho ${daysInStock} ngày, ngưỡng: ${threshold} ngày)</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">- (Chưa đủ ${threshold} ngày hoặc hết hàng. Hiện tại: ${daysInStock} ngày)</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">- (Chưa có ngày nhập kho)</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <small class="text-muted">Tồn kho sẽ hiển thị khi sản phẩm đã trong kho ≥ số ngày đã cài đặt và còn hàng</small>
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