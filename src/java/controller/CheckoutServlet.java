package controller;

import dal.CategoryDAO;
import dal.OrderDAO;
import dal.VoucherDAO;
import model.CartItem;
import model.Category;
import model.User;
import model.Voucher;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Đây là Controller (C)
 * Xử lý logic THANH TOÁN cuối cùng
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    // Xử lý khi người dùng gõ thẳng URL /checkout (method="get")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Khi người dùng vào /checkout (GET), chúng ta chỉ hiển thị trang checkout.jsp
        // (Trang này đã có logic kiểm tra đăng nhập bên trong JSP)
        
        // Tải danh mục cho sidebar
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categoryList = categoryDAO.getAllCategories();
        request.setAttribute("categoryList", categoryList);
        
        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }

    // Xử lý khi người dùng nhấn nút "Xác nhận Đặt hàng" (method="post")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        // Đặt UTF-8 để nhận Tiếng Việt từ form
        request.setCharacterEncoding("UTF-8"); 
        
        HttpSession session = request.getSession();

        // 1. Lấy thông tin người dùng và giỏ hàng từ Session
        User user = (User) session.getAttribute("account");
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");

        // 2. Kiểm tra xem user đã đăng nhập và giỏ hàng có đồ chưa
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect("home");
            return;
        }

        // === Đọc thông tin giao hàng từ form ===
        String shipName = request.getParameter("shipping_name");
        String shipPhone = request.getParameter("shipping_phone");
        String shipAddress = request.getParameter("shipping_address");
        String voucherCode = request.getParameter("voucher_code"); // Mã giảm giá
        // ================================================

        // 3. Xử lý voucher nếu có
        Integer voucherId = null;
        double discountAmount = 0.0;
        
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            VoucherDAO voucherDAO = new VoucherDAO();
            Voucher voucher = voucherDAO.validateVoucherForUse(voucherCode.trim(), user.getUser_id());
            
            if (voucher != null) {
                // Tính tổng tiền giỏ hàng
                double totalAmount = 0;
                for (CartItem item : cart.values()) {
                    totalAmount += item.getTotalItemPrice();
                }
                
                // Tính số tiền giảm giá
                discountAmount = voucherDAO.calculateDiscount(voucher, totalAmount);
                voucherId = voucher.getVoucher_id();
            } else {
                // Voucher không hợp lệ
                CategoryDAO categoryDAO = new CategoryDAO();
                List<Category> categoryList = categoryDAO.getAllCategories();
                request.setAttribute("categoryList", categoryList);
                request.setAttribute("error", "Mã giảm giá không hợp lệ, đã hết hạn hoặc bạn đã sử dụng mã này rồi!");
                request.setAttribute("voucherError", "Mã giảm giá không hợp lệ, đã hết hạn hoặc bạn đã sử dụng mã này rồi!");
                request.getRequestDispatcher("checkout.jsp").forward(request, response);
                return;
            }
        }

        // 4. (C) gọi (DAO) để thực hiện logic Transaction
        OrderDAO orderDAO = new OrderDAO();
        
        // === Truyền voucher vào hàm ===
        boolean success = orderDAO.createOrder(user, cart, shipName, shipPhone, shipAddress, voucherId, discountAmount);

        // 5. Xử lý kết quả
        if (success) {
            // Đặt hàng thành công
            session.removeAttribute("cart");
            response.sendRedirect("order_success.jsp");
        } else {
            // Đặt hàng thất bại (thường là do hết hàng)
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categoryList = categoryDAO.getAllCategories();
            request.setAttribute("categoryList", categoryList);
            
            request.setAttribute("error", "Đặt hàng thất bại! Một số sản phẩm có thể đã hết hàng. Vui lòng kiểm tra lại giỏ hàng.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
        }
    }
}