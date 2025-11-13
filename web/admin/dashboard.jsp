<%--
    File: /admin/dashboard.jsp
    ĐÃ SỬA: Cập nhật thẻ Thống kê (Doanh thu Tuần/Tháng)
    ĐÃ SỬA: Sửa lỗi cú pháp </Giao>
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin Dashboard - Quản lý Đơn hàng</title>
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
            
            /* Thêm CSS cho thẻ card khi là link */
            .card-link {
                display: block;
                text-decoration: none;
                transition: transform 0.2s ease-in-out;
            }
            .card-link:hover {
                transform: translateY(-5px); /* Hiệu ứng nhấc lên */
                box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            }
        </style>
    </head>
    <body>
        
        <jsp:include page="header_admin.jsp"></jsp:include>

        <div class="container-fluid">
            <div class="row">
                <jsp:include page="sidebar_admin.jsp"></jsp:include>

                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    
                    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                        <h1 class="h2">TRANG QUẢN TRỊ</h1>
                        <div class="btn-toolbar mb-2 mb-md-0">
                            <a href="${pageContext.request.contextPath}/home" class="btn btn-sm btn-outline-secondary">
                               <i class="fas fa-home me-1"></i> Về Trang Chủ
                            </a>
                        </div>
                    </div>
                    
                    <%-- === THAY THẾ KHỐI THỐNG KÊ CŨ BẰNG KHỐI NÀY === --%>
                    <div class="row my-4">
                        <div class="col-md-4">
                            <%-- Thẻ này là link dẫn đến trang Biểu đồ (chúng ta sẽ làm sau) --%>
                            <a href="${pageContext.request.contextPath}/admin/report" class="card-link">
                                <div class="card text-white bg-success h-100">
                                    <div class="card-body">
                                        <h5 class="card-title">Doanh thu Tháng này (Đã giao)</h5>
                                        <p class="card-text fs-3 fw-bold">
                                            <%-- Dùng biến 'monthlyRevenue' mới từ Servlet --%>
                                            <fmt:formatNumber value="${requestScope.monthlyRevenue}" type="currency" currencySymbol="đ"/>
                                        </p>
                                        <small class="text-white-50">Bấm để xem chi tiết biểu đồ &raquo;</small>
                                    </div>
                                </div>
                            </a>
                        </div>
                        <div class="col-md-4">
                            <div class="card text-dark bg-info h-100">
                                <div class="card-body">
                                    <h5 class="card-title">Doanh thu Tuần này (Đã giao)</h5>
                                    <p class="card-text fs-3 fw-bold">
                                        <%-- Dùng biến 'weeklyRevenue' mới từ Servlet --%>
                                        <fmt:formatNumber value="${requestScope.weeklyRevenue}" type="currency" currencySymbol="đ"/>
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="card text-dark bg-warning h-100">
                                <div class="card-body">
                                    <h5 class="card-title">Đơn hàng Chờ xử lý</h5>
                                    <p class="card-text fs-3 fw-bold">
                                        ${requestScope.pendingOrders}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <%-- =============================================== --%>

                    <h3 class="mt-4">Quản lý Đơn hàng</h3>
                    
                    <%-- Nút "In Đơn Loạt" (dùng JavaScript) --%>
                    <div class="mb-3">
                        <button type="button" id="batchPrintButton" class="btn btn-info">
                            <i class="fas fa-print me-1"></i> In các đơn đã chọn
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-striped table-sm table-hover align-middle">
                            <thead>
                                <tr>
                                    <th><input class="form-check-input" type="checkbox" id="selectAllCheckbox"></th>
                                    <th>ID Đơn</th>
                                    <th>Ngày đặt</th>
                                    <th>Khách hàng</th>
                                    <th>SĐT</th>
                                    <th>Địa chỉ</th>
                                    <th>Tổng tiền</th>
                                    <th>Trạng thái</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${requestScope.orderList}" var="o">
                                    <tr>
                                        <td>
                                            <input class="form-check-input order-checkbox" type="checkbox" name="selectedOrderIds" value="${o.order_id}">
                                        </td>
                                        <td>${o.order_id}</td>
                                        <td><fmt:formatDate value="${o.order_date}" pattern="dd-MM-yyyy"/></td>
                                        <td>${o.shipping_name}</td>
                                        <td>${o.shipping_phone}</td>
                                        <td>${o.shipping_address}</td>
                                        <td class="text-danger fw-bold"><fmt:formatNumber value="${o.total_amount}" type="currency" currencySymbol="đ"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${o.status == 'Pending'}"><span class="badge bg-warning text-dark">Chờ xử lý</span></c:when>
                                                <c:when test="${o.status == 'Processing'}"><span class="badge bg-info text-dark">Đang giao hàng</span></c:when>
                                                <c:when test="${o.status == 'Shipped'}"><span class="badge bg-success">Đã giao</span></c:when>
                                                <c:when test="${o.status == 'Cancelled'}"><span class="badge bg-danger">Đã hủy</span></c:when>
                                                <c:otherwise><span class="badge bg-secondary">${o.status}</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="d-flex">
                                                <form action="${pageContext.request.contextPath}/admin/update-status" method="post" class="d-flex">
                                                    <input type="hidden" name="orderId" value="${o.order_id}">
                                                    <select name="newStatus" class="form-select form-select-sm" style="width: 120px;">
                                                        <option value="Pending" ${o.status == 'Pending' ? 'selected' : ''}>Chờ xử lý</option>
                                                        <option value="Processing" ${o.status == 'Processing' ? 'selected' : ''}>Đang giao hàng</option>
                                                        
                                                        <%-- === SỬA LỖI CÚ PHÁP: </Giao> thành </option> === --%>
                                                        <option value="Shipped" ${o.status == 'Shipped' ? 'selected' : ''}>Đã giao</option>
                                                        
                                                        <option value="Cancelled" ${o.status == 'Cancelled' ? 'selected' : ''}>Hủy</option>
                                                    </select>
                                                    <button type="submit" class="btn btn-sm btn-primary ms-1">Cập nhật</button>
                                                </form>
                                                
                                                <a href="${pageContext.request.contextPath}/admin/order-detail?orderId=${o.order_id}" 
                                                   class="btn btn-sm btn-outline-info ms-1">
                                                   Xem
                                                </a>
                                            </div>
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
        
        <script>
            document.getElementById('selectAllCheckbox').addEventListener('click', function(event) {
                var checkboxes = document.querySelectorAll('.order-checkbox');
                for (var checkbox of checkboxes) {
                    checkbox.checked = event.target.checked;
                }
            });
            
            document.getElementById('batchPrintButton').addEventListener('click', function() {
                var form = document.createElement('form');
                form.setAttribute('method', 'post');
                form.setAttribute('action', '${pageContext.request.contextPath}/admin/batch-print');
                form.setAttribute('target', '_blank');
                var selectedCheckboxes = document.querySelectorAll('.order-checkbox:checked');
                if (selectedCheckboxes.length === 0) {
                    alert('Bạn chưa chọn đơn hàng nào để in.');
                    return;
                }
                selectedCheckboxes.forEach(function(checkbox) {
                    var hiddenField = document.createElement('input');
                    hiddenField.setAttribute('type', 'hidden');
                    hiddenField.setAttribute('name', 'selectedOrderIds');
                    hiddenField.setAttribute('value', checkbox.value);
                    form.appendChild(hiddenField);
                });
                document.body.appendChild(form);
                form.submit();
                document.body.removeChild(form);
            });
        </script>
    </body>
</html>