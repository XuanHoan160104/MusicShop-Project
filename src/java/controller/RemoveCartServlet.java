package controller;

import model.CartItem;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Đây là Controller (C)
 * Xử lý việc XÓA sản phẩm khỏi giỏ hàng (trong Session)
 */
@WebServlet(name = "RemoveCartServlet", urlPatterns = {"/remove-cart"})
public class RemoveCartServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy 'pid' (product id) từ URL
        String productID = request.getParameter("pid");
        
        // 2. Lấy session hiện tại
        HttpSession session = request.getSession();

        // 3. Lấy giỏ hàng từ session
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        
        // 4. Kiểm tra xem giỏ hàng có tồn tại và sản phẩm có trong giỏ không
        if (cart != null && cart.containsKey(productID)) {
            // Xóa sản phẩm khỏi Map
            cart.remove(productID);
        }
        
        // 5. Lưu (cập nhật) lại giỏ hàng vào session
        session.setAttribute("cart", cart);

        // 6. Chuyển hướng (redirect) người dùng về lại trang giỏ hàng
        response.sendRedirect("cart.jsp");
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