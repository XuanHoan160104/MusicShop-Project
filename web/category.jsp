<%--
    File: category.jsp
    ĐÃ SỬA: Thêm logic Phân trang (Pagination)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- Tiêu đề động --%>
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
    </head>
    <body>

        <jsp:include page="header.jsp"></jsp:include>

        <div class="container mt-4">
            <div class="row">

                <div class="col-md-3">
                    <jsp:include page="sidebar.jsp"></jsp:include>
                </div>

                <div class="col-md-9">
                    <%-- Tiêu đề động (Tìm kiếm / Danh mục) --%>
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
                            <div class="col-md-4 mb-4">
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
                                        <a href="${pageContext.request.contextPath}/detail?pid=${p.product_id}" class="btn btn-primary btn-sm">Xem chi tiết</a>
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

                    <%-- === THÊM MỚI: KHỐI PHÂN TRANG (PAGINATION) === --%>
                    <c:if test="${empty requestScope.searchKeyword}"> <%-- Chỉ hiển thị phân trang khi xem danh mục, không hiển thị khi tìm kiếm --%>
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                
                                <%-- Nút Về Trang Trước (Previous) --%>
                                <li class="page-item ${requestScope.currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/category?cid=${requestScope.currentCid}&page=${requestScope.currentPage - 1}">Trước</a>
                                </li>
                                
                                <%-- Dùng JSTL <c:forEach> để lặp và tạo các nút số trang --%>
                                <c:forEach begin="1" end="${requestScope.totalPages}" var="i">
                                    <li class="page-item ${requestScope.currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/category?cid=${requestScope.currentCid}&page=${i}">${i}</a>
                                    </li>
                                </c:forEach>
                                
                                <%-- Nút Sang Trang Sau (Next) --%>
                                <li class="page-item ${requestScope.currentPage == requestScope.totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/category?cid=${requestScope.currentCid}&page=${requestScope.currentPage + 1}">Sau</a>
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