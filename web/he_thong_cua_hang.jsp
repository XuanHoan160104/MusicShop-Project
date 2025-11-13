<%-- 
    File: he_thong_cua_hang.jsp (Trang Hệ thống cửa hàng)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hệ thống cửa hàng - Nhạc cụ HDH</title>
        
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    </head>
    <body>

        <%-- 1. Nhúng Header --%>
        <jsp:include page="header.jsp"></jsp:include>

        <div class="container mt-4">
            <div class="row">
                <div class="col-md-3">
                    <%-- 2. Nhúng Sidebar (Sẽ hoạt động vì Servlet đã tải categoryList) --%>
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>
                
                <div class="col-md-9">
                    <%-- 3. Nội dung trang Hệ thống cửa hàng --%>
                    <h2>Hệ thống Cửa hàng MusicShop</h2>
                    <hr>
                    
                    <div class="row">
                        <div class="col-md-6 mb-4">
                            <div class="card h-100">
                                <div class="card-body">
                                    <h5 class="card-title">MusicShop Vĩnh Phúc (Trụ sở chính)</h5>
                                    <p class="card-text">
                                        <i class="fas fa-map-marker-alt me-2"></i> <strong>Địa chỉ:</strong> 123 Đường A, Phường B, Quận C, Tỉnh Vĩnh Phúc
                                        <br>
                                        <i class="fas fa-phone me-2"></i> <strong>Hotline:</strong> 0355.962.008
                                        <br>
                                        <i class="fas fa-envelope me-2"></i> <strong>Email:</strong> contact@musicshop.com
                                    </p>
                                </div>
                                <%-- Nhúng bản đồ Google Maps --%>
                                <div class="card-footer p-0">
                                    <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d118944.9723220403!2d105.54904907705191!3d21.32185521990433!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3134e1a0b77c5147%3A0x6a160d1efb05f63d!2zVsSpbmggUGjDumMsIFZp4buHdCBOYW0!5e0!3m2!1svi!2s!4v1670000000000!5m2!1svi!2s" 
                                            width="100%" height="300" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-6 mb-4">
                            <div class="card h-100">
                                <div class="card-body">
                                    <h5 class="card-title">MusicShop Hà Nội (Chi nhánh)</h5>
                                    <p class="card-text">
                                        <i class="fas fa-map-marker-alt me-2"></i> <strong>Địa chỉ:</strong> 456 Đường D, Quận E, TP. Hà Nội
                                        <br>
                                        <i class="fas fa-phone me-2"></i> <strong>Hotline:</strong> 0123.456.789
                                        <br>
                                        <i class="fas fa-envelope me-2"></i> <strong>Email:</strong> hanoi@musicshop.com
                                    </p>
                                </div>
                                <div class="card-footer p-0">
                                    <%-- (Thay link Google Maps nhúng cho chi nhánh Hà Nội ở đây) --%>
                                    <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d238350.2198751515!2d105.6189552199859!3d21.0227391963155!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ab9bd9861ca1%3A0xe7887f7b72ca1f7!2zSMOgIE7hu5lpLCBWaeG7h3QgTmFt!5e0!3m2!1svi!2s!4v1670000000001!5m2!1svi!2s" 
                                            width="100%" height="300" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                </div>
            </div>
        </div>

        <%-- 4. Nhúng Footer --%>
        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>