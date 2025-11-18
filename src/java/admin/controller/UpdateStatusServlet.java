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
        
        try {
            // 1. Lấy dữ liệu từ form (dashboard.jsp)
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String newStatus = request.getParameter("newStatus");
            
            // 2. (C) gọi (DAO)
            OrderDAO orderDAO = new OrderDAO();
            boolean success = orderDAO.updateOrderStatus(orderId, newStatus);
            
            // 3. Xác định trang redirect
            String redirectUrl = "dashboard"; // Mặc định về dashboard
            
            // Nếu có tham số redirectUrl từ form, dùng nó
            String redirectParam = request.getParameter("redirectUrl");
            if (redirectParam != null && !redirectParam.isEmpty()) {
                redirectUrl = redirectParam;
            }
            // Nếu không có, kiểm tra referer
            else {
                String referer = request.getHeader("Referer");
                if (referer != null && referer.contains("/admin/report") && "Shipped".equals(newStatus) && success) {
                    redirectUrl = "report?updated=true&orderId=" + orderId;
                }
            }
            
            // 4. Chuyển hướng
            response.sendRedirect(redirectUrl);
            
        } catch (NumberFormatException e) {
            // Xử lý lỗi nếu orderId không hợp lệ
            response.sendRedirect("dashboard?error=invalid_order");
        } catch (Exception e) {
            // Xử lý lỗi khác
            e.printStackTrace();
            response.sendRedirect("dashboard?error=update_failed");
        }
    }
    
    // Không cần dùng doGet, vì form của chúng ta dùng method="post"
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu ai đó cố tình gõ URL này, cứ đá họ về dashboard
        response.sendRedirect("dashboard");
    }
}