<%-- 
    File: order_success.jsp (Trang báo đặt hàng thành công)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đặt hàng thành công</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="css/style.css" rel="stylesheet">
    </head>
    <body>

        <jsp:include page="header.jsp"></jsp:include>

        <div class="container mt-4" style="min-height: 50vh;">
            <div class="row">
                <div class="col-md-3">
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>
                
                <div class="col-md-9">
                    <div class="alert alert-success text-center p-5">
                        <h2 class="alert-heading">ĐẶT HÀNG THÀNH CÔNG!</h2>
                        <p>Cảm ơn bạn đã mua hàng. Đơn hàng của bạn đang được xử lý (COD).</p>
                        <p>Chúng tôi sẽ liên hệ với bạn qua email hoặc số điện thoại để xác nhận.</p>
                        <hr>
                        <a href="home" class="btn btn-primary">Tiếp tục mua sắm</a>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="footer.jsp"></jsp:include>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>