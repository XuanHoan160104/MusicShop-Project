package controller;

import dal.CategoryDAO;
import dal.NewsDAO;
import model.Category;
import model.News;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (C)
 * Xử lý việc hiển thị các trang tĩnh (Giới thiệu, Liên hệ...)
 * Luôn tải CategoryList cho sidebar.
 */
// === SỬA Ở ĐÂY: Thêm "/he-thong-cua-hang" vào danh sách ===
@WebServlet(name = "StaticPageServlet", urlPatterns = {
    "/gioi-thieu",
    "/he-thong-cua-hang", // <-- THÊM DÒNG NÀY
    "/chinh-sach-bao-hanh",
    "/tin-tuc",
    "/lien-he"
})
public class StaticPageServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // 1. Luôn lấy danh mục cho sidebar
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categoryList = categoryDAO.getAllCategories();
        request.setAttribute("categoryList", categoryList);

        // 2. Lấy đường dẫn URL
        String path = request.getServletPath();
        String destinationPage = "";

        // 3. Quyết định forward đến file JSP nào
        switch (path) {
            case "/gioi-thieu":
                destinationPage = "gioi_thieu.jsp";
                break;
            // === SỬA Ở ĐÂY: Thêm case mới ===
            case "/he-thong-cua-hang":
                destinationPage = "he_thong_cua_hang.jsp"; // <-- THÊM CASE NÀY
                break;
            // ============================
            case "/chinh-sach-bao-hanh":
                destinationPage = "chinh_sach_bao_hanh.jsp";
                break;
            case "/tin-tuc":
                // Load tin tức đã published cho trang tin tức
                NewsDAO newsDAO = new NewsDAO();
                List<News> newsList = newsDAO.getPublishedNews();
                request.setAttribute("newsList", newsList);
                destinationPage = "tin_tuc.jsp";
                break;
            case "/lien-he":
                destinationPage = "lien_he.jsp";
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/home");
                return;
        }

        // 4. Chuyển hướng đến trang JSP tương ứng
        request.getRequestDispatcher(destinationPage).forward(request, response);
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