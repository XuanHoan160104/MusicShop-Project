package admin.controller;

import dal.OrderDAO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet(name = "ReportServlet", urlPatterns = {"/admin/report"})
public class ReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        OrderDAO orderDAO = new OrderDAO();
        Gson gson = new Gson();

        // --- 1. Doanh thu 7 ngày qua ---
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        Map<LocalDate, Double> dailyData = orderDAO.getDailyRevenue(startDate, endDate);

        Map<String, Double> weeklyChartData = new LinkedHashMap<>();
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd/MM");
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            weeklyChartData.put(currentDate.format(dayFormat), 0.0);
            currentDate = currentDate.plusDays(1);
        }

        for (Map.Entry<LocalDate, Double> entry : dailyData.entrySet()) {
            String key = entry.getKey().format(dayFormat);
            if (weeklyChartData.containsKey(key)) {
                weeklyChartData.put(key, entry.getValue());
            }
        }

        request.setAttribute("weeklyLabels", gson.toJson(weeklyChartData.keySet()));
        request.setAttribute("weeklyData", gson.toJson(weeklyChartData.values()));

        // --- 2. Doanh thu 12 tháng qua ---
        LocalDate endMonth = LocalDate.now();
        LocalDate startMonth = endMonth.withDayOfMonth(1).minusMonths(11);

        Map<LocalDate, Double> monthlyRawData = orderDAO.getDailyRevenue(startMonth, endMonth);
        Map<String, Double> monthlyChartData = new LinkedHashMap<>();
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM/yyyy");

        LocalDate currentMonth = startMonth;
        for (int i = 0; i < 12; i++) {
            monthlyChartData.put(currentMonth.format(monthFormat), 0.0);
            currentMonth = currentMonth.plusMonths(1);
        }

        for (Map.Entry<LocalDate, Double> entry : monthlyRawData.entrySet()) {
            String monthKey = entry.getKey().format(monthFormat);
            if (monthlyChartData.containsKey(monthKey)) {
                double total = monthlyChartData.get(monthKey);
                monthlyChartData.put(monthKey, total + entry.getValue());
            }
        }

        request.setAttribute("monthlyLabels", gson.toJson(monthlyChartData.keySet()));
        request.setAttribute("monthlyData", gson.toJson(monthlyChartData.values()));

        request.getRequestDispatcher("/admin/report.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
