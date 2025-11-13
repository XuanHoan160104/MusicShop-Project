<%-- 
    File: chinh_sach_bao_hanh.jsp (Trang Chính sách bảo hành)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chính sách bảo hành - Nhạc cụ HDH</title>
        
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
                    <%-- 3. Nội dung trang Chính sách bảo hành --%>
                    <h2>Chính sách bảo hành</h2>
                    <hr>
                    
                    <h4>1. ĐIỀU KIỆN BẢO HÀNH</h4>
                    <p>Sản phẩm được bảo hành miễn phí nếu đảm bảo tất cả các điều kiện sau:</p>
                    <ul>
                        <li>Sản phẩm còn trong thời hạn bảo hành (được tính kể từ ngày mua hàng).</li>
                        <li>Sản phẩm bị lỗi kỹ thuật do lỗi của nhà sản xuất.</li>
                        <li>Phiếu bảo hành (nếu có) còn nguyên vẹn, không chắp vá, tẩy xóa.</li>
                        <li>Tem bảo hành (nếu có) còn nguyên vẹn.</li>
                    </ul>

                    <h4>2. NHỮNG TRƯỜNG HỢP KHÔNG ĐƯỢC BẢO HÀNH</h4>
                    <p>Chúng tôi không bảo hành trong các trường hợp sau:</p>
                    <ul>
                        <li>Sản phẩm hết thời hạn bảo hành.</li>
                        <li>Hư hỏng do thiên tai, tai nạn (rơi vỡ, ẩm ướt, nước vào...).</li>
                        <li>Hư hỏng do sử dụng sai cách, sai điện áp quy định.</li>
                        <li>Sản phẩm bị can thiệp, sửa chữa bởi các đơn vị bên ngoài mà không được sự cho phép của MusicShop.</li>
                    </ul>
                    
                </div>
            </div>
        </div>

        <%-- 4. Nhúng Footer --%>
        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>