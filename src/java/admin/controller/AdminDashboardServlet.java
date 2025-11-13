package admin.controller;

import dal.OrderDAO;
import model.Order; // Đảm bảo đã import model.Order

// === THÊM CÁC IMPORT MỚI CHO JAVA TIME ===
import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.sql.Date; // Dùng java.sql.Date
// ======================================

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (C) cho trang Admin Dashboard
 * ĐÃ NÂNG CẤP: Thêm logic tính doanh thu Tuần/Tháng (dựa trên shipped_date)
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        OrderDAO orderDAO = new OrderDAO();
        
        // 1. Lấy danh sách đơn hàng (giữ nguyên)
        List<model.Order> orderList = orderDAO.getAllOrders();
        
        // --- LOGIC TÍNH DOANH THU MỚI (DỰA TRÊN NGÀY GIAO HÀNG) ---
        
        LocalDate today = LocalDate.now();

        // 2. Tính Doanh thu Tháng này
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        // Gọi hàm DAO mới (lưu ý: hàm này đã được sửa để dùng shipped_date)
        double monthlyRevenue = orderDAO.getTotalRevenueByDateRange(
            Date.valueOf(startOfMonth), 
            Date.valueOf(endOfMonth)
        );

        // 3. Tính Doanh thu Tuần này (Giả sử tuần bắt đầu từ Thứ 2 - MONDAY)
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        double weeklyRevenue = orderDAO.getTotalRevenueByDateRange(
            Date.valueOf(startOfWeek), 
            Date.valueOf(endOfWeek)
        );
        
        // 4. Lấy các thống kê cũ
        int pendingOrders = orderDAO.countPendingOrders();
        int totalCustomers = orderDAO.countTotalCustomers();

        // 5. Đẩy TẤT CẢ dữ liệu sang View
        request.setAttribute("orderList", orderList);
        request.setAttribute("monthlyRevenue", monthlyRevenue); // DOANH THU THÁNG MỚI
        request.setAttribute("weeklyRevenue", weeklyRevenue);   // DOANH THU TUẦN MỚI
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("activePage", "dashboard"); // Cho sidebar
        
        // 6. Chuyển hướng
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