package admin.controller;

import dal.OrderDAO;
import model.Order; // Đảm bảo đã import model.Order
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (C) cho trang Admin Dashboard
 * Sẽ được Filter bảo vệ
 * Nhiệm vụ: Tải tất cả đơn hàng + Thống kê doanh thu
 */
// Map với URL /admin/dashboard
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        OrderDAO orderDAO = new OrderDAO();
        
        // 1. Lấy danh sách đơn hàng
        List<model.Order> orderList = orderDAO.getAllOrders();
        
        // 2. Lấy số liệu thống kê
        double totalRevenue = orderDAO.getTotalRevenue();
        int pendingOrders = orderDAO.countPendingOrders();
        int totalCustomers = orderDAO.countTotalCustomers();

        // 3. Đẩy TẤT CẢ dữ liệu sang View
        request.setAttribute("orderList", orderList);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("totalCustomers", totalCustomers);
        
        // === SỬA Ở ĐÂY: Thêm dòng này để báo cho sidebar biết trang nào đang active ===
        request.setAttribute("activePage", "dashboard");
        // =======================================================================
        
        // 4. Chuyển hướng (forward) tới trang View
        // (Lưu ý đường dẫn: /admin/dashboard.jsp)
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
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