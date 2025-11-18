package admin.controller;

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
 * Quản lý việc hiển thị danh sách sản phẩm trong Admin
 * (AdminFilter sẽ bảo vệ URL này)
 */
@WebServlet(name = "ManageProductServlet", urlPatterns = {"/admin/manage-products"})
public class ManageProductServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO(); 

        // 1. Kiểm tra xem có tham số tìm kiếm không
        String searchKeyword = request.getParameter("search");
        List<Product> productList;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // Nếu có từ khóa tìm kiếm, tìm kiếm theo tên hoặc giá
            productList = productDAO.searchProducts(searchKeyword.trim());
            request.setAttribute("searchKeyword", searchKeyword.trim());
        } else {
            // Nếu không có, lấy tất cả sản phẩm
            productList = productDAO.getAllProducts();
        }

        // 2. Lấy danh sách TẤT CẢ danh mục
        List<Category> categoryList = categoryDAO.getAllCategories();

        // 3. Đẩy dữ liệu sang View
        request.setAttribute("productList", productList);
        request.setAttribute("categoryList", categoryList);
        
        // === SỬA Ở ĐÂY: Thêm dòng này để báo cho sidebar biết trang nào đang active ===
        request.setAttribute("activePage", "products");
        // =======================================================================

        // 4. Chuyển hướng tới trang View
        request.getRequestDispatcher("/admin/manage_products.jsp").forward(request, response);
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