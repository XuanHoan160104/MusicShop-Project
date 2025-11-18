package controller;

import dal.VoucherDAO;
import model.User;
import model.Voucher;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.JsonObject;

/**
 * Servlet để validate voucher qua AJAX (khi user nhập mã giảm giá)
 */
@WebServlet(name = "VoucherValidateServlet", urlPatterns = {"/validate-voucher"})
public class VoucherValidateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");
        
        if (user == null) {
            sendError(response, "Bạn cần đăng nhập để sử dụng mã giảm giá!");
            return;
        }
        
        String voucherCode = request.getParameter("voucher_code");
        String cartTotalStr = request.getParameter("cart_total");
        
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            sendError(response, "Vui lòng nhập mã giảm giá!");
            return;
        }
        
        try {
            double cartTotal = 0.0;
            if (cartTotalStr != null && !cartTotalStr.trim().isEmpty()) {
                cartTotal = Double.parseDouble(cartTotalStr);
            }
            
            VoucherDAO voucherDAO = new VoucherDAO();
            Voucher voucher = voucherDAO.validateVoucherForUse(voucherCode.trim(), user.getUser_id());
            
            if (voucher == null) {
                sendError(response, "Mã giảm giá không hợp lệ, đã hết hạn, chưa được đăng trong tin tức hoặc bạn đã sử dụng mã này rồi!");
                return;
            }
            
            // Tính số tiền giảm giá
            double discountAmount = voucherDAO.calculateDiscount(voucher, cartTotal);
            double finalAmount = cartTotal - discountAmount;
            if (finalAmount < 0) {
                finalAmount = 0;
            }
            
            // Trả về JSON với thông tin voucher
            JsonObject json = new JsonObject();
            json.addProperty("success", true);
            json.addProperty("discount_amount", discountAmount);
            json.addProperty("final_amount", finalAmount);
            json.addProperty("discount_type", voucher.getDiscount_type());
            json.addProperty("discount_value", voucher.getDiscount_value());
            json.addProperty("description", voucher.getDescription() != null ? voucher.getDescription() : "");
            
            PrintWriter out = response.getWriter();
            out.print(json.toString());
            out.flush();
            
        } catch (NumberFormatException e) {
            sendError(response, "Lỗi định dạng số tiền!");
        } catch (Exception e) {
            sendError(response, "Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    private void sendError(HttpServletResponse response, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);
        
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }
}

