package admin.controller;

import dal.OrderDAO;
import model.Order;
import model.OrderHistoryItem; // Dùng lại model này vì nó chứa (Ảnh, Tên, SL)
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (C)
 * Xử lý việc hiển thị CHI TIẾT MỘT ĐƠN HÀNG cho Admin
 * (AdminFilter sẽ bảo vệ URL này)
 */
// Map với URL /admin/order-detail
@WebServlet(name = "OrderDetailServlet", urlPatterns = {"/admin/order-detail"})
public class OrderDetailServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // 1. Lấy orderId từ URL (ví dụ: ?orderId=14)
        String orderId_raw = request.getParameter("orderId");
        int orderId = 0;
        try {
            orderId = Integer.parseInt(orderId_raw);
        } catch (NumberFormatException e) {
            System.err.println("Admin OrderDetail: ID đơn hàng không hợp lệ: " + orderId_raw);
            response.sendRedirect("dashboard"); // Về trang dashboard nếu ID lỗi
            return;
        }

        OrderDAO orderDAO = new OrderDAO();
        
        // 2. Lấy thông tin chung của đơn hàng (Người nhận, Địa chỉ, Trạng thái...)
        // Đảm bảo bạn đã thêm hàm getOrderById(int orderId) vào OrderDAO.java
        Order orderInfo = orderDAO.getOrderById(orderId);
        
        // 3. Lấy danh sách sản phẩm (Ảnh, Tên, SL, Giá) của đơn hàng đó
        // Đảm bảo bạn đã thêm hàm getDetailsForOrder(int orderId) vào OrderDAO.java
        List<OrderHistoryItem> orderDetails = orderDAO.getDetailsForOrder(orderId);

        // 4. Gửi cả 2 sang JSP
        request.setAttribute("orderInfo", orderInfo);
        request.setAttribute("orderDetails", orderDetails);
        
        // 5. Chuyển đến trang View
        request.getRequestDispatcher("/admin/order_detail.jsp").forward(request, response);
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