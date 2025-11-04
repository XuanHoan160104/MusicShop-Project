<%-- 
    File: index.jsp (Trang khung chính)
    Nhiệm vụ: Hiển thị Slider, Sidebar và Sản phẩm nổi bật.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%-- Thêm 2 thư viện JSTL (core để lặp, fmt để format tiền) --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Nhạc cụ ba miền HDH  </title>
        
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
    </head>
    <body>

        <jsp:include page="header.jsp"></jsp:include>

        <div class="container mt-4">
            <div class="row">
                
                <div class="col-md-3">
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>
                
                <div class="col-md-9">
                    
                    <%-- === ĐÃ SỬA: THÊM SLIDER (CAROUSEL) === --%>
                    <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
                        <div class="carousel-indicators">
                            <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active"></button>
                            <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
                        </div>
                        <div class="carousel-inner">
                            <div class="carousel-item active">
                                <img src="images/banner1.jpg" class="d-block w-100" alt="Banner 1">
                            </div>
                            <div class="carousel-item">
                                <img src="images/banner2.jpg" class="d-block w-100" alt="Banner 2"> 
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
                    
                    <%-- === ĐÃ SỬA: HIỂN THỊ SẢN PHẨM NỔI BẬT === --%>
                    <div class="row">
                        <%-- 
                            Dùng JSTL <c:forEach> để lặp qua 'featuredList'
                            mà HomeServlet đã gửi qua.
                        --%>
                        <c:forEach items="${requestScope.featuredList}" var="p">
                            <div class="col-md-4 col-lg-3 mb-4"> <%-- Hiển thị 4 sản phẩm/hàng --%>
                                <div class="card h-100">
                                    
                                    <img src="${p.image_url}" class="card-img-top" alt="${p.name}">
                                    
                                    <div class="card-body d-flex flex-column">
                                        <h5 class="card-title" style="font-size: 1rem;">
                                            <a href="detail?pid=${p.product_id}">${p.name}</a>
                                        </h5>
                                        <p class="card-text text-danger fw-bold mt-auto">
                                            <fmt:setLocale value = "vi_VN"/>
                                            <fmt:formatNumber value = "${p.price}" type = "currency" currencySymbol="đ"/>
                                        </p>
                                        <a href="detail?pid=${p.product_id}" class="btn btn-primary btn-sm">Xem chi tiết</a>
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