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

@WebServlet(name = "CategoryServlet", urlPatterns = {"/category"})
public class CategoryServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // --- Bổ sung Logic Phân trang ---
        
        // 1. Lấy 'cid' (category id) từ URL
        String categoryId = request.getParameter("cid");

        // 2. Lấy số trang (page) từ URL, nếu không có thì mặc định là 1
        String page_raw = request.getParameter("page");
        int pageNumber;
        try {
            if (page_raw == null) {
                pageNumber = 1;
            } else {
                pageNumber = Integer.parseInt(page_raw);
            }
        } catch (NumberFormatException e) {
            pageNumber = 1; // Nếu nhập bậy thì về trang 1
        }
        
        int pageSize = 9; // Đặt cố định 9 sản phẩm/trang

        // 3. (C) gọi (DAO) để đếm tổng số sản phẩm
        ProductDAO productDAO = new ProductDAO();
        int totalProducts = productDAO.countProductsByCategoryID(categoryId);
        
        // 4. Tính tổng số trang (totalPages)
        // Ví dụ: 20 SP / 9 = 2.22 -> làm tròn lên 3 trang
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        // 5. (C) gọi (DAO) để lấy sản phẩm của trang hiện tại
        List<Product> productList = productDAO.getProductsByCategoryIDPaged(categoryId, pageNumber, pageSize);

        // 6. (C) gọi (DAO) để lấy danh sách danh mục (cho sidebar)
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categoryList = categoryDAO.getAllCategories();

        // 7. (C) đẩy dữ liệu (Model) sang (View)
        request.setAttribute("productList", productList);
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("totalPages", totalPages);      // Gửi tổng số trang
        request.setAttribute("currentPage", pageNumber);    // Gửi trang hiện tại
        request.setAttribute("currentCid", categoryId);       // Gửi category id (để nút phân trang hoạt động)

        // 8. (C) chuyển hướng (forward) tới trang (View)
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