<%-- 
    File: lien_he.jsp (Trang Liên hệ)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Liên hệ - Nhạc cụ HDH</title>
        
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
                    <%-- 2. Nhúng Sidebar (Sẽ hoạt động) --%>
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>
                
                <div class="col-md-9">
                    <%-- 3. Nội dung trang Liên hệ --%>
                    <h2>Liên hệ với chúng tôi</h2>
                    <hr>
                    
                    <div class="row">
                        <%-- Cột (A): Form liên hệ --%>
                        <div class="col-md-7 mb-4">
                            <h5>Gửi tin nhắn cho chúng tôi</h5>
                            <p>Nếu bạn có bất kỳ thắc mắc nào, đừng ngần ngại gửi biểu mẫu bên dưới.</p>
                            
                            <%-- Form này hiện CHƯA hoạt động. Cần 1 Servlet (ví dụ: /contact) để xử lý --%>
                            <form action="#" method="post">
                                <div class="mb-3">
                                    <label for="contactName" class="form-label">Họ và tên của bạn (*):</label>
                                    <input type="text" class="form-control" id="contactName" name="contactName" required>
                                </div>
                                <div class="mb-3">
                                    <label for="contactEmail" class="form-label">Email (*):</label>
                                    <input type="email" class="form-control" id="contactEmail" name="contactEmail" required>
                                </div>
                                <div class="mb-3">
                                    <label for="contactMessage" class="form-label">Nội dung tin nhắn (*):</label>
                                    <textarea class="form-control" id="contactMessage" name="contactMessage" rows="5" required></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary" disabled>Gửi tin nhắn (Chưa hoạt động)</button>
                            </form>
                        </div>
                        
                        <%-- Cột (B): Thông tin --%>
                        <div class="col-md-5 mb-4">
                            <h5>Thông tin cửa hàng</h5>
                            <p>
                                <i class="fas fa-map-marker-alt me-2"></i> <strong>Địa chỉ:</strong> 123 Đường A, Phường B, Quận C, Tỉnh Vĩnh Phúc
                                <br>
                                <i class="fas fa-phone me-2"></i> <strong>Hotline:</strong> 0355.962.008
                                <br>
                                <i class="fas fa-envelope me-2"></i> <strong>Email:</strong> contact@musicshop.com
                                <br>
                                <i class="fas fa-clock me-2"></i> <strong>Giờ mở cửa:</strong> 8:00 - 21:00 (Tất cả các ngày)
                            </p>
                            
                            <%-- Bản đồ Google Maps --%>
                            <h5 class="mt-4">Bản đồ</h5>
                            <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d118944.9723220403!2d105.54904907705191!3d21.32185521990433!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3134e1a0b77c5147%3A0x6a160d1efb05f63d!2zVsSpbmggUGjDumMsIFZp4buHdCBOYW0!5e0!3m2!1svi!2s!4v1670000000000!5m2!1svi!2s" 
                                    width="100%" height="300" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
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