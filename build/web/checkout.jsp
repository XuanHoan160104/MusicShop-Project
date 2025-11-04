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
                                        <li class="list-group-item d-flex justify-content-between lh-sm">
                                            <div>
                                                <h6 class="my-0">${item.product.name} (SL: ${item.quantity})</h6>
                                            </div>
                                            <span class="text-muted">
                                                <fmt:formatNumber value="${item.totalItemPrice}" type="currency" currencySymbol="đ"/>
                                            </span>
                                        </li>
                                        <c:set var="totalCartPrice" value="${totalCartPrice + item.totalItemPrice}" />
                                    </c:forEach>

                                    <li class="list-group-item d-flex justify-content-between bg-light">
                                        <strong>Tổng cộng (VND)</strong>
                                        <strong class="text-danger">
                                            <fmt:formatNumber value="${totalCartPrice}" type="currency" currencySymbol="đ"/>
                                        </strong>
                                    </li>
                                </ul>

                                <button type="submit" class="btn btn-success btn-lg w-100">Xác nhận Đặt hàng (COD)</button>
                            </div>
                        </form>
                    </c:if>
                </div>
            </div>
        </div>

        <jsp:include page="footer.jsp"></jsp:include>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>