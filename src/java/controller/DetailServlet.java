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
 * Đây là Controller (C)
 * Xử lý việc hiển thị CHI TIẾT một sản phẩm
 */
@WebServlet(name = "DetailServlet", urlPatterns = {"/detail"})
public class DetailServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // 1. Lấy 'pid' (product id) từ URL
        String productID = request.getParameter("pid");
        
        // 2. (C) gọi (DAO) để lấy 1 sản phẩm (Model)
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductByID(productID);
        
        // 3. (C) gọi (DAO) để lấy danh sách danh mục (cho sidebar)
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categoryList = categoryDAO.getAllCategories();
        
        // 4. (C) đẩy dữ liệu (Model) sang (View)
        request.setAttribute("productDetail", product); // Gửi 1 sản phẩm
        request.setAttribute("categoryList", categoryList); // Gửi danh sách cho sidebar
        
        // 5. (C) chuyển hướng (forward) tới trang (View)
        request.getRequestDispatcher("detail.jsp").forward(request, response);
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