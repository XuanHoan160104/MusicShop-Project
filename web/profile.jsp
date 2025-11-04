<%--
    File: profile.jsp
    ĐÃ SỬA: Thêm Mô tả và Nút "Mua lại"
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty sessionScope.account}">
    <% response.sendRedirect(request.getContextPath() + "/login.jsp"); %>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Thông tin tài khoản - ${sessionScope.account.full_name}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <%-- CSS mới để tùy chỉnh giao diện đơn hàng --%>
    <style>
        .order-history-card {
            background-color: #fff;
            border: 1px solid #eee;
            border-radius: 8px;
            margin-bottom: 1.5rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        .order-history-header {
            padding: 1rem 1.5rem;
            border-bottom: 1px solid #eee;
        }
        .order-history-body {
            padding: 1.5rem;
        }
        .order-item {
            display: flex;
            margin-bottom: 1rem;
        }
        .order-item-img {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 4px;
            margin-right: 1rem;
        }
        .order-item-details {
            flex-grow: 1;
        }
        .order-history-footer {
            padding: 1rem 1.5rem;
            background-color: #fafafa;
            border-top: 1px solid #eee;
            /* Căn lề cho các nút */
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .order-status-badge {
            font-size: 0.9rem;
            font-weight: 600;
        }
        /* CSS cho mô tả ngắn */
        .item-description {
            font-size: 0.85rem;
            color: #6c757d; /* text-muted */
            /* Giới hạn mô tả chỉ 2 dòng */
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    </style>
</head>
<body>

    <jsp:include page="header.jsp"></jsp:include>

    <div class="container mt-4" style="min-height: 70vh;">
        <div class="row">
            <div class="col-md-3">
                <jsp:include page="sidebar.jsp"></jsp:include>
            </div>

            <div class="col-md-9">
                <h2>Thông tin Tài khoản</h2>
                <%-- (Form sửa thông tin và thông báo giữ nguyên) --%>
                <c:if test="${not empty sessionScope.profileMessage}">
                    <div id="autoCloseAlert" class="alert alert-${sessionScope.profileStatus} alert-dismissible fade show" role="alert">
                        ${sessionScope.profileMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="profileMessage" scope="session"/>
                    <c:remove var="profileStatus" scope="session"/>
                </c:if>
                <form action="${pageContext.request.contextPath}/update-profile" method="post" class="card mb-4">
                    <div class="card-body">
                         <%-- (Nội dung form sửa profile giữ nguyên) --%>
                        <c:set var="user" value="${sessionScope.account}" />
                        <div class="mb-3 row"><label class="col-sm-3 col-form-label">Tên đăng nhập:</label><div class="col-sm-9"><input type="text" readonly class="form-control-plaintext" value="${user.username}"></div></div>
                        <div class="mb-3 row"><label for="fullname" class="col-sm-3 col-form-label">Họ và tên (*):</label><div class="col-sm-9"><input type="text" class="form-control" id="fullname" name="fullname" value="${user.full_name}" required></div></div>
                        <div class="mb-3 row"><label for="email" class="col-sm-3 col-form-label">Email (*):</label><div class="col-sm-9"><input type="email" class="form-control" id="email" name="email" value="${user.email}" required></div></div>
                        <div class="mb-3 row"><label for="address" class="col-sm-3 col-form-label">Địa chỉ:</label><div class="col-sm-9"><input type="text" class="form-control" id="address" name="address" value="${user.address}"></div></div>
                        <div class="row"><div class="col-sm-9 offset-sm-3"><button type="submit" class="btn btn-primary">Lưu thay đổi</button><a href="${pageContext.request.contextPath}/change-password" class="btn btn-secondary ms-2">Đổi Mật khẩu</a></div></div>
                    </div>
                </form>

                <h2 class="mt-4">Lịch sử Đơn hàng</h2>
                
                <%-- === SỬA LẠI KHỐI HIỂN THỊ LỊCH SỬ ĐƠN HÀNG === --%>
                <c:choose>
                    <c:when test="${empty requestScope.orderHistory}">
                        <div class="alert alert-info">
                            Bạn chưa có đơn hàng nào.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${requestScope.orderHistory}" var="item">
                            <div class="order-history-card">
                                <div class="order-history-header d-flex justify-content-between align-items-center">
                                    <span>Ngày đặt: <fmt:formatDate value="${item.orderDate}" pattern="dd/MM/yyyy"/></span>
                                    
                                    <%-- Trạng thái đơn hàng --%>
                                    <c:choose>
                                        <c:when test="${item.orderStatus == 'Pending'}"><span class="badge bg-warning text-dark order-status-badge">Chờ xử lý</span></c:when>
                                        <c:when test="${item.orderStatus == 'Processing'}"><span class="badge bg-info text-dark order-status-badge">Đang giao hàng</span></c:when>
                                        <c:when test="${item.orderStatus == 'Shipped'}"><span class="badge bg-success order-status-badge">Đã giao</span></c:when>
                                        <c:when test="${item.orderStatus == 'Cancelled'}"><span class="badge bg-danger order-status-badge">Đã hủy</span></c:when>
                                        <c:otherwise><span class="badge bg-secondary order-status-badge">${item.orderStatus}</span></c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <div class="order-history-body">
                                    <div class="order-item">
                                        <img src="${pageContext.request.contextPath}/${item.productImageUrl}" alt="${item.productName}" class="order-item-img">
                                        <div class="order-item-details">
                                            <h6 class="mb-1">${item.productName}</h6>
                                            <small class="text-muted d-block">Số lượng: ${item.quantity}</small>
                                            
                                            <%-- THÊM MÔ TẢ NGẮN (bị cắt 2 dòng) --%>
                                            <small class="text-muted d-block item-description">${item.productDescription}</small>
                                        </div>
                                    </div>
                                </div>

                                <div class="order-history-footer">
                                    <strong>Tổng số tiền: 
                                        <span class="text-danger fs-5">
                                            <fmt:formatNumber value="${item.totalItemPrice}" type="currency" currencySymbol="đ"/>
                                        </span>
                                    </strong>
                                    
                                    <%-- THÊM NÚT "MUA LẠI" --%>
                                    <%-- Nút này sẽ gọi AddToCartServlet, thêm 1 sản phẩm vào giỏ --%>
                                    <a href="${pageContext.request.contextPath}/add-to-cart?productID=${item.productId}&quantity=1" 
                                       class="btn btn-primary btn-sm">Mua lại</a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                <%-- ======================================================= --%>

            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp"></jsp:include>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        var alert = document.getElementById('autoCloseAlert');
        if (alert) {
            setTimeout(function() {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 3000); 
        }
    </script>
    
</body>
</html>