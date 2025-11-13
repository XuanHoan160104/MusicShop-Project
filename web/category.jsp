<%--
    File: category.jsp
    ĐÃ SỬA: Sửa lại logic Phân trang (hiển thị cho cả Danh mục VÀ Tìm kiếm)
    ĐÃ SỬA: Thêm logic kiểm tra tồn kho ("Đã hết hàng")
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <c:choose>
            <c:when test="${not empty requestScope.searchKeyword}">
                <title>Kết quả tìm kiếm cho: ${requestScope.searchKeyword}</title>
            </c:when>
            <c:otherwise>
                <title>Sản phẩm - Nhạc cụ HDH</title>
            </c:otherwise>
        </c:choose>
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
                    <c:choose>
                        <c:when test="${not empty requestScope.searchKeyword}">
                            <h3>Kết quả tìm kiếm cho: "${requestScope.searchKeyword}"</h3>
                        </c:when>
                        <c:otherwise>
                             <h3>DANH SÁCH SẢN PHẨM</h3>
                        </c:otherwise>
                    </c:choose>

                    <div class="row mt-3">
                        <c:forEach items="${requestScope.productList}" var="p">
                            <%-- Thêm class 'product-list-col' để làm responsive --%>
                            <div class="col-md-4 mb-4 product-list-col">
                                <div class="card h-100">
                                    <img src="${pageContext.request.contextPath}/${p.image_url}" class="card-img-top" alt="${p.name}">
                                    
                                    <div class="card-body d-flex flex-column">
                                        <h5 class="card-title" style="font-size: 1rem;">
                                            <a href="${pageContext.request.contextPath}/detail?pid=${p.product_id}">${p.name}</a>
                                        </h5>
                                        <p class="card-text text-danger fw-bold mt-auto">
                                            <fmt:setLocale value = "vi_VN"/>
                                            <fmt:formatNumber value = "${p.price}" type = "currency" currencySymbol="đ"/>
                                        </p>
                                        
                                        <%-- Logic kiểm tra tồn kho --%>
                                        <c:choose>
                                            <%-- 
                                                Đảm bảo ProductDAO (các hàm ...Paged) 
                                                đã lấy về p.stock_quantity
                                            --%>
                                            <c:when test="${p.stock_quantity > 0}">
                                                <a href="${pageContext.request.contextPath}/detail?pid=${p.product_id}" class="btn btn-primary btn-sm">Xem chi tiết</a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="btn btn-sm btn-outline-danger disabled">Đã hết hàng</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${empty requestScope.productList}">
                             <c:choose>
                                <c:when test="${not empty requestScope.searchKeyword}">
                                    <p>Không tìm thấy sản phẩm nào phù hợp với từ khóa "${requestScope.searchKeyword}".</p>
                                </c:when>
                                <c:otherwise>
                                     <p>Không tìm thấy sản phẩm nào trong danh mục này.</p>
                                </c:otherwise>
                             </c:choose>
                        </c:if>
                    </div>

                    <%-- === SỬA LẠI KHỐI PHÂN TRANG (PAGINATION) === --%>
                    <%-- Chỉ hiển thị nếu có nhiều hơn 1 trang --%>
                    <c:if test="${requestScope.totalPages > 1}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                
                                <%-- Nút Về Trang Trước (Previous) --%>
                                <li class="page-item ${requestScope.currentPage == 1 ? 'disabled' : ''}">
                                    <%-- Dùng c:choose để tạo link đúng --%>
                                    <c:choose>
                                        <%-- Link cho Tìm kiếm --%>
                                        <c:when test="${not empty requestScope.searchKeyword}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/search?searchQuery=${requestScope.searchKeyword}&page=${requestScope.currentPage - 1}">Trước</a>
                                        </c:when>
                                        <%-- Link cho Danh mục --%>
                                        <c:otherwise>
                                            <a class="page-link" href="${pageContext.request.contextPath}/category?cid=${requestScope.currentCid}&page=${requestScope.currentPage - 1}">Trước</a>
                                        </c:otherwise>
                                    </c:choose>
                                </li>
                                
                                <%-- Các nút số trang --%>
                                <c:forEach begin="1" end="${requestScope.totalPages}" var="i">
                                    <li class="page-item ${requestScope.currentPage == i ? 'active' : ''}">
                                        <c:choose>
                                            <%-- Link cho Tìm kiếm --%>
                                            <c:when test="${not empty requestScope.searchKeyword}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/search?searchQuery=${requestScope.searchKeyword}&page=${i}">${i}</a>
                                            </c:when>
                                            <%-- Link cho Danh mục --%>
                                            <c:otherwise>
                                                <a class="page-link" href="${pageContext.request.contextPath}/category?cid=${requestScope.currentCid}&page=${i}">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:forEach>
                                
                                <%-- Nút Sang Trang Sau (Next) --%>
                                <li class="page-item ${requestScope.currentPage == requestScope.totalPages ? 'disabled' : ''}">
                                    <c:choose>
                                        <%-- Link cho Tìm kiếm --%>
                                        <c:when test="${not empty requestScope.searchKeyword}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/search?searchQuery=${requestScope.searchKeyword}&page=${requestScope.currentPage + 1}">Sau</a>
                                        </c:when>
                                        <%-- Link cho Danh mục --%>
                                        <c:otherwise>
                                            <a class="page-link" href="${pageContext.request.contextPath}/category?cid=${requestScope.currentCid}&page=${requestScope.currentPage + 1}">Sau</a>
                                        </c:otherwise>
                                    </c:choose>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                    <%-- ========================================== --%>

                </div>
            </div>
        </div>

        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>