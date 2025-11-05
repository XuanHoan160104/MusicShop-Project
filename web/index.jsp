<%--
    File: index.jsp (Trang khung chính)
    ĐÃ SỬA: Thêm meta viewport và contextPath
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- === THÊM MỚI: Thẻ Meta Viewport (Bắt buộc) === --%>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        
        <title>Nhạc cụ ba miền HDH</title>
        
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <%-- Thêm Font Awesome (nếu header chưa có) --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    </head>
    <body>

        <jsp:include page="header.jsp"></jsp:include>

        <div class="container mt-4">
            <div class="row">
                
                <div class="col-md-3">
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>
                
                <div class="col-md-9">
                    
                    <%-- Slider (Carousel) --%>
                    <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
                        <div class="carousel-indicators">
                            <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active"></button>
                            <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
                        </div>
                        <div class="carousel-inner">
                            <div class="carousel-item active">
                                <%-- Sửa: Thêm contextPath --%>
                                <img src="${pageContext.request.contextPath}/images/banner1.jpg" class="d-block w-100" alt="Banner 1">
                            </div>
                            <div class="carousel-item">
                                <%-- Sửa: Thêm contextPath --%>
                                <img src="${pageContext.request.contextPath}/images/banner2.jpg" class="d-block w-100" alt="Banner 2"> 
                            </div>
                        </div>
                        <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev">
                            <span class="carousel-control-prev-icon"></span>
                        </button>
                        <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next">
                            <span class="carousel-control-next-icon"></span>
                        </button>
                    </div>
                    
                    <h3 class="mt-4">Sản phẩm nổi bật</h3>
                    
                    <div class="row">
                        <c:forEach items="${requestScope.featuredList}" var="p">
                            <%-- Thêm class 'product-list-col' để làm responsive (hiển thị 2 cột trên ĐT) --%>
                            <div class="col-md-4 col-lg-3 mb-4 product-list-col"> 
                                <div class="card h-100">
                                    
                                    <%-- Sửa: Thêm contextPath --%>
                                    <img src="${pageContext.request.contextPath}/${p.image_url}" class="card-img-top" alt="${p.name}">
                                    
                                    <div class="card-body d-flex flex-column">
                                        <h5 class="card-title" style="font-size: 1rem;">
                                            <%-- Sửa: Thêm contextPath --%>
                                            <a href="${pageContext.request.contextPath}/detail?pid=${p.product_id}">${p.name}</a>
                                        </h5>
                                        <p class="card-text text-danger fw-bold mt-auto">
                                            <fmt:setLocale value = "vi_VN"/>
                                            <fmt:formatNumber value = "${p.price}" type = "currency" currencySymbol="đ"/>
                                        </p>
                                        
                                        <%-- Sửa: Thêm logic kiểm tra tồn kho --%>
                                        <c:choose>
                                            <c:when test="${p.stock_quantity > 0}">
                                                <a href="${pageContext.request.contextPath}/detail?pid=${p.product_id}" class="btn btn-primary btn-sm">Xem chi tiết</a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="btn btn-sm btn-outline-danger disabled">Đã hết hàng</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    
                </div>
                
            </div>
        </div>

        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>