<%-- 
    File: sidebar.jsp
    Nhiệm vụ: Hiển thị động danh sách danh mục.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="list-group">
    <h5 class="list-group-item text-white" style="background-color: #004a99; border-color: #004a99;">
        DANH MỤC SẢN PHẨM
    </h5>
    
    <%-- 
        'categoryList' chính là tên mà HomeServlet đã đặt (request.setAttribute)
        Chúng ta dùng vòng lặp forEach để duyệt qua danh sách đó.
    --%>
    <c:forEach items="${requestScope.categoryList}" var="cat">
        <a href="category?cid=${cat.category_id}" class="list-group-item list-group-item-action">
            ${cat.name}
        </a>
    </c:forEach>
</div>