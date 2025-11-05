<%-- 
    File: detail.jsp (Trang chi tiết sản phẩm)
    ĐÃ SỬA: Thêm logic kiểm tra tồn kho.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${requestScope.productDetail.name}</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
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
                    <c:set var="p" value="${requestScope.productDetail}" />
                    
                    <div class="row">
                        <div class="col-md-5">
                            <%-- Sửa lại đường dẫn ảnh để luôn chính xác --%>
                            <img src="${pageContext.request.contextPath}/${p.image_url}" class="img-fluid" alt="${p.name}">
                        </div>
                        
                        <div class="col-md-7">
                            <h2>${p.name}</h2>
                            <h3 class="text-danger fw-bold mt-3">
                                <fmt:setLocale value="vi_VN"/>
                                <fmt:formatNumber value="${p.price}" type="currency" currencySymbol="đ"/>
                            </h3>
                            
                            <hr>
                            
                            <%-- === SỬA Ở ĐÂY: Kiểm tra tồn kho trước khi hiển thị nút === --%>
                            <c:choose>
                                <%-- Trường hợp 1: Hết hàng --%>
                                <c:when test="${p.stock_quantity <= 0}">
                                    <div class="alert alert-danger" role="alert">
                                        Sản phẩm này đã hết hàng
                                    </div>
                                    <button type="button" class="btn btn-danger btn-lg" disabled>Thêm vào giỏ hàng</button>
                                </c:when>
                                <%-- Trường hợp 2: Còn hàng --%>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/add-to-cart" method="post">
                                        <div class="mb-3">
                                            <label for="quantity" class="form-label">Số lượng:</label>
                                            <input type="number" name="quantity" class="form-control" value="1" min="1" max="${p.stock_quantity}" style="width: 100px;">
                                            <small class="form-text text-muted">(${p.stock_quantity} sản phẩm có sẵn)</small>
                                        </div>
                                        <input type="hidden" name="productID" value="${p.product_id}">
                                        <button type="submit" class="btn btn-danger btn-lg">Thêm vào giỏ hàng</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                            <%-- ========================================================== --%>
                            
                        </div>
                    </div>
                    
                    <div class="mt-5">
                        <ul class="nav nav-tabs" id="myTab" role="tablist">
                          <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="description-tab" data-bs-toggle="tab" data-bs-target="#description" type="button" role="tab">Mô tả sản phẩm</button>
                          </li>
                        </ul>
                        <div class="tab-content p-3 border border-top-0" id="myTabContent">
                          <div class="tab-pane fade show active" id="description" role="tabpanel">
                              ${p.description}
                          </div>
                        </div>
                    </div>
                    
                </div>
            </div>
        </div>

        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>