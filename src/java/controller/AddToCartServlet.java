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
 * Controller (C)
 * Xử lý việc THÊM SẢN PHẨM vào giỏ hàng (lưu trong Session)
 * ĐÃ SỬA: Thêm logic kiểm tra tồn kho trước khi thêm.
 */
@WebServlet(name = "AddToCartServlet", urlPatterns = {"/add-to-cart"})
public class AddToCartServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        // 1. Lấy thông tin
        String productID = request.getParameter("productID");
        int quantity = 1;
        try {
            // Lấy số lượng từ form (trang detail), nếu không có (từ nút "Mua lại") thì mặc định là 1
            String quantityStr = request.getParameter("quantity");
            if (quantityStr != null) {
                quantity = Integer.parseInt(quantityStr);
            }
        } catch (NumberFormatException e) {
            quantity = 1;
        }

        // 2. Lấy giỏ hàng từ session
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // 3. === KIỂM TRA TỒN KHO TRƯỚC KHI THÊM ===
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductByID(productID);

        if (product == null || product.getStock_quantity() <= 0) {
            // Nếu sản phẩm không tồn tại hoặc đã hết hàng
            session.setAttribute("cartError", "Sản phẩm '" + (product != null ? product.getName() : "") + "' đã hết hàng!");
            // Chuyển hướng người dùng về trang trước đó (nếu có thể) hoặc trang chủ
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
            return; // Dừng xử lý
        }
        // ============================================

        // 4. Nếu còn hàng, tiếp tục xử lý giỏ hàng
        if (cart.containsKey(productID)) {
            // Nếu đã có: Lấy món hàng đó ra và tăng số lượng
            CartItem item = cart.get(productID);
            
            // Kiểm tra xem tổng số lượng có vượt tồn kho không
            int newQuantity = item.getQuantity() + quantity;
            if (newQuantity > product.getStock_quantity()) {
                newQuantity = product.getStock_quantity(); // Chỉ cho phép thêm tối đa
                session.setAttribute("cartMessage", "Số lượng sản phẩm " + product.getName() + " đã được điều chỉnh về mức tồn kho tối đa.");
            }
            item.setQuantity(newQuantity);
            
        } else {
            // Nếu chưa có: Tạo món hàng mới
            // (Đảm bảo số lượng thêm vào không vượt tồn kho)
            if (quantity > product.getStock_quantity()) {
                quantity = product.getStock_quantity();
                session.setAttribute("cartMessage", "Số lượng sản phẩm " + product.getName() + " đã được điều chỉnh về mức tồn kho tối đa.");
            }
            CartItem item = new CartItem(product, quantity);
            cart.put(productID, item);
        }

        // 5. Lưu giỏ hàng trở lại session
        session.setAttribute("cart", cart);

        // 6. Chuyển hướng người dùng đến trang cart.jsp
        response.sendRedirect(request.getContextPath() + "/cart.jsp");
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