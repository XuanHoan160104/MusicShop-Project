<%--
    File: /admin/vouchers.jsp
    Trang quản lý Mã giảm giá cho Admin
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin - Quản lý Mã giảm giá</title>
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
                    <h1 class="h2"><i class="fas fa-ticket-alt me-2"></i>Quản lý Mã giảm giá</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-sm btn-outline-secondary">
                           <i class="fas fa-home me-1"></i> Về Trang Chủ
                        </a>
                    </div>
                </div>
                
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>Thao tác thành công!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>Lỗi: ${param.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">
                            <c:if test="${not empty requestScope.editVoucher}">
                                <i class="fas fa-edit me-2"></i>Sửa mã giảm giá
                            </c:if>
                            <c:if test="${empty requestScope.editVoucher}">
                                <i class="fas fa-plus me-2"></i>Thêm mã giảm giá mới
                            </c:if>
                        </h5>
                    </div>
                    <div class="card-body">
                        <form method="post" action="vouchers">
                            <input type="hidden" name="action" value="${not empty requestScope.editVoucher ? 'update' : 'create'}">
                            <c:if test="${not empty requestScope.editVoucher}">
                                <input type="hidden" name="voucher_id" value="${requestScope.editVoucher.voucher_id}">
                            </c:if>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Mã giảm giá <span class="text-danger">*</span>:</label>
                                    <input type="text" class="form-control" name="code" value="${requestScope.editVoucher.code}" required>
                                    <small class="text-muted">Mã này người dùng sẽ nhập khi checkout</small>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Loại giảm giá <span class="text-danger">*</span>:</label>
                                    <select class="form-select" name="discount_type" required>
                                        <option value="percentage" ${requestScope.editVoucher != null && requestScope.editVoucher.discount_type == 'percentage' ? 'selected' : ''}>Phần trăm (%)</option>
                                        <option value="fixed" ${requestScope.editVoucher != null && requestScope.editVoucher.discount_type == 'fixed' ? 'selected' : ''}>Số tiền cố định (VND)</option>
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Giá trị <span class="text-danger">*</span>:</label>
                                    <input type="number" class="form-control" name="discount_value" step="0.01" min="0" value="${requestScope.editVoucher.discount_value}" required>
                                    <small class="text-muted">
                                        <c:if test="${requestScope.editVoucher == null || requestScope.editVoucher.discount_type == 'percentage'}">
                                            Ví dụ: 10 (nghĩa là giảm 10%)
                                        </c:if>
                                        <c:if test="${requestScope.editVoucher != null && requestScope.editVoucher.discount_type == 'fixed'}">
                                            Ví dụ: 50000 (nghĩa là giảm 50,000 đ)
                                        </c:if>
                                    </small>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Ngày bắt đầu <span class="text-danger">*</span>:</label>
                                    <c:choose>
                                        <c:when test="${not empty requestScope.editVoucher}">
                                            <fmt:formatDate value="${requestScope.editVoucher.start_date}" pattern="yyyy-MM-dd'T'HH:mm" var="startDateFormatted"/>
                                            <input type="datetime-local" class="form-control" name="start_date" value="${startDateFormatted}" required>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="datetime-local" class="form-control" name="start_date" required>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Ngày kết thúc <span class="text-danger">*</span>:</label>
                                    <c:choose>
                                        <c:when test="${not empty requestScope.editVoucher}">
                                            <fmt:formatDate value="${requestScope.editVoucher.end_date}" pattern="yyyy-MM-dd'T'HH:mm" var="endDateFormatted"/>
                                            <input type="datetime-local" class="form-control" name="end_date" value="${endDateFormatted}" required>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="datetime-local" class="form-control" name="end_date" required>
                                        </c:otherwise>
                                    </c:choose>
                                    <small class="text-muted">Mã giảm giá chỉ có hiệu lực trong khoảng thời gian này</small>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Mô tả:</label>
                                    <textarea class="form-control" name="description" rows="2">${requestScope.editVoucher.description}</textarea>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Trạng thái:</label>
                                    <select class="form-select" name="is_active">
                                        <option value="1" ${requestScope.editVoucher == null || requestScope.editVoucher.is_active ? 'selected' : ''}>Active (Kích hoạt)</option>
                                        <option value="0" ${requestScope.editVoucher != null && !requestScope.editVoucher.is_active ? 'selected' : ''}>Inactive (Tắt)</option>
                                    </select>
                                    <small class="text-muted">Mã giảm giá chỉ hoạt động khi ở trạng thái Active</small>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-2"></i>${not empty requestScope.editVoucher ? 'Cập nhật' : 'Thêm'} mã giảm giá
                            </button>
                            <c:if test="${not empty requestScope.editVoucher}">
                                <a href="vouchers" class="btn btn-secondary">
                                    <i class="fas fa-times me-2"></i>Hủy
                                </a>
                            </c:if>
                        </form>
                    </div>
                </div>
                
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="fas fa-list me-2"></i>Danh sách mã giảm giá</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-striped table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Mã</th>
                                        <th>Loại</th>
                                        <th>Giá trị</th>
                                        <th>Bắt đầu</th>
                                        <th>Kết thúc</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty requestScope.voucherList}">
                                            <tr>
                                                <td colspan="8" class="text-center text-muted">Chưa có mã giảm giá nào</td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach items="${requestScope.voucherList}" var="voucher">
                                                <tr>
                                                    <td>${voucher.voucher_id}</td>
                                                    <td><strong>${voucher.code}</strong></td>
                                                    <td>
                                                        <span class="badge bg-info">
                                                            ${voucher.discount_type == 'percentage' ? 'Phần trăm' : 'Cố định'}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <c:if test="${voucher.discount_type == 'percentage'}">
                                                            <strong>${voucher.discount_value}%</strong>
                                                        </c:if>
                                                        <c:if test="${voucher.discount_type == 'fixed'}">
                                                            <strong><fmt:formatNumber value="${voucher.discount_value}" type="currency" currencySymbol="đ"/></strong>
                                                        </c:if>
                                                    </td>
                                                    <td><fmt:formatDate value="${voucher.start_date}" pattern="dd-MM-yyyy HH:mm"/></td>
                                                    <td><fmt:formatDate value="${voucher.end_date}" pattern="dd-MM-yyyy HH:mm"/></td>
                                                    <td>
                                                        <c:if test="${voucher.is_active}">
                                                            <span class="badge bg-success"><i class="fas fa-check me-1"></i>Active</span>
                                                        </c:if>
                                                        <c:if test="${!voucher.is_active}">
                                                            <span class="badge bg-secondary"><i class="fas fa-times me-1"></i>Inactive</span>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <a href="vouchers?voucher_id=${voucher.voucher_id}" class="btn btn-sm btn-primary" title="Sửa">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                        <a href="vouchers?action=delete&voucher_id=${voucher.voucher_id}" 
                                                           class="btn btn-sm btn-danger" 
                                                           onclick="return confirm('Bạn chắc chắn muốn xóa mã giảm giá này?')"
                                                           title="Xóa">
                                                            <i class="fas fa-trash"></i>
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>
    
    <jsp:include page="footer_admin.jsp"></jsp:include>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
