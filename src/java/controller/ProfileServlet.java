package controller;

import dal.CategoryDAO; 
import dal.OrderDAO;    
import model.Category;
// import model.Order; // Không cần import Order nữa
import model.OrderHistoryItem; // THAY ĐỔI: Import model mới
import model.User;      
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

/**
 * Controller (C)
 * Xử lý trang Thông tin cá nhân (Profile) của người dùng
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        // 1. Kiểm tra xem người dùng đã đăng nhập chưa
        User user = (User) session.getAttribute("account");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); 
            return;
        }

        // 2. Lấy danh sách danh mục (cho sidebar)
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categoryList = categoryDAO.getAllCategories();
        request.setAttribute("categoryList", categoryList);

        // === THAY ĐỔI LỚN Ở ĐÂY ===
        // 3. Lấy lịch sử đơn hàng TRỰC QUAN (đã JOIN)
        OrderDAO orderDAO = new OrderDAO();
        
        // Gọi hàm DAO mới (getOrderHistoryByUserID) thay vì hàm cũ
        List<OrderHistoryItem> orderHistory = orderDAO.getOrderHistoryByUserID(user.getUser_id());
        
        // Đổi tên attribute để JSP sử dụng
        request.setAttribute("orderHistory", orderHistory); 
        // ============================

        // 4. Chuyển hướng tới trang View (profile.jsp)
        request.getRequestDispatcher("profile.jsp").forward(request, response);
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