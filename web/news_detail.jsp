<%-- 
    File: news_detail.jsp (Trang Chi tiết Tin tức)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${requestScope.news.title} - Nhạc cụ HDH</title>
        
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <style>
            .voucher-card {
                border: 2px dashed #28a745;
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                transition: transform 0.3s ease;
            }
            .voucher-card:hover {
                transform: scale(1.02);
            }
            .voucher-code {
                font-size: 1.5rem;
                font-weight: bold;
                color: #28a745;
                letter-spacing: 2px;
                font-family: 'Courier New', monospace;
            }
            .copy-btn {
                cursor: pointer;
                transition: all 0.3s ease;
            }
            .copy-btn:hover {
                background-color: #218838 !important;
            }
        </style>
    </head>
    <body>

        <%-- 1. Nhúng Header --%>
        <jsp:include page="header.jsp"></jsp:include>

        <div class="container mt-4">
            <div class="row">
                <div class="col-md-3">
                    <%-- 2. Nhúng Sidebar --%>
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>
                
                <div class="col-md-9">
                    <%-- 3. Nội dung chi tiết tin tức --%>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/tin-tuc">Tin tức</a></li>
                            <li class="breadcrumb-item active" aria-current="page">${requestScope.news.title}</li>
                        </ol>
                    </nav>
                    
                    <article>
                        <h1 class="mb-3">${requestScope.news.title}</h1>
                        
                        <p class="text-muted mb-4">
                            <i class="fas fa-calendar me-2"></i>
                            Đăng ngày: <fmt:formatDate value="${requestScope.news.created_at}" pattern="dd/MM/yyyy HH:mm" />
                        </p>
                        
                        <c:if test="${not empty requestScope.news.image_url}">
                            <img src="${pageContext.request.contextPath}/${requestScope.news.image_url}" 
                                 class="img-fluid rounded mb-4" 
                                 alt="${requestScope.news.title}">
                        </c:if>
                        
                        <div class="news-content mb-4" style="line-height: 1.8; font-size: 1.1rem;">
                            ${requestScope.news.content}
                        </div>
                        
                        <%-- Hiển thị mã giảm giá nếu có --%>
                        <c:if test="${not empty requestScope.voucher}">
                            <div class="voucher-card card p-4 mb-4">
                                <div class="row align-items-center">
                                    <div class="col-md-8">
                                        <h4 class="text-success mb-3">
                                            <i class="fas fa-tag me-2"></i>Mã giảm giá kèm theo
                                        </h4>
                                        
                                        <div class="mb-3">
                                            <label class="form-label text-muted">Mã giảm giá:</label>
                                            <div class="input-group">
                                                <input type="text" 
                                                       class="form-control form-control-lg voucher-code" 
                                                       id="voucherCode" 
                                                       value="${requestScope.voucher.code}" 
                                                       readonly>
                                                <button class="btn btn-success copy-btn" 
                                                        type="button" 
                                                        onclick="copyVoucherCode()"
                                                        title="Sao chép mã">
                                                    <i class="fas fa-copy me-1"></i> Sao chép
                                                </button>
                                            </div>
                                        </div>
                                        
                                        <div class="mb-2">
                                            <strong>Giảm giá:</strong>
                                            <c:choose>
                                                <c:when test="${requestScope.voucher.discount_type == 'percentage'}">
                                                    <span class="text-danger fw-bold">${requestScope.voucher.discount_value}%</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-danger fw-bold">
                                                        <fmt:formatNumber value="${requestScope.voucher.discount_value}" type="currency" currencySymbol="đ"/>
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        
                                        <c:if test="${not empty requestScope.voucher.description}">
                                            <div class="mb-2">
                                                <strong>Mô tả:</strong> ${requestScope.voucher.description}
                                            </div>
                                        </c:if>
                                        
                                        <div class="mb-2">
                                            <strong>Thời gian áp dụng:</strong><br>
                                            <small class="text-muted">
                                                Từ <fmt:formatDate value="${requestScope.voucher.start_date}" pattern="dd/MM/yyyy HH:mm" />
                                                đến <fmt:formatDate value="${requestScope.voucher.end_date}" pattern="dd/MM/yyyy HH:mm" />
                                            </small>
                                        </div>
                                    </div>
                                    
                                    <div class="col-md-4 text-center">
                                        <div class="bg-light p-3 rounded">
                                            <i class="fas fa-gift fa-4x text-success mb-3"></i>
                                            <p class="mb-0">
                                                <small class="text-muted">Sử dụng mã này khi thanh toán</small>
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        
                        <div class="mt-4">
                            <a href="${pageContext.request.contextPath}/tin-tuc" class="btn btn-outline-secondary">
                                <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách tin tức
                            </a>
                        </div>
                    </article>
                    
                </div>
            </div>
        </div>

        <%-- 4. Nhúng Footer --%>
        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function copyVoucherCode() {
                const voucherCode = document.getElementById('voucherCode');
                voucherCode.select();
                voucherCode.setSelectionRange(0, 99999); // For mobile devices
                
                try {
                    document.execCommand('copy');
                    
                    // Thông báo thành công
                    const btn = event.target.closest('button');
                    const originalHTML = btn.innerHTML;
                    btn.innerHTML = '<i class="fas fa-check me-1"></i> Đã sao chép!';
                    btn.classList.remove('btn-success');
                    btn.classList.add('btn-primary');
                    
                    setTimeout(function() {
                        btn.innerHTML = originalHTML;
                        btn.classList.remove('btn-primary');
                        btn.classList.add('btn-success');
                    }, 2000);
                } catch (err) {
                    alert('Không thể sao chép mã. Vui lòng sao chép thủ công: ' + voucherCode.value);
                }
            }
        </script>
    </body>
</html>

