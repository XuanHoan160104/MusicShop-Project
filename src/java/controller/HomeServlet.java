package controller;

import dal.CategoryDAO;
import dal.ProductDAO; // (Thêm import)
import model.Category;
import model.Product; // (Thêm import)
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Đây là Controller (C)
 * Nó được map với URL "/" (trang chủ)
 * Nhiệm vụ: Lấy danh sách danh mục (cho sidebar) VÀ sản phẩm nổi bật
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // --- Logic của Controller ---
        
        // 1. Lấy danh sách danh mục (cho sidebar)
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> listCategories = categoryDAO.getAllCategories();
        
        // === ĐÃ CẬP NHẬT TẠI ĐÂY ===
        // 2. Lấy danh sách sản phẩm nổi bật
        ProductDAO productDAO = new ProductDAO();
        List<Product> featuredList = productDAO.getFeaturedProducts();
        // ==========================

        // 3. Đẩy 2 danh sách (Model) sang (View)
        request.setAttribute("categoryList", listCategories); // Dữ liệu cho sidebar
        request.setAttribute("featuredList", featuredList);   // Dữ liệu MỚI cho trang chủ

        // 4. Chuyển hướng (forward) tới trang (View)
        request.getRequestDispatcher("index.jsp").forward(request, response);
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