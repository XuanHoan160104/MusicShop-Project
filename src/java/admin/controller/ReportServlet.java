package admin.controller;

import dal.OrderDAO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ReportServlet", urlPatterns = {"/admin/report"})
public class ReportServlet extends HttpServlet {

    private OrderDAO orderDAO;
    
    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CHỐNG CACHE - để luôn load dữ liệu mới
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            // =======================
            // 1️⃣ Doanh thu 6 ngày gần nhất
            // =======================
            Map<String, Object> weeklyData = getWeeklyRevenueData();
            List<String> weeklyLabels = (List<String>) weeklyData.get("labels");
            List<Double> weeklyDataList = (List<Double>) weeklyData.get("data");
            
            // SỬA: Đảm bảo luôn có dữ liệu, không bao giờ null
            if (weeklyLabels == null) weeklyLabels = new ArrayList<>();
            if (weeklyDataList == null) weeklyDataList = new ArrayList<>();
            
            request.setAttribute("weeklyLabels", weeklyLabels);
            request.setAttribute("weeklyData", weeklyDataList);

            // =======================
            // 2️⃣ Doanh thu 12 tháng qua
            // =======================
            Map<String, Object> monthlyData = getMonthlyRevenueData();
            List<String> monthlyLabels = (List<String>) monthlyData.get("labels");
            List<Double> monthlyDataList = (List<Double>) monthlyData.get("data");
            
            // SỬA: Đảm bảo luôn có dữ liệu, không bao giờ null
            if (monthlyLabels == null) monthlyLabels = new ArrayList<>();
            if (monthlyDataList == null) monthlyDataList = new ArrayList<>();
            
            request.setAttribute("monthlyLabels", monthlyLabels);
            request.setAttribute("monthlyData", monthlyDataList);

            // =======================
            // 3️⃣ Thống kê tổng quan
            // =======================
            Map<String, Object> stats = getRevenueStats();
            request.setAttribute("todayRevenue", stats.get("todayRevenue") != null ? stats.get("todayRevenue") : 0.0);
            request.setAttribute("weekRevenue", stats.get("weekRevenue") != null ? stats.get("weekRevenue") : 0.0);
            request.setAttribute("monthRevenue", stats.get("monthRevenue") != null ? stats.get("monthRevenue") : 0.0);
            request.setAttribute("todayTrend", stats.get("todayTrend") != null ? stats.get("todayTrend") : 0.0);
            request.setAttribute("weekTrend", stats.get("weekTrend") != null ? stats.get("weekTrend") : 0.0);
            request.setAttribute("monthTrend", stats.get("monthTrend") != null ? stats.get("monthTrend") : 0.0);

        } catch (Exception e) {
            System.err.println("ERROR in ReportServlet: " + e.getMessage());
            e.printStackTrace();
            // Set default values khi có lỗi
            setDefaultAttributes(request);
        }

        request.getRequestDispatcher("/admin/report.jsp").forward(request, response);
    }

    /**
     * Lấy dữ liệu doanh thu 6 ngày gần nhất
     */
    private Map<String, Object> getWeeklyRevenueData() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(5); // 6 ngày: hôm nay + 5 ngày trước

        Map<LocalDate, Double> dailyData = orderDAO.getDailyRevenue(startDate, endDate);
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd/MM");

        // Đảm bảo luôn có 6 ngày, kể cả ngày không có doanh thu
        for (int i = 0; i <= 5; i++) {
            LocalDate date = startDate.plusDays(i);
            double value = dailyData.getOrDefault(date, 0.0);
            
            labels.add(date.format(dayFormat));
            data.add(value);
        }

        result.put("labels", labels);
        result.put("data", data);
        return result;
    }

    /**
     * Lấy dữ liệu doanh thu 12 tháng qua
     * TỐI ƯU: Dùng query trực tiếp theo tháng thay vì lấy theo ngày rồi tổng hợp
     */
    private Map<String, Object> getMonthlyRevenueData() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(11).withDayOfMonth(1); // 12 tháng tính từ đầu tháng

        // TỐI ƯU: Dùng getMonthlyRevenue thay vì getDailyRevenue
        Map<String, Double> monthlyRevenue = orderDAO.getMonthlyRevenue(startDate, endDate);
        
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM/yyyy");

        // Khởi tạo 12 tháng với giá trị 0
        LocalDate currentMonth = startDate;
        for (int i = 0; i < 12; i++) {
            String monthKey = currentMonth.format(monthFormat);
            // Lấy giá trị từ database hoặc 0
            Double revenue = monthlyRevenue.getOrDefault(monthKey, 0.0);
            labels.add(monthKey);
            data.add(revenue);
            currentMonth = currentMonth.plusMonths(1);
        }

        result.put("labels", labels);
        result.put("data", data);
        return result;
    }

    /**
     * Lấy thống kê tổng quan
     */
    private Map<String, Object> getRevenueStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate lastMonthStart = monthStart.minusMonths(1);
        
        try {
            // Doanh thu hôm nay
            double todayRevenue = orderDAO.getRevenueByDate(today);
            stats.put("todayRevenue", todayRevenue);
            
            // Doanh thu tuần này (7 ngày)
            double weekRevenue = orderDAO.getRevenueByDateRange(weekStart, today);
            stats.put("weekRevenue", weekRevenue);
            
            // Doanh thu tháng này
            double monthRevenue = orderDAO.getRevenueByDateRange(monthStart, today);
            stats.put("monthRevenue", monthRevenue);
            
            // Tính xu hướng (so với ngày/ tuần/ tháng trước)
            double yesterdayRevenue = orderDAO.getRevenueByDate(today.minusDays(1));
            double lastWeekRevenue = orderDAO.getRevenueByDateRange(weekStart.minusDays(7), weekStart.minusDays(1));
            double lastMonthRevenue = orderDAO.getRevenueByDateRange(lastMonthStart, monthStart.minusDays(1));
            
            // Xu hướng hôm nay so với hôm qua
            double todayTrend = calculateTrend(todayRevenue, yesterdayRevenue);
            stats.put("todayTrend", todayTrend);
            
            // Xu hướng tuần này so với tuần trước
            double weekTrend = calculateTrend(weekRevenue, lastWeekRevenue);
            stats.put("weekTrend", weekTrend);
            
            // Xu hướng tháng này so với tháng trước
            double monthTrend = calculateTrend(monthRevenue, lastMonthRevenue);
            stats.put("monthTrend", monthTrend);
            
        } catch (Exception e) {
            System.err.println("Error calculating stats: " + e.getMessage());
            // Set default values
            stats.put("todayRevenue", 0.0);
            stats.put("weekRevenue", 0.0);
            stats.put("monthRevenue", 0.0);
            stats.put("todayTrend", 0.0);
            stats.put("weekTrend", 0.0);
            stats.put("monthTrend", 0.0);
        }
        
        return stats;
    }

    /**
     * Tính phần trăm thay đổi
     */
    private double calculateTrend(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((current - previous) / previous) * 100;
    }

    /**
     * Set giá trị mặc định khi có lỗi
     */
    private void setDefaultAttributes(HttpServletRequest request) {
        // Dữ liệu mặc định cho 6 ngày
        List<String> defaultWeeklyLabels = List.of("01/01", "02/01", "03/01", "04/01", "05/01", "06/01");
        List<Double> defaultWeeklyData = List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        
        request.setAttribute("weeklyLabels", defaultWeeklyLabels);
        request.setAttribute("weeklyData", defaultWeeklyData);
        
        // Dữ liệu mặc định cho 12 tháng
        List<String> defaultMonthlyLabels = new ArrayList<>();
        List<Double> defaultMonthlyData = new ArrayList<>();
        
        LocalDate current = LocalDate.now().minusMonths(11);
        for (int i = 0; i < 12; i++) {
            defaultMonthlyLabels.add(current.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            defaultMonthlyData.add(0.0);
            current = current.plusMonths(1);
        }
        
        request.setAttribute("monthlyLabels", defaultMonthlyLabels);
        request.setAttribute("monthlyData", defaultMonthlyData);
        
        // Stats mặc định
        request.setAttribute("todayRevenue", 0.0);
        request.setAttribute("weekRevenue", 0.0);
        request.setAttribute("monthRevenue", 0.0);
        request.setAttribute("todayTrend", 0.0);
        request.setAttribute("weekTrend", 0.0);
        request.setAttribute("monthTrend", 0.0);
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