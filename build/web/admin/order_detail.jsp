<%--
    File: /admin/order_detail.jsp
    Trang Admin hiển thị chi tiết 1 đơn hàng (để đóng hàng/in đơn)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Chi tiết Đơn hàng #${requestScope.orderInfo.order_id}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    
    <%-- CSS riêng cho trang này --%>
    <style>
        .order-detail-card { margin-bottom: 1.5rem; }
        .product-img-thumbnail { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; }
        
        /* CSS cho bản in (khi bấm nút In) */
        @media print {
            body { padding: 0; }
            /* Ẩn mọi thứ KHÔNG cần in */
            .no-print, 
            .navbar, 
            .sidebar, 
            .footer, 
            .btn {
                display: none !important;
            }
            /* Đảm bảo nội dung in chiếm toàn bộ trang */
            .print-container {
                width: 100% !important;
                padding: 0 !important;
                margin: 0 !important;
            }
            .print-card {
                box-shadow: none !important;
                border: 1px solid #ccc !important;
            }
        }
    </style>
</head>
<body>

    <%-- Ẩn header/sidebar/footer khi in --%>
    <div class="no-print">
        <jsp:include page="header_admin.jsp"></jsp:include>
    </div>

    <div class="container-fluid print-container">
        <div class="row">
            <%-- Ẩn sidebar khi in --%>
            <div class="no-print">
                <jsp:include page="sidebar_admin.jsp"></jsp:include>
            </div>

            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <c:set var="order" value="${requestScope.orderInfo}" />
                
                <%-- Tiêu đề và Nút In (Ẩn khi in) --%>
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom no-print">
                    <h1 class="h2">Chi tiết Đơn hàng #${order.order_id}</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-sm btn-outline-secondary me-2">
                           <i class="fas fa-arrow-left me-1"></i> Quay lại
                        </a>
                        <%-- Nút In: Gọi JavaScript window.print() --%>
                        <button class.="btn btn-sm btn-primary" onclick="window.print();">
                            <i class="fas fa-print me-1"></i> In đơn hàng
                        </button>
                    </div>
                </div>

                <%-- Phần nội dung sẽ được in (Thông tin giao hàng + Sản phẩm) --%>
                <div class="card order-detail-card print-card">
                    <div class="card-header">
                        <div class="row">
                            <div class="col-md-4">
                                <strong>Mã đơn:</strong> #${order.order_id}
                            </div>
                            <div class="col-md-4">
                                <strong>Ngày đặt:</strong> <fmt:formatDate value="${order.order_date}" pattern="dd/MM/yyyy"/>
                            </div>
                            <div class="col-md-4">
                                <strong>Trạng thái:</strong>
                                <span class="badge 
                                    <c:choose>
                                        <c:when test='${order.status == "Pending"}'>bg-warning text-dark</c:when>
                                        <c:when test='${order.status == "Processing"}'>bg-info text-dark</c:when>
                                        <c:when test='${order.status == "Shipped"}'>bg-success</c:when>
                                        <c:when test='${order.status == "Cancelled"}'>bg-danger</c:when>
                                        <c:otherwise>bg-secondary</c:otherwise>
                                    </c:choose>
                                ">${order.status}</span>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <h5 class="card-title">Thông tin Giao hàng (Người nhận)</h5>
                        <p class="card-text mb-0">
                            <strong>Họ tên:</strong> ${order.shipping_name}
                        </p>
                        <p class="card-text mb-0">
                            <strong>Số điện thoại:</strong> ${order.shipping_phone}
                        </p>
                        <p class="card-text">
                            <strong>Địa chỉ:</strong> ${order.shipping_address}
                        </p>

                        <hr>
                        <h5 class="card-title">Danh sách Sản phẩm</h5>
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Ảnh</th>
                                    <th>Tên Sản phẩm</th>
                                    <th>Số lượng</th>
                                    <th>Đơn giá (lúc mua)</th>
                                    <th>Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${requestScope.orderDetails}" var="item">
                                    <tr>
                                        <td>
                                            <img src="${pageContext.request.contextPath}/${item.productImageUrl}" alt="${item.productName}" class="product-img-thumbnail">
                                        </td>
                                        <td>${item.productName}</td>
                                        <td>${item.quantity}</td>
                                        <td><fmt:formatNumber value="${item.priceAtPurchase}" type="currency" currencySymbol="đ"/></td>
                                        <td>
                                            <fmt:formatNumber value="${item.totalItemPrice}" type="currency" currencySymbol="đ"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot class="table-group-divider">
                                <tr>
                                    <td colspan="4" class="text-end"><strong>Tổng cộng:</strong></td>
                                    <td class="fs-5 text-danger fw-bold">
                                        <fmt:formatNumber value="${order.total_amount}" type="currency" currencySymbol="đ"/>
                                    </td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>

            </main>
        </div>
    </div>
    
    <div class="no-print">
        <jsp:include page="footer_admin.jsp"></jsp:include>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>