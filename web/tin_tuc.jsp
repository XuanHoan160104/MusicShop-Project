<%-- 
    File: tin_tuc.jsp (Trang Tin tức / Blog)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tin tức - Nhạc cụ HDH</title>
        
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
                    <%-- 3. Nội dung trang Tin tức --%>
                    <h2>Tin tức & Khuyến mãi</h2>
                    <hr>
                    
                    <%-- Mẫu Bài đăng 1 --%>
                    <div class="card mb-3">
                        <div class="row g-0">
                            <div class="col-md-4">
                                <img src="${pageContext.request.contextPath}/images/guitar1.jpg" class="img-fluid rounded-start" alt="Tin tức 1">
                            </div>
                            <div class="col-md-8">
                                <div class="card-body">
                                    <h5 class="card-title">Khuyến mãi cuối năm: Giảm giá 50% cho Guitar Acoustic</h5>
                                    <p class="card-text">Chào đón mùa lễ hội, MusicShop tưng bừng giảm giá sốc cho tất cả các dòng đàn Guitar Acoustic Yamaha. Đây là cơ hội tuyệt vời...</p>
                                    <p class="card-text"><small class="text-muted">Đăng ngày 01-11-2025</small></p>
                                    <a href="#" class="btn btn-primary btn-sm">Đọc tiếp</a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%-- Mẫu Bài đăng 2 --%>
                    <div class="card mb-3">
                        <div class="row g-0">
                            <div class="col-md-4">
                                <img src="${pageContext.request.contextPath}/images/piano1.jpg" class="img-fluid rounded-start" alt="Tin tức 2">
                            </div>
                            <div class="col-md-8">
                                <div class="card-body">
                                    <h5 class="card-title">5 mẹo tự học Piano hiệu quả tại nhà cho người mới bắt đầu</h5>
                                    <p class="card-text">Bạn đam mê Piano nhưng không có thời gian đến lớp? Đừng lo, 5 mẹo nhỏ sau đây sẽ giúp bạn xây dựng nền tảng vững chắc...</p>
                                    <p class="card-text"><small class="text-muted">Đăng ngày 28-10-2025</small></p>
                                    <a href="#" class="btn btn-primary btn-sm">Đọc tiếp</a>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <%-- (Sau này bạn có thể tạo CSDL Bảng 'Articles' và CRUD cho Admin) --%>
                    
                </div>
            </div>
        </div>

        <%-- 4. Nhúng Footer --%>
        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>