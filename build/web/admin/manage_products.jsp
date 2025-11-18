<%--
    File: /admin/manage_products.jsp
    ĐÃ SỬA: Thêm class 'table-hover'
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<%@ page import="model.Product" %>

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
        .inventory-cell { cursor: pointer; color: #004a99; }
        .inventory-cell:hover { text-decoration: underline; }
        .inventory-cell.has-inventory { color: #198754; font-weight: 500; }
        .inventory-cell.no-inventory { color: #6c757d; }
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

                <%-- Thanh tìm kiếm --%>
                <div class="mb-3">
                    <form action="${pageContext.request.contextPath}/admin/manage-products" method="get" class="d-flex">
                        <input type="text" class="form-control me-2" name="search" 
                               placeholder="Tìm kiếm theo tên hoặc giá..." 
                               value="${requestScope.searchKeyword != null ? requestScope.searchKeyword : ''}">
                        <button type="submit" class="btn btn-outline-primary">
                            <i class="fas fa-search me-1"></i> Tìm kiếm
                        </button>
                        <c:if test="${requestScope.searchKeyword != null && !requestScope.searchKeyword.isEmpty()}">
                            <a href="${pageContext.request.contextPath}/admin/manage-products" class="btn btn-outline-secondary ms-2">
                                <i class="fas fa-times me-1"></i> Xóa bộ lọc
                            </a>
                        </c:if>
                    </form>
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
                                <th>Trong kho</th>
                                <th>Tồn kho</th>
                                <th>Danh mục ID</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${requestScope.productList}" var="p">
                                <%
                                    // Tính số ngày sản phẩm đã trong kho và so sánh với ngưỡng tùy chỉnh
                                    model.Product product = (model.Product) pageContext.getAttribute("p");
                                    long daysInStock = 0;
                                    int threshold = 30; // Mặc định 30 ngày
                                    if (product.getWarehouse_date() != null) {
                                        Date now = new Date();
                                        Date warehouseDate = product.getWarehouse_date();
                                        long diffInMillis = now.getTime() - warehouseDate.getTime();
                                        daysInStock = diffInMillis / (1000 * 60 * 60 * 24);
                                    }
                                    // Lấy ngưỡng tùy chỉnh từ product
                                    if (product.getInventory_days_threshold() > 0) {
                                        threshold = product.getInventory_days_threshold();
                                    }
                                    pageContext.setAttribute("daysInStock", daysInStock);
                                    pageContext.setAttribute("threshold", threshold);
                                %>
                                <tr>
                                    <td>${p.product_id}</td>
                                    <td>
                                        <img src="${pageContext.request.contextPath}/${p.image_url}" alt="${p.name}" class="product-img-thumbnail">
                                    </td>
                                    <td>${p.name}</td>
                                    <td><fmt:formatNumber value="${p.price}" type="currency" currencySymbol="đ"/></td>
                                    <td>${p.stock_quantity}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.warehouse_date != null}">
                                                <c:choose>
                                                    <c:when test="${daysInStock >= threshold && p.stock_quantity > 0}">
                                                        <span class="inventory-cell has-inventory" 
                                                              data-product-id="${p.product_id}"
                                                              data-product-name="${p.name}"
                                                              data-days-in-stock="${daysInStock}"
                                                              data-threshold="${threshold}"
                                                              data-stock-quantity="${p.stock_quantity}"
                                                              data-warehouse-date="<fmt:formatDate value='${p.warehouse_date}' pattern='dd/MM/yyyy'/>"
                                                              data-price="${p.price}"
                                                              onclick="showInventoryDetail(this)">
                                                            <strong>${p.stock_quantity} sp</strong><br>
                                                            <small class="text-muted">(${daysInStock} ngày)</small>
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="inventory-cell no-inventory"
                                                              data-product-id="${p.product_id}"
                                                              data-product-name="${p.name}"
                                                              data-days-in-stock="${daysInStock}"
                                                              data-threshold="${threshold}"
                                                              data-stock-quantity="${p.stock_quantity}"
                                                              data-warehouse-date="<fmt:formatDate value='${p.warehouse_date}' pattern='dd/MM/yyyy'/>"
                                                              data-price="${p.price}"
                                                              onclick="showInventoryDetail(this)">
                                                            <span class="text-muted">-</span><br>
                                                            <small class="text-muted">(${daysInStock}/${threshold} ngày)</small>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">- (Chưa có ngày nhập kho)</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
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

    <!-- Modal hiển thị chi tiết tồn kho -->
    <div class="modal fade" id="inventoryDetailModal" tabindex="-1" aria-labelledby="inventoryDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="inventoryDetailModalLabel">Chi tiết Tồn kho</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <h6 id="modalProductName" class="mb-3"></h6>
                    <div class="mb-3">
                        <div class="row">
                            <div class="col-md-6 mb-2">
                                <strong>Ngày nhập kho:</strong><br>
                                <span id="modalWarehouseDate" class="text-primary"></span>
                            </div>
                            <div class="col-md-6 mb-2">
                                <strong>Số ngày trong kho:</strong><br>
                                <span id="modalDaysInStock" class="fw-bold text-danger fs-5"></span> ngày
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <div class="row">
                            <div class="col-md-6 mb-2">
                                <strong>Ngưỡng tồn kho:</strong><br>
                                <span id="modalThreshold"></span> ngày
                            </div>
                            <div class="col-md-6 mb-2">
                                <strong>Số lượng hiện tại:</strong><br>
                                <span id="modalStockQuantity" class="fw-bold"></span> sản phẩm
                            </div>
                        </div>
                    </div>
                    <hr>
                    <div class="mb-3 p-3 bg-warning bg-opacity-10 border border-warning rounded">
                        <h6 class="text-warning-emphasis mb-2">
                            <i class="fas fa-box me-2"></i>Số lượng tồn đọng
                        </h6>
                        <div class="fs-4 fw-bold text-danger" id="modalInventoryQuantity">-</div>
                        <div class="text-muted small mt-1" id="modalInventoryValue"></div>
                    </div>
                    <div id="modalInventoryStatus" class="alert" role="alert"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function showInventoryDetail(element) {
            const productId = element.getAttribute('data-product-id');
            const productName = element.getAttribute('data-product-name');
            const daysInStock = parseInt(element.getAttribute('data-days-in-stock'));
            const threshold = parseInt(element.getAttribute('data-threshold'));
            const stockQuantity = parseInt(element.getAttribute('data-stock-quantity'));
            const warehouseDate = element.getAttribute('data-warehouse-date');
            const price = parseFloat(element.getAttribute('data-price')) || 0;
            
            // Điền thông tin vào modal
            document.getElementById('modalProductName').textContent = productName;
            document.getElementById('modalWarehouseDate').textContent = warehouseDate;
            document.getElementById('modalDaysInStock').textContent = daysInStock;
            document.getElementById('modalThreshold').textContent = threshold;
            document.getElementById('modalStockQuantity').textContent = stockQuantity;
            
            // Tính và hiển thị số lượng tồn đọng
            const inventoryQuantityDiv = document.getElementById('modalInventoryQuantity');
            const inventoryValueDiv = document.getElementById('modalInventoryValue');
            
            if (daysInStock >= threshold && stockQuantity > 0) {
                // Sản phẩm đã tồn đọng
                const excessDays = daysInStock - threshold;
                inventoryQuantityDiv.textContent = stockQuantity + ' sản phẩm';
                inventoryQuantityDiv.className = 'fs-4 fw-bold text-danger';
                
                // Tính giá trị tồn đọng
                const inventoryValue = stockQuantity * price;
                const formattedValue = new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND'
                }).format(inventoryValue);
                inventoryValueDiv.innerHTML = 'Giá trị tồn đọng: <strong>' + formattedValue + '</strong><br>' +
                    '<span class="text-muted">Đã vượt ngưỡng ' + excessDays + ' ngày (tồn kho ' + daysInStock + ' ngày, ngưỡng ' + threshold + ' ngày)</span>';
                
                // Hiển thị trạng thái cảnh báo
                const statusDiv = document.getElementById('modalInventoryStatus');
                statusDiv.className = 'alert alert-warning';
                statusDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i><strong>Cảnh báo:</strong> Sản phẩm đã tồn đọng <strong>' + stockQuantity + ' sản phẩm</strong> sau <strong>' + daysInStock + ' ngày</strong> (vượt ngưỡng ' + threshold + ' ngày). Cần xem xét giảm giá hoặc khuyến mãi để giải phóng hàng tồn kho.';
            } else if (stockQuantity <= 0) {
                inventoryQuantityDiv.textContent = '0 sản phẩm';
                inventoryQuantityDiv.className = 'fs-4 fw-bold text-muted';
                inventoryValueDiv.textContent = 'Sản phẩm đã hết hàng trong kho';
                
                const statusDiv = document.getElementById('modalInventoryStatus');
                statusDiv.className = 'alert alert-danger';
                statusDiv.innerHTML = '<i class="fas fa-times-circle me-2"></i><strong>Hết hàng:</strong> Sản phẩm đã hết hàng trong kho.';
            } else {
                inventoryQuantityDiv.textContent = '0 sản phẩm';
                inventoryQuantityDiv.className = 'fs-4 fw-bold text-success';
                inventoryValueDiv.innerHTML = '<span class="text-muted">Sản phẩm chưa đạt ngưỡng tồn kho</span><br>' +
                    '<span class="text-muted">Hiện tại: ' + daysInStock + ' ngày / Ngưỡng: ' + threshold + ' ngày</span>';
                
                const statusDiv = document.getElementById('modalInventoryStatus');
                statusDiv.className = 'alert alert-info';
                statusDiv.innerHTML = '<i class="fas fa-info-circle me-2"></i><strong>Thông tin:</strong> Sản phẩm đã trong kho <strong>' + daysInStock + ' ngày</strong>, chưa đạt ngưỡng tồn kho (' + threshold + ' ngày).';
            }
            
            // Hiển thị modal
            const modal = new bootstrap.Modal(document.getElementById('inventoryDetailModal'));
            modal.show();
        }
    </script>
</body>
</html>