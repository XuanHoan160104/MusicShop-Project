<%-- 
    File: gioi_thieu.jsp (Trang Giới thiệu)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Giới thiệu - Nhạc cụ HDH</title>
        
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
                    <%-- 
                        2. Nhúng Sidebar 
                        (Nó sẽ hoạt động vì StaticPageServlet đã tải "categoryList")
                    --%>
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>
                
                <div class="col-md-9">
                    <%-- 3. Nội dung trang Giới thiệu --%>
                    <h2>Giới thiệu về MusicShop</h2>
                    <hr>
                    <p>
                        Chào mừng bạn đến với MusicShop! Chúng tôi tự hào là nhà cung cấp hàng đầu 
                        về các loại nhạc cụ chất lượng, từ đàn guitar, piano, organ cho đến 
                        các nhạc cụ dân tộc truyền thống.
                    </p>
                    <p>
                        Sứ mệnh của chúng tôi là mang âm nhạc đến gần hơn với mọi người, 
                        cung cấp những sản phẩm tốt nhất với giá cả hợp lý và dịch vụ 
                        chăm sóc khách hàng tận tâm.
                    </p>
                    <img src="${pageContext.request.contextPath}/images/banner2.jpg" class="img-fluid rounded" alt="Cửa hàng">
                </div>
            </div>
        </div>

        <%-- 4. Nhúng Footer --%>
        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>