<%--
    File: cart.jsp (Trang hiển thị giỏ hàng)
    ĐÃ SỬA: Tự động cập nhật khi thay đổi số lượng (bằng onchange)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Giỏ hàng - Nhạc cụ Vĩnh Phúc</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    </head>
    <body>

        <jsp:include page="header.jsp"></jsp:include>

        <div class="container mt-4" style="min-height: 50vh;">
            <div class="row">
                
                <div class="col-md-3">
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>
                
                <div class="col-md-9">
                    <h3>GIỎ HÀNG CỦA BẠN</h3>
                    
                    <%-- Hiển thị thông báo (ví dụ: vượt quá tồn kho) --%>
                    <c:if test="${not empty sessionScope.cartMessage}">
                        <div class="alert alert-warning alert-dismissible fade show" role="alert">
                            ${sessionScope.cartMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                        <c:remove var="cartMessage" scope="session"/>
                    </c:if>

                    <c:if test="${empty sessionScope.cart or sessionScope.cart.size() == 0}">
                        <div class="alert alert-info">
                            Chưa có sản phẩm nào trong giỏ hàng.
                        </div>
                    </c:if>
                        
                    <c:if test="${not empty sessionScope.cart and sessionScope.cart.size() > 0}">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Sản phẩm</th>
                                    <th>Đơn giá</th>
                                    <th>Số lượng</th>
                                    <th>Thành tiền</th>
                                    <th>Xóa</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:set var="totalCartPrice" value="0" />
                                <c:forEach items="${sessionScope.cart.values()}" var="item">
                                    <tr>
                                        <td>
                                            <img src="${pageContext.request.contextPath}/${item.product.image_url}" alt="${item.product.name}" style="width: 50px; height: 50px; object-fit: cover;">
                                            <span class="ms-2">${item.product.name}</span>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="đ"/>
                                        </td>
                                        <td>
                                            <%-- === SỬA LẠI FORM SỐ LƯỢNG === --%>
                                            <form action="${pageContext.request.contextPath}/update-cart" method="post" class="d-flex">
                                                <input type="hidden" name="productId" value="${item.product.product_id}">
                                                <input type="number" name="quantity" 
                                                       class="form-control form-control-sm" 
                                                       value="${item.quantity}" 
                                                       min="1" 
                                                       max="${item.product.stock_quantity}" 
                                                       style="width: 70px;"
                                                       <%-- 1. THÊM onchange: Tự động submit form khi đổi số lượng --%>
                                                       onchange="this.form.submit()">
                                                
                                                <%-- 2. XÓA NÚT "Cập nhật" --%>
                                                <%-- <button type="submit" class="btn btn-sm btn-outline-primary ms-1">Cập nhật</button> --%>
                                            </form>
                                            <%-- ============================ --%>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${item.totalItemPrice}" type="currency" currencySymbol="đ"/>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/remove-cart?pid=${item.product.product_id}" class="btn btn-sm btn-outline-danger">Xóa</a>
                                        </td>
                                    </tr>
                                    <c:set var="totalCartPrice" value="${totalCartPrice + item.totalItemPrice}" />
                                </c:forEach>
                            </tbody>
                        </table>

                        <div class="text-end">
                            <h4 class="text-danger">
                                Tổng cộng: 
                                <fmt:formatNumber value="${totalCartPrice}" type="currency" currencySymbol="đ"/>
                            </h4>
                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success btn-lg mt-3">Tiến hành Thanh toán</a>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>