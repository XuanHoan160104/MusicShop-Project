<%--
    File: change_password.jsp (Trang đổi mật khẩu)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Tự động chuyển hướng nếu chưa đăng nhập --%>
<c:if test="${empty sessionScope.account}">
    <% response.sendRedirect(request.getContextPath() + "/login.jsp"); %>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Đổi Mật khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>

    <jsp:include page="header.jsp"></jsp:include>

    <div class="container mt-4" style="min-height: 60vh;">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-4">
                <h3 class="text-center mt-5">ĐỔI MẬT KHẨU</h3>

                <%-- Hiển thị thông báo lỗi (nếu có từ ChangePasswordServlet) --%>
                <c:if test="${not empty requestScope.passwordError}">
                    <div class="alert alert-danger mt-3" role="alert">
                        ${requestScope.passwordError}
                    </div>
                </c:if>

                <%-- Form đổi mật khẩu, trỏ đến ChangePasswordServlet --%>
                <form id="changePasswordForm" action="${pageContext.request.contextPath}/change-password" method="post" class="mt-4" onsubmit="return validatePassword()">
                    <div class="mb-3">
                        <label for="oldPassword" class="form-label">Mật khẩu cũ (*):</label>
                        <input type="password" class="form-control" id="oldPassword" name="oldPassword" required>
                    </div>
                    <div class="mb-3">
                        <label for="newPassword" class="form-label">Mật khẩu mới (*):</label>
                        <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                    </div>
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Nhập lại mật khẩu mới (*):</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Xác nhận đổi mật khẩu</button>
                     <a href="${pageContext.request.contextPath}/profile" class="btn btn-secondary w-100 mt-2">Hủy</a>
                </form>

            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp"></jsp:include>

    <%-- JavaScript đơn giản để kiểm tra mật khẩu mới khớp nhau --%>
    <script>
        function validatePassword() {
            var newPass = document.getElementById("newPassword").value;
            var confirmPass = document.getElementById("confirmPassword").value;

            if (newPass !== confirmPass) {
                alert("Mật khẩu mới nhập lại không khớp. Vui lòng kiểm tra lại!");
                return false; // Ngăn không cho form được gửi đi
            }
            return true; // Cho phép gửi form
        }
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>