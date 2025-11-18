package admin.controller;

import dal.OrderDAO;
import model.Order;
import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
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
        
        // 1. Lấy danh sách đơn hàng
        List<Order> orderList = orderDAO.getAllOrders();
        
        // --- LOGIC TÍNH DOANH THU MỚI (DỰA TRÊN NGÀY GIAO HÀNG) ---
        
        LocalDate today = LocalDate.now();

        // 2. Tính Doanh thu Tháng này - SỬA Ở ĐÂY
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        // SỬA: Dùng method mới với LocalDate
        double monthlyRevenue = orderDAO.getRevenueByDateRange(startOfMonth, endOfMonth);

        // 3. Tính Doanh thu Tuần này (Tuần bắt đầu từ Thứ 2 - MONDAY) - SỬA Ở ĐÂY
        // SỬA: Đảm bảo tính đúng tuần hiện tại, nếu hôm nay là Chủ nhật thì tuần này vẫn là từ Thứ 2 đến Chủ nhật
        LocalDate startOfWeek = today;
        // Nếu hôm nay không phải Thứ 2, tìm Thứ 2 gần nhất trước đó
        if (today.getDayOfWeek() != DayOfWeek.MONDAY) {
            startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        // Tuần kết thúc vào Chủ nhật của tuần đó
        LocalDate endOfWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        // Nếu hôm nay chưa đến Chủ nhật, chỉ tính đến hôm nay
        if (endOfWeek.isAfter(today)) {
            endOfWeek = today;
        }
        // SỬA: Dùng method mới với LocalDate
        double weeklyRevenue = orderDAO.getRevenueByDateRange(startOfWeek, endOfWeek);
        
        // 4. Lấy các thống kê cũ
        double totalRevenue = orderDAO.getTotalRevenue(); // Tổng doanh thu tất cả thời gian
        int pendingOrders = orderDAO.countPendingOrders();
        int totalCustomers = orderDAO.countTotalCustomers();

        // 5. Đẩy TẤT CẢ dữ liệu sang View
        request.setAttribute("orderList", orderList);
        request.setAttribute("totalRevenue", totalRevenue);     // Tổng doanh thu
        request.setAttribute("monthlyRevenue", monthlyRevenue); // DOANH THU THÁNG MỚI
        request.setAttribute("weeklyRevenue", weeklyRevenue);   // DOANH THU TUẦN MỚI
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("activePage", "dashboard"); // Cho sidebar
        
        // DEBUG: In ra console để kiểm tra
        System.out.println("=== AdminDashboardServlet ===");
        System.out.println("Total Revenue: " + totalRevenue);
        System.out.println("Monthly Revenue: " + monthlyRevenue);
        System.out.println("Weekly Revenue: " + weeklyRevenue);
        System.out.println("Pending Orders: " + pendingOrders);
        System.out.println("Total Customers: " + totalCustomers);
        System.out.println("Date Range - Month: " + startOfMonth + " to " + endOfMonth);
        System.out.println("Date Range - Week: " + startOfWeek + " to " + endOfWeek);
        
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