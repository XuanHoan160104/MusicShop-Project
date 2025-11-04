package controller;

import dal.ProductDAO;
import model.CartItem;
import model.Product;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Đây là Controller (C)
 * Xử lý việc THÊM SẢN PHẨM vào giỏ hàng (lưu trong Session)
 */
@WebServlet(name = "AddToCartServlet", urlPatterns = {"/add-to-cart"})
public class AddToCartServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // 1. Lấy thông tin từ form (trang detail.jsp)
        String productID = request.getParameter("productID");
        int quantity = 1;
        try {
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException e) {
            System.out.println("Lỗi số lượng: " + e.getMessage());
        }

        // 2. Lấy session hiện tại
        HttpSession session = request.getSession();

        // 3. Lấy giỏ hàng từ session
        // Chúng ta sẽ dùng Map<String, CartItem> để lưu giỏ hàng
        // Key là productID (String), Value là CartItem
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");

        // 4. Nếu giỏ hàng chưa tồn tại (lần đầu thêm), tạo giỏ hàng mới
        if (cart == null) {
            cart = new HashMap<>();
        }

        // 5. Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        if (cart.containsKey(productID)) {
            // Nếu đã có: Lấy món hàng đó ra và tăng số lượng
            CartItem item = cart.get(productID);
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Nếu chưa có: Lấy sản phẩm từ CSDL và tạo món hàng mới
            ProductDAO productDAO = new ProductDAO();
            Product product = productDAO.getProductByID(productID);
            
            CartItem item = new CartItem(product, quantity);
            cart.put(productID, item);
        }

        // 6. Lưu giỏ hàng (cart) trở lại session
        session.setAttribute("cart", cart);

        // 7. Chuyển hướng (redirect) người dùng đến trang cart.jsp
        // (Chúng ta sẽ tạo trang này ở bước sau)
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