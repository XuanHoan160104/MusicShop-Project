<%--
    File: checkout.jsp (Trang xác nhận đơn hàng)
    ĐÃ SỬA: Sửa lại cú pháp comment HTML
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty sessionScope.account}">
    <% response.sendRedirect(request.getContextPath() + "/login.jsp"); %>
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Thanh toán - Nhạc cụ Vĩnh Phúc</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>
    <body>

        <jsp:include page="header.jsp"></jsp:include>

        <div class="container mt-4">
            <div class="row">

                <div class="col-md-3">
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>

                <div class="col-md-9">
                    <h3>XÁC NHẬN ĐƠN HÀNG</h3>

                    <c:if test="${empty sessionScope.cart or sessionScope.cart.size() == 0}">
                        <div class="alert alert-info">
                            Giỏ hàng của bạn đang rỗng.
                            <a href="${pageContext.request.contextPath}/home" class="alert-link">Quay lại trang chủ</a>.
                        </div>
                    </c:if>

                    <c:if test="${not empty requestScope.error}">
                        <div class="alert alert-danger" role="alert">
                            ${requestScope.error}
                        </div>
                    </c:if>

                    <c:if test="${not empty sessionScope.cart and sessionScope.cart.size() > 0}">
                        <form action="${pageContext.request.contextPath}/checkout" method="post" class="row">

                            <div class="col-md-6">
                                <h4>Thông tin người nhận</h4>
                                <c:set var="user" value="${sessionScope.account}" />
                                <div class="mb-3">
                                    <label class="form-label">Họ và tên (*):</label>
                                    <input type="text" class="form-control" name="shipping_name" value="${user.full_name}" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Số điện thoại (*):</label>
                                    <input type="tel" class="form-control" name="shipping_phone" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Địa chỉ giao hàng (*):</label>
                                    <input type="text" class="form-control" name="shipping_address" value="${user.address}" required>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <h4>Tóm tắt đơn hàng</h4>
                                <ul class="list-group mb-3">
                                    <c:set var="totalCartPrice" value="0" />
                                    <c:forEach items="${sessionScope.cart.values()}" var="item">
                                        <li class="list-group-item">
                                            <div class="d-flex justify-content-between align-items-center mb-2">
                                                <div class="flex-grow-1">
                                                    <h6 class="my-0">${item.product.name}</h6>
                                                    <small class="text-muted">Đơn giá: <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="đ"/></small>
                                                </div>
                                                <div class="text-end">
                                                    <strong class="text-danger">
                                                        <fmt:formatNumber value="${item.totalItemPrice}" type="currency" currencySymbol="đ"/>
                                                    </strong>
                                                </div>
                                            </div>
                                            <div class="d-flex align-items-center">
                                                <label class="form-label me-2 mb-0">Số lượng:</label>
                                                <div class="d-flex align-items-center" style="gap: 5px;">
                                                    <button type="button" class="btn btn-sm btn-outline-secondary" onclick="decreaseQuantity(${item.product.product_id}, ${item.product.stock_quantity})">-</button>
                                                    <input type="number" 
                                                           class="form-control form-control-sm text-center" 
                                                           value="${item.quantity}" 
                                                           min="1" 
                                                           max="${item.product.stock_quantity}" 
                                                           style="width: 70px;"
                                                           id="quantity_${item.product.product_id}"
                                                           onchange="updateQuantity(${item.product.product_id}, ${item.product.stock_quantity})">
                                                    <button type="button" class="btn btn-sm btn-outline-secondary" onclick="increaseQuantity(${item.product.product_id}, ${item.product.stock_quantity})">+</button>
                                                </div>
                                            </div>
                                        </li>
                                        <c:set var="totalCartPrice" value="${totalCartPrice + item.totalItemPrice}" />
                                    </c:forEach>

                                    <!-- Mã giảm giá -->
                                    <li class="list-group-item">
                                        <div class="mb-2">
                                            <label class="form-label fw-bold">Mã giảm giá:</label>
                                            <div class="input-group">
                                                <input type="text" class="form-control" id="voucher_code" name="voucher_code" placeholder="Nhập mã giảm giá" value="${param.voucher_code}">
                                                <button type="button" class="btn btn-outline-primary" id="btn_validate_voucher">Áp dụng</button>
                                            </div>
                                            <small class="text-muted">Mã giảm giá chỉ áp dụng khi admin đăng trong tin tức</small>
                                            <div id="voucher_message" class="mt-2"></div>
                                            <input type="hidden" id="discount_amount" name="discount_amount" value="0">
                                        </div>
                                    </li>

                                    <li class="list-group-item d-flex justify-content-between" id="discount_row" style="display: none;">
                                        <span>Giảm giá:</span>
                                        <span class="text-success fw-bold" id="discount_display">-0 đ</span>
                                    </li>

                                    <li class="list-group-item d-flex justify-content-between bg-light">
                                        <strong>Tổng cộng (VND)</strong>
                                        <strong class="text-danger" id="final_total">
                                            <fmt:formatNumber value="${totalCartPrice}" type="currency" currencySymbol="đ"/>
                                        </strong>
                                    </li>
                                </ul>

                                <c:if test="${not empty requestScope.voucherError}">
                                    <div class="alert alert-danger" role="alert">
                                        ${requestScope.voucherError}
                                    </div>
                                </c:if>

                                <button type="submit" class="btn btn-success btn-lg w-100">Xác nhận Đặt hàng (COD)</button>
                            </div>
                        </form>
                    </c:if>
                </div>
            </div>
        </div>

        <jsp:include page="footer.jsp"></jsp:include>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Tính tổng tiền giỏ hàng
            var totalCartPrice = <c:out value="${totalCartPrice}" />;
            
            // Hàm tăng số lượng
            function increaseQuantity(productId, maxStock) {
                var input = document.getElementById('quantity_' + productId);
                if (!input) return;
                
                var currentValue = parseInt(input.value) || 1;
                if (currentValue < maxStock) {
                    input.value = currentValue + 1;
                    updateQuantity(productId, maxStock);
                } else {
                    alert('Số lượng không thể vượt quá tồn kho (' + maxStock + ')');
                }
            }
            
            // Hàm giảm số lượng
            function decreaseQuantity(productId, maxStock) {
                var input = document.getElementById('quantity_' + productId);
                if (!input) return;
                
                var currentValue = parseInt(input.value) || 1;
                if (currentValue > 1) {
                    input.value = currentValue - 1;
                    updateQuantity(productId, maxStock);
                }
            }
            
            // Hàm cập nhật số lượng
            function updateQuantity(productId, maxStock) {
                var input = document.getElementById('quantity_' + productId);
                if (!input) {
                    console.error('Không tìm thấy input với id: quantity_' + productId);
                    return;
                }
                
                var quantity = parseInt(input.value) || 1;
                
                // Kiểm tra giới hạn
                if (quantity < 1) {
                    quantity = 1;
                    input.value = 1;
                }
                if (quantity > maxStock) {
                    quantity = maxStock;
                    input.value = maxStock;
                    alert('Số lượng không thể vượt quá tồn kho (' + maxStock + ')');
                    return;
                }
                
                // Lưu dữ liệu form vào sessionStorage trước khi reload
                var shippingName = document.querySelector('input[name="shipping_name"]');
                var shippingPhone = document.querySelector('input[name="shipping_phone"]');
                var shippingAddress = document.querySelector('input[name="shipping_address"]');
                var voucherCode = document.getElementById('voucher_code');
                
                if (shippingName) sessionStorage.setItem('shipping_name', shippingName.value);
                if (shippingPhone) sessionStorage.setItem('shipping_phone', shippingPhone.value);
                if (shippingAddress) sessionStorage.setItem('shipping_address', shippingAddress.value);
                if (voucherCode) sessionStorage.setItem('voucher_code', voucherCode.value);
                
                // Disable input và nút để tránh click nhiều lần
                input.disabled = true;
                var buttons = document.querySelectorAll('button[onclick*="' + productId + '"]');
                buttons.forEach(function(btn) { btn.disabled = true; });
                
                // Gửi request cập nhật - đảm bảo productId là String
                var formData = new FormData();
                formData.append('productId', String(productId)); // Đảm bảo là String
                formData.append('quantity', String(quantity)); // Đảm bảo là String
                formData.append('redirectTo', 'checkout');
                
                console.log('Đang cập nhật: productId=' + productId + ', quantity=' + quantity);
                
                var xhr = new XMLHttpRequest();
                xhr.open('POST', '${pageContext.request.contextPath}/update-cart', true);
                xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            try {
                                var response = JSON.parse(xhr.responseText);
                                if (response.success) {
                                    console.log('Cập nhật thành công, đang reload...');
                                    // Reload trang để cập nhật tổng tiền và đảm bảo dữ liệu đồng bộ
                                    setTimeout(function() {
                                        location.reload();
                                    }, 300);
                                } else {
                                    alert('Cập nhật thất bại: ' + (response.message || 'Lỗi không xác định'));
                                    input.disabled = false;
                                    buttons.forEach(function(btn) { btn.disabled = false; });
                                }
                            } catch (e) {
                                // Nếu không phải JSON, có thể là redirect - reload trang
                                console.log('Response không phải JSON, reload trang...');
                                setTimeout(function() {
                                    location.reload();
                                }, 300);
                            }
                        } else {
                            console.error('Lỗi cập nhật: status=' + xhr.status);
                            alert('Có lỗi xảy ra khi cập nhật số lượng! Status: ' + xhr.status);
                            // Enable lại input và nút
                            input.disabled = false;
                            buttons.forEach(function(btn) { btn.disabled = false; });
                        }
                    }
                };
                xhr.onerror = function() {
                    console.error('Lỗi AJAX');
                    alert('Có lỗi xảy ra khi gửi request!');
                    input.disabled = false;
                    buttons.forEach(function(btn) { btn.disabled = false; });
                };
                
                // Chuyển FormData thành URL-encoded string
                var params = new URLSearchParams();
                params.append('productId', String(productId));
                params.append('quantity', String(quantity));
                params.append('redirectTo', 'checkout');
                xhr.send(params.toString());
            }
            
            // Khôi phục dữ liệu form sau khi reload
            window.addEventListener('DOMContentLoaded', function() {
                if (sessionStorage.getItem('shipping_name')) {
                    document.querySelector('input[name="shipping_name"]').value = sessionStorage.getItem('shipping_name');
                    document.querySelector('input[name="shipping_phone"]').value = sessionStorage.getItem('shipping_phone');
                    document.querySelector('input[name="shipping_address"]').value = sessionStorage.getItem('shipping_address');
                    document.getElementById('voucher_code').value = sessionStorage.getItem('voucher_code') || '';
                    
                    // Xóa dữ liệu sau khi khôi phục
                    sessionStorage.removeItem('shipping_name');
                    sessionStorage.removeItem('shipping_phone');
                    sessionStorage.removeItem('shipping_address');
                    sessionStorage.removeItem('voucher_code');
                }
            });
            
            // Validate voucher
            document.getElementById('btn_validate_voucher').addEventListener('click', function() {
                var voucherCode = document.getElementById('voucher_code').value.trim();
                var messageDiv = document.getElementById('voucher_message');
                
                if (!voucherCode) {
                    messageDiv.innerHTML = '<div class="alert alert-warning">Vui lòng nhập mã giảm giá!</div>';
                    return;
                }
                
                // Gửi AJAX request
                var xhr = new XMLHttpRequest();
                xhr.open('POST', '${pageContext.request.contextPath}/validate-voucher', true);
                xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            try {
                                var response = JSON.parse(xhr.responseText);
                                
                                if (response.success) {
                                    // Voucher hợp lệ
                                    var discountAmount = response.discount_amount;
                                    var finalAmount = response.final_amount;
                                    
                                    document.getElementById('discount_amount').value = discountAmount;
                                    document.getElementById('discount_display').textContent = '-' + formatCurrency(discountAmount) + ' đ';
                                    document.getElementById('discount_row').style.display = 'flex';
                                    document.getElementById('final_total').textContent = formatCurrency(finalAmount) + ' đ';
                                    
                                    messageDiv.innerHTML = '<div class="alert alert-success">Áp dụng mã giảm giá thành công!</div>';
                                } else {
                                    // Voucher không hợp lệ
                                    document.getElementById('discount_amount').value = 0;
                                    document.getElementById('discount_row').style.display = 'none';
                                    document.getElementById('final_total').textContent = formatCurrency(totalCartPrice) + ' đ';
                                    messageDiv.innerHTML = '<div class="alert alert-danger">' + response.message + '</div>';
                                }
                            } catch (e) {
                                messageDiv.innerHTML = '<div class="alert alert-danger">Lỗi xử lý phản hồi!</div>';
                            }
                        } else {
                            messageDiv.innerHTML = '<div class="alert alert-danger">Có lỗi xảy ra khi kiểm tra mã giảm giá!</div>';
                        }
                    }
                };
                
                xhr.send('voucher_code=' + encodeURIComponent(voucherCode) + '&cart_total=' + totalCartPrice);
            });
            
            // Format currency
            function formatCurrency(amount) {
                return new Intl.NumberFormat('vi-VN').format(Math.round(amount));
            }
        </script>
    </body>
</html>