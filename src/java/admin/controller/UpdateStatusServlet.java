package admin.controller;

import dal.OrderDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (C)
 * Xử lý việc cập nhật trạng thái đơn hàng
 * Sẽ được Filter bảo vệ vì URL bắt đầu bằng /admin/
 */
// Map với URL /admin/update-status
@WebServlet(name = "UpdateStatusServlet", urlPatterns = {"/admin/update-status"})
public class UpdateStatusServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy dữ liệu từ form (dashboard.jsp)
        // Dùng Integer.parseInt để đổi chuỗi (String) sang số (int)
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        String newStatus = request.getParameter("newStatus");
        
        // 2. (C) gọi (DAO)
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.updateOrderStatus(orderId, newStatus);
        
        // 3. Chuyển hướng (redirect) admin về lại trang dashboard
        // Gửi redirect về "dashboard" (nó sẽ tự hiểu là /admin/dashboard)
        response.sendRedirect("dashboard");
    }
    
    // Không cần dùng doGet, vì form của chúng ta dùng method="post"
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu ai đó cố tình gõ URL này, cứ đá họ về dashboard
        response.sendRedirect("dashboard");
    }
}