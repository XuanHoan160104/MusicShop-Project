<%--
    File: /admin/news.jsp
    Trang quản lý Tin tức cho Admin
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin - Quản lý Tin tức</title>
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
                    <h1 class="h2"><i class="fas fa-newspaper me-2"></i>Quản lý Tin tức & Khuyến mãi</h1>
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
                            <c:if test="${not empty requestScope.editNews}">
                                <i class="fas fa-edit me-2"></i>Sửa tin tức
                            </c:if>
                            <c:if test="${empty requestScope.editNews}">
                                <i class="fas fa-plus me-2"></i>Thêm tin tức mới về sự kiện khuyến mãi
                            </c:if>
                        </h5>
                    </div>
                    <div class="card-body">
                        <form method="post" action="news" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="${not empty requestScope.editNews ? 'update' : 'create'}">
                            <c:if test="${not empty requestScope.editNews}">
                                <input type="hidden" name="news_id" value="${requestScope.editNews.news_id}">
                                <input type="hidden" name="oldImageUrl" value="${requestScope.editNews.image_url}">
                            </c:if>
                            <div class="row">
                                <div class="col-md-12 mb-3">
                                    <label class="form-label">Tiêu đề <span class="text-danger">*</span>:</label>
                                    <input type="text" class="form-control" name="title" value="${requestScope.editNews.title}" required>
                                </div>
                                <div class="col-md-12 mb-3">
                                    <label class="form-label">Nội dung <span class="text-danger">*</span>:</label>
                                    <textarea class="form-control" name="content" rows="6" required>${requestScope.editNews.content}</textarea>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <c:if test="${not empty requestScope.editNews && not empty requestScope.editNews.image_url}">
                                        <label class="form-label">Ảnh hiện tại:</label><br>
                                        <img src="${pageContext.request.contextPath}/${requestScope.editNews.image_url}" alt="Ảnh tin tức" style="max-width: 200px; max-height: 150px; border: 1px solid #ddd; border-radius: 4px; margin-bottom: 10px;">
                                    </c:if>
                                    <label class="form-label">Upload hình ảnh:</label>
                                    <input type="file" class="form-control" name="imageFile" accept="image/png, image/jpeg, image/jpg">
                                    <small class="text-muted">Chọn file ảnh để upload (PNG, JPG, JPEG)</small>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Mã giảm giá (kèm theo):</label>
                                    <select class="form-select" name="voucher_id">
                                        <option value="">-- Không chọn mã giảm giá --</option>
                                        <c:forEach items="${requestScope.voucherList}" var="voucher">
                                            <option value="${voucher.voucher_id}" 
                                                ${requestScope.editNews.voucher_id != null && requestScope.editNews.voucher_id == voucher.voucher_id ? 'selected' : ''}>
                                                ${voucher.code} - 
                                                <c:if test="${voucher.discount_type == 'percentage'}">${voucher.discount_value}%</c:if>
                                                <c:if test="${voucher.discount_type == 'fixed'}"><fmt:formatNumber value="${voucher.discount_value}" type="currency" currencySymbol="đ"/></c:if>
                                                (${voucher.discount_type})
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <small class="text-muted">Người dùng chỉ có thể sử dụng mã giảm giá khi admin đăng trong tin tức này</small>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Trạng thái:</label>
                                    <select class="form-select" name="is_published">
                                        <option value="0" ${requestScope.editNews != null && !requestScope.editNews.is_published ? 'selected' : ''}>Draft (Nháp - Chưa hiển thị)</option>
                                        <option value="1" ${requestScope.editNews == null || requestScope.editNews.is_published ? 'selected' : ''}>Published (Đã đăng - Người dùng có thể xem)</option>
                                    </select>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-2"></i>${not empty requestScope.editNews ? 'Cập nhật' : 'Thêm'} tin tức
                            </button>
                            <c:if test="${not empty requestScope.editNews}">
                                <a href="news" class="btn btn-secondary">
                                    <i class="fas fa-times me-2"></i>Hủy
                                </a>
                            </c:if>
                        </form>
                    </div>
                </div>
                
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="fas fa-list me-2"></i>Danh sách tin tức</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-striped table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tiêu đề</th>
                                        <th>Mã giảm giá</th>
                                        <th>Trạng thái</th>
                                        <th>Ngày đăng</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty requestScope.newsList}">
                                            <tr>
                                                <td colspan="6" class="text-center text-muted">Chưa có tin tức nào</td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach items="${requestScope.newsList}" var="news">
                                                <tr>
                                                    <td>${news.news_id}</td>
                                                    <td>
                                                        <strong>${news.title}</strong>
                                                        <c:if test="${not empty news.image_url}">
                                                            <br><small class="text-muted"><i class="fas fa-image me-1"></i>Có hình ảnh</small>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <c:if test="${not empty news.voucher_id}">
                                                            <span class="badge bg-success">
                                                                <i class="fas fa-ticket-alt me-1"></i>Voucher ID: ${news.voucher_id}
                                                            </span>
                                                        </c:if>
                                                        <c:if test="${empty news.voucher_id}">
                                                            <span class="text-muted">-</span>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <c:if test="${news.is_published}">
                                                            <span class="badge bg-success"><i class="fas fa-eye me-1"></i>Published</span>
                                                        </c:if>
                                                        <c:if test="${!news.is_published}">
                                                            <span class="badge bg-secondary"><i class="fas fa-eye-slash me-1"></i>Draft</span>
                                                        </c:if>
                                                    </td>
                                                    <td><fmt:formatDate value="${news.created_at}" pattern="dd-MM-yyyy HH:mm"/></td>
                                                    <td>
                                                        <a href="news?news_id=${news.news_id}" class="btn btn-sm btn-primary" title="Sửa">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                        <a href="news?action=delete&news_id=${news.news_id}" 
                                                           class="btn btn-sm btn-danger" 
                                                           onclick="return confirm('Bạn chắc chắn muốn xóa tin tức này?')"
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
