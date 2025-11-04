<%-- 
    File: register.jsp (Trang đăng ký)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đăng ký tài khoản</title>
       <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="css/style.css" rel="stylesheet">
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        
        <div class="container" style="min-height: 80vh;">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
                    <h3 class="text-center mt-5">ĐĂNG KÝ TÀI KHOẢN MỚI</h3>
                    
                    <c:if test="${not empty requestScope.error}">
                        <div class="alert alert-danger" role="alert">
                            ${requestScope.error}
                        </div>
                    </c:if>
                        
                    <form id="registerForm" action="register" method="post" class="mt-4" onsubmit="return validateForm()">
                        <div class="mb-3">
                            <label for="username" class="form-label">Tên đăng nhập (*):</label>
                            <input type="text" class="form-control" id="username" name="username" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Mật khẩu (*):</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>
                        <div class="mb-3">
                            <label for="re_password" class="form-label">Nhập lại mật khẩu (*):</label>
                            <input type="password" class="form-control" id="re_password" name="re_password" required>
                        </div>
                        <div class="mb-3">
                            <label for="fullname" class="form-label">Họ và tên:</label>
                            <input type="text" class="form-control" id="fullname" name="fullname">
                        </div>
                         <div class="mb-3">
                            <label for="email" class="form-label">Email:</label>
                            <input type="email" class="form-control" id="email" name="email">
                        </div>
                        <div class="mb-3">
                            <label for="address" class="form-label">Địa chỉ:</label>
                            <input type="text" class="form-control" id="address" name="address">
                        </div>
                        
                        <button type="submit" class="btn btn-primary w-100">Đăng ký</button>
                    </form>
                    <div class="text-center mt-3">
                        <p>Đã có tài khoản? <a href="login.jsp">Đăng nhập ngay</a></p>
                    </div>
                </div>
            </div>
        </div>
        
        <jsp:include page="footer.jsp"></jsp:include>
        
        <script>
            function validateForm() {
                var pass = document.getElementById("password").value;
                var re_pass = document.getElementById("re_password").value;
                
                if (pass !== re_pass) {
                    alert("Mật khẩu nhập lại không khớp. Vui lòng kiểm tra lại!");
                    return false; // Ngăn không cho form được gửi đi
                }
                
                return true; // Cho phép gửi form
            }
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>