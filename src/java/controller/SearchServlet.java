package controller;

import dal.CategoryDAO;
import dal.ProductDAO;
import model.Category;
import model.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (C)
 * Xử lý chức năng tìm kiếm sản phẩm
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // Để nhận tiếng Việt

        // 1. Lấy từ khóa tìm kiếm
        String keyword = request.getParameter("searchQuery");
        if (keyword == null) {
            keyword = ""; // Tránh lỗi NullPointerException
        }

        // 2. Gọi DAO để tìm sản phẩm
        ProductDAO productDAO = new ProductDAO();
        // **Quan trọng:** Đảm bảo bạn có hàm `searchByName(String keyword)` trong ProductDAO.java
        List<Product> searchResult = productDAO.searchByName(keyword.trim()); // Xóa khoảng trắng thừa

        // 3. Gọi DAO để lấy danh mục (cho sidebar)
        CategoryDAO categoryDAO = new CategoryDAO();
        // **Quan trọng:** Đảm bảo bạn có hàm `getAllCategories()` trong CategoryDAO.java
        List<Category> categoryList = categoryDAO.getAllCategories();

        // 4. Đẩy dữ liệu sang View
        request.setAttribute("productList", searchResult);   // Dùng tên "productList" để tái sử dụng category.jsp
        request.setAttribute("categoryList", categoryList); // Cho sidebar
        request.setAttribute("searchKeyword", keyword);      // Gửi lại từ khóa

        // 5. Chuyển hướng tới View
        request.getRequestDispatcher("category.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}