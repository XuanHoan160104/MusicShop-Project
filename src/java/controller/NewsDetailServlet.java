package controller;

import dal.CategoryDAO;
import dal.NewsDAO;
import dal.VoucherDAO;
import model.Category;
import model.News;
import model.Voucher;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller để hiển thị chi tiết tin tức và mã giảm giá
 */
@WebServlet(name = "NewsDetailServlet", urlPatterns = {"/news-detail"})
public class NewsDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // Lấy newsId từ request
        String newsIdStr = request.getParameter("newsId");
        if (newsIdStr == null || newsIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/tin-tuc");
            return;
        }
        
        int newsId;
        try {
            newsId = Integer.parseInt(newsIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/tin-tuc");
            return;
        }
        
        // Lấy danh mục cho sidebar
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categoryList = categoryDAO.getAllCategories();
        request.setAttribute("categoryList", categoryList);
        
        // Lấy thông tin tin tức
        NewsDAO newsDAO = new NewsDAO();
        News news = newsDAO.getNewsById(newsId);
        
        if (news == null || !news.isIs_published()) {
            // Nếu không tìm thấy hoặc chưa được publish, redirect về trang tin tức
            response.sendRedirect(request.getContextPath() + "/tin-tuc");
            return;
        }
        
        // Lấy thông tin mã giảm giá nếu có
        Voucher voucher = null;
        if (news.getVoucher_id() != null) {
            VoucherDAO voucherDAO = new VoucherDAO();
            voucher = voucherDAO.getVoucherById(news.getVoucher_id());
        }
        
        // Đẩy dữ liệu sang View
        request.setAttribute("news", news);
        request.setAttribute("voucher", voucher);
        
        // Chuyển hướng đến trang chi tiết
        request.getRequestDispatcher("news_detail.jsp").forward(request, response);
    }
}

