<%-- 
    File: tin_tuc.jsp (Trang Tin tức / Blog)
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
                    
                    <c:choose>
                        <c:when test="${empty requestScope.newsList or requestScope.newsList.size() == 0}">
                            <div class="alert alert-info">
                                Hiện tại chưa có tin tức nào.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${requestScope.newsList}" var="news">
                                <div class="card mb-3">
                                    <div class="row g-0">
                                        <c:if test="${not empty news.image_url}">
                                            <div class="col-md-4">
                                                <img src="${pageContext.request.contextPath}/${news.image_url}" class="img-fluid rounded-start" alt="${news.title}">
                                            </div>
                                        </c:if>
                                        <div class="col-md-${not empty news.image_url ? '8' : '12'}">
                                            <div class="card-body">
                                                <h5 class="card-title">
                                                    <a href="${pageContext.request.contextPath}/news-detail?newsId=${news.news_id}" 
                                                       class="text-decoration-none text-dark">
                                                        ${news.title}
                                                    </a>
                                                </h5>
                                                <p class="card-text">
                                                    <c:choose>
                                                        <c:when test="${fn:length(news.content) > 150}">
                                                            ${fn:substring(news.content, 0, 150)}...
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${news.content}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>
                                                <p class="card-text">
                                                    <small class="text-muted">
                                                        Đăng ngày <fmt:formatDate value="${news.created_at}" pattern="dd-MM-yyyy" />
                                                    </small>
                                                </p>
                                                <div class="d-flex justify-content-between align-items-center">
                                                    <c:if test="${not empty news.voucher_id}">
                                                        <span class="badge bg-success">
                                                            <i class="fas fa-tag me-1"></i>Có mã giảm giá kèm theo
                                                        </span>
                                                    </c:if>
                                                    <a href="${pageContext.request.contextPath}/news-detail?newsId=${news.news_id}" 
                                                       class="btn btn-sm btn-outline-primary">
                                                        Xem chi tiết <i class="fas fa-arrow-right ms-1"></i>
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    
                </div>
            </div>
        </div>

        <%-- 4. Nhúng Footer --%>
        <jsp:include page="footer.jsp"></jsp:include>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>