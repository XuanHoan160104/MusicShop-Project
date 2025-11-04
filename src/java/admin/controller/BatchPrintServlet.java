package admin.controller;

import dal.OrderDAO;
import model.Order;
import model.OrderHistoryItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap; // Dùng LinkedHashMap để giữ đúng thứ tự
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (C)
 * Xử lý việc in hàng loạt các đơn hàng đã chọn
 * (AdminFilter sẽ bảo vệ URL này)
 */
@WebServlet(name = "BatchPrintServlet", urlPatterns = {"/admin/batch-print"})
public class BatchPrintServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // 1. Lấy mảng các ID đơn hàng đã được chọn (từ checkbox)
        String[] selectedOrderIds = request.getParameterValues("selectedOrderIds");
        
        // Nếu không chọn đơn hàng nào, quay lại dashboard
        if (selectedOrderIds == null || selectedOrderIds.length == 0) {
            response.sendRedirect("dashboard");
            return;
        }

        OrderDAO orderDAO = new OrderDAO();
        
        // Dùng LinkedHashMap để giữ đúng thứ tự các đơn hàng đã chọn
        // Map này sẽ lưu: (Thông tin Đơn hàng -> Danh sách sản phẩm của đơn đó)
        Map<Order, List<OrderHistoryItem>> ordersToPrint = new LinkedHashMap<>();

        // 2. Lặp qua từng ID, lấy thông tin chung và chi tiết sản phẩm
        for (String id_raw : selectedOrderIds) {
            try {
                int orderId = Integer.parseInt(id_raw);
                
                // Lấy thông tin chung (Tên, SĐT, Địa chỉ...)
                Order orderInfo = orderDAO.getOrderById(orderId);
                // Lấy danh sách sản phẩm (Ảnh, Tên, SL)
                List<OrderHistoryItem> orderDetails = orderDAO.getDetailsForOrder(orderId);
                
                // Chỉ thêm vào Map nếu lấy được cả 2 thông tin
                if (orderInfo != null && orderDetails != null && !orderDetails.isEmpty()) {
                    ordersToPrint.put(orderInfo, orderDetails);
                }
                
            } catch (NumberFormatException e) {
                System.err.println("Lỗi ID đơn hàng không hợp lệ khi in loạt: " + id_raw);
            }
        }
        
        // 3. Gửi Map chứa dữ liệu sang trang JSP
        request.setAttribute("ordersToPrint", ordersToPrint);
        
        // 4. Chuyển đến trang View để in (sẽ tạo ở bước sau)
        request.getRequestDispatcher("/admin/batch_print_view.jsp").forward(request, response);
    }
}