package controller;

import model.CartItem;
import model.Product; // Cần import Product để kiểm tra tồn kho
import dal.ProductDAO; // Cần ProductDAO để kiểm tra tồn kho
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller (C)
 * Xử lý việc CẬP NHẬT SỐ LƯỢNG sản phẩm trong giỏ hàng
 */
@WebServlet(name = "UpdateCartServlet", urlPatterns = {"/update-cart"})
public class UpdateCartServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        // 1. Lấy giỏ hàng từ session
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");

        if (cart != null) {
            // 2. Lấy thông tin từ form
            String productId = request.getParameter("productId");
            int newQuantity = 1; // Mặc định
            
            try {
                newQuantity = Integer.parseInt(request.getParameter("quantity"));
            } catch (NumberFormatException e) {
                System.err.println("Lỗi parse số lượng khi cập nhật giỏ hàng: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/cart.jsp");
                return;
            }

            // 3. Kiểm tra logic (số lượng phải > 0 và kiểm tra tồn kho)
            if (newQuantity <= 0) {
                // Nếu số lượng là 0 hoặc âm, coi như Xóa
                cart.remove(productId);
            } else {
                // Kiểm tra xem sản phẩm có trong giỏ không
                if (cart.containsKey(productId)) {
                    // Lấy sản phẩm từ CSDL để kiểm tra tồn kho
                    ProductDAO productDAO = new ProductDAO();
                    Product product = productDAO.getProductByID(productId);
                    
                    if (product != null) {
                        // Nếu số lượng mới VƯỢT QUÁ tồn kho, chỉ đặt bằng max tồn kho
                        if (newQuantity > product.getStock_quantity()) {
                            newQuantity = product.getStock_quantity();
                            session.setAttribute("cartMessage", "Số lượng sản phẩm " + product.getName() + " đã được điều chỉnh về mức tồn kho tối đa (" + product.getStock_quantity() + ").");
                        }
                        
                        // Cập nhật số lượng mới
                        CartItem item = cart.get(productId);
                        item.setQuantity(newQuantity);
                    }
                }
            }
            
            // 4. Lưu lại giỏ hàng vào session
            session.setAttribute("cart", cart);
        }

        // 5. Chuyển hướng về lại trang giỏ hàng
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