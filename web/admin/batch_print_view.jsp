<%--
    File: /admin/batch_print_view.jsp
    Trang hiển thị hàng loạt đơn hàng để in.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>In Hàng Loạt Đơn Hàng</title>
    <%-- Chỉ dùng Bootstrap CSS để định dạng --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        /* CSS cho 1 phiếu in (khổ giấy A5 hoặc A4) */
        .print-ticket {
            width: 90%; /* Kích thước trên màn hình */
            margin: 2rem auto;
            border: 2px dashed #999;
            padding: 1.5rem;
            background: #fff;
        }
        
        .product-img-print {
            width: 40px;
            height: 40px;
            object-fit: cover;
        }

        /* CSS đặc biệt cho lúc IN THẬT */
        @media print {
            body {
                margin: 0;
                padding: 0;
            }
            .print-ticket {
                width: 100%;
                margin: 0;
                border: 1px solid #000;
                padding: 1rem;
                box-shadow: none;
                /* QUAN TRỌNG: Đảm bảo mỗi đơn hàng nằm trên 1 trang giấy mới */
                page-break-after: always;
            }
            h1.print-title, .btn-print-close {
                display: none; /* Ẩn tiêu đề và nút đóng khi in */
            }
        }
    </style>
</head>
<body style="background-color: #f0f0f0;">

    <h1 class="text-center mt-3 print-title">Xem trước Phiếu In (In Hàng Loạt)</h1>
    <div class="text-center mb-3 print-title">
        <button class="btn btn-primary" onclick="window.print();"><i class="fas fa-print me-1"></i> In ngay</button>
        <button class="btn btn-secondary" onclick="window.close();">Đóng</button>
    </div>

    <%--
        Lặp qua Map ordersToPrint (key=Order, value=List<OrderHistoryItem>)
        mà BatchPrintServlet đã gửi sang.
    --%>
    <c:forEach items="${requestScope.ordersToPrint}" var="entry">
        <c:set var="order" value="${entry.key}" />
        <c:set var="details" value="${entry.value}" />
        
        <div class="print-ticket">
            <div class="row border-bottom pb-2 mb-3">
                <div class="col-6">
                    <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" style="max-height: 50px;">
                    <br><small>MusicShop</small>
                </div>
                <div class="col-6 text-end">
                    <strong>ĐƠN HÀNG #${order.order_id}</strong><br>
                    <small>Ngày đặt: <fmt:formatDate value="${order.order_date}" pattern="dd/MM/yyyy"/></small>
                </div>
            </div>
            
            <h5>Thông tin Người nhận</h5>
            <p class="mb-0"><strong>Tên:</strong> ${order.shipping_name}</p>
            <p class="mb-0"><strong>SĐT:</strong> ${order.shipping_phone}</p>
            <p class="mb-2"><strong>Địa chỉ:</strong> ${order.shipping_address}</p>
            
            <h5 class="mt-3">Chi tiết Sản phẩm</h5>
            <table class="table table-sm">
                <thead>
                    <tr>
                        <th>Sản phẩm</th>
                        <th>SL</th>
                        <th>Giá</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${details}" var="item">
                        <tr>
                            <td>
                                <img src="${pageContext.request.contextPath}/${item.productImageUrl}" class="product-img-print me-2">
                                ${item.productName}
                            </td>
                            <td>x${item.quantity}</td>
                            <td><fmt:formatNumber value="${item.priceAtPurchase}" type="currency" currencySymbol="đ"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            
            <div class="text-end mt-3">
                <h4>Tổng cộng: 
                    <span class="text-danger">
                        <fmt:formatNumber value="${order.total_amount}" type="currency" currencySymbol="đ"/>
                    </span>
                </h4>
                <p class="text-muted">(Thanh toán khi nhận hàng - COD)</p>
            </div>
        </div>
        
    </c:forEach> <%-- Hết vòng lặp các đơn hàng --%>
    
    <%-- JavaScript để tự động bật cửa sổ in --%>
    <script>
        // Tự động gọi window.print() khi trang tải xong
        window.onload = function() {
            window.print();
            // (Tùy chọn) Tự động đóng tab sau khi in
            // window.onafterprint = function() {
            //     window.close();
            // }
        }
    </script>
</body>
</html>