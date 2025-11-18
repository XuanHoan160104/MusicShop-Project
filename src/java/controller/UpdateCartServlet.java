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
            
            if (productId == null || productId.trim().isEmpty()) {
                System.err.println("UpdateCart: productId là null hoặc rỗng");
                String redirectTo = request.getParameter("redirectTo");
                if ("checkout".equals(redirectTo)) {
                    response.sendRedirect(request.getContextPath() + "/checkout");
                } else {
                    response.sendRedirect(request.getContextPath() + "/cart.jsp");
                }
                return;
            }
            
            try {
                String quantityStr = request.getParameter("quantity");
                if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                    newQuantity = Integer.parseInt(quantityStr);
                }
            } catch (NumberFormatException e) {
                System.err.println("Lỗi parse số lượng khi cập nhật giỏ hàng: " + e.getMessage());
                String redirectTo = request.getParameter("redirectTo");
                if ("checkout".equals(redirectTo)) {
                    response.sendRedirect(request.getContextPath() + "/checkout");
                } else {
                    response.sendRedirect(request.getContextPath() + "/cart.jsp");
                }
                return;
            }

            System.out.println("UpdateCart: productId=" + productId + ", newQuantity=" + newQuantity);
            System.out.println("UpdateCart: cart keys=" + cart.keySet());

            // 3. Kiểm tra logic (số lượng phải > 0 và kiểm tra tồn kho)
            if (newQuantity <= 0) {
                // Nếu số lượng là 0 hoặc âm, coi như Xóa
                cart.remove(productId);
                System.out.println("UpdateCart: Đã xóa sản phẩm " + productId);
            } else {
                // Kiểm tra xem sản phẩm có trong giỏ không - thử cả String và Integer
                CartItem item = null;
                if (cart.containsKey(productId)) {
                    item = cart.get(productId);
                } else {
                    // Thử tìm với Integer key
                    try {
                        Integer productIdInt = Integer.parseInt(productId);
                        if (cart.containsKey(String.valueOf(productIdInt))) {
                            item = cart.get(String.valueOf(productIdInt));
                            productId = String.valueOf(productIdInt); // Cập nhật lại productId
                        }
                    } catch (NumberFormatException e) {
                        // Không phải số, bỏ qua
                    }
                }
                
                if (item != null) {
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
                        item.setQuantity(newQuantity);
                        System.out.println("UpdateCart: Đã cập nhật sản phẩm " + productId + " với số lượng " + newQuantity);
                    } else {
                        System.err.println("UpdateCart: Không tìm thấy sản phẩm với ID: " + productId);
                    }
                } else {
                    System.err.println("UpdateCart: Không tìm thấy sản phẩm trong giỏ hàng với ID: " + productId);
                }
            }
            
            // 4. Lưu lại giỏ hàng vào session
            session.setAttribute("cart", cart);
            System.out.println("UpdateCart: Đã lưu giỏ hàng vào session");
        } else {
            System.err.println("UpdateCart: Giỏ hàng là null");
        }

        // 5. Chuyển hướng hoặc trả về JSON - kiểm tra xem có redirect về checkout không
        String redirectTo = request.getParameter("redirectTo");
        String acceptHeader = request.getHeader("Accept");
        
        // Nếu là AJAX request (có redirectTo=checkout), trả về JSON
        if ("checkout".equals(redirectTo)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            java.io.PrintWriter out = response.getWriter();
            out.print("{\"success\": true, \"message\": \"Đã cập nhật số lượng thành công\"}");
            out.flush();
            return;
        } else {
            // Nếu không phải AJAX, redirect như bình thường
            response.sendRedirect(request.getContextPath() + "/cart.jsp");
        }
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