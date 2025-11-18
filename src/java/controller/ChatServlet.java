package controller;

import dal.MessageDAO;
import model.Message;
import model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet xử lý chat cho người dùng
 * - GET: Lấy danh sách tin nhắn
 * - POST: Gửi tin nhắn mới
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/chat"})
public class ChatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");
        
        if (user == null) {
            sendError(response, "Bạn cần đăng nhập để sử dụng chat!");
            return;
        }
        
        MessageDAO messageDAO = new MessageDAO();
        List<Message> messages = messageDAO.getUserMessages(user.getUser_id());
        
        // Đánh dấu tin nhắn từ admin là đã đọc khi user xem
        for (Message msg : messages) {
            if (msg.isIs_from_admin() && !msg.isIs_read()) {
                messageDAO.markAsRead(msg.getMessage_id());
            }
        }
        
        // Reload lại danh sách sau khi đánh dấu đã đọc
        messages = messageDAO.getUserMessages(user.getUser_id());
        int unreadCount = messageDAO.getUnreadCountForUser(user.getUser_id());
        
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("unread_count", unreadCount);
        
        Gson gson = new Gson();
        json.add("messages", gson.toJsonTree(messages));
        
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ĐẶT BIẾT encoding TRƯỚC khi đọc parameter
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");
        
        if (user == null) {
            sendError(response, "Bạn cần đăng nhập để gửi tin nhắn!");
            return;
        }
        
        // Đọc message từ request parameter
        String message = request.getParameter("message");
        String productIdStr = request.getParameter("product_id");
        
        // Debug: in ra console để kiểm tra
        System.out.println("ChatServlet - Received message: " + message);
        System.out.println("ChatServlet - Product ID: " + productIdStr);
        
        if (message == null || message.trim().isEmpty()) {
            System.err.println("ChatServlet - Message is empty or null");
            sendError(response, "Nội dung tin nhắn không được để trống!");
            return;
        }
        
        try {
            MessageDAO messageDAO = new MessageDAO();
            Integer productId = null;
            if (productIdStr != null && !productIdStr.trim().isEmpty()) {
                productId = Integer.parseInt(productIdStr);
            }
            
            boolean success = messageDAO.sendMessageFromUser(user.getUser_id(), message.trim(), productId);
            
            if (success) {
                JsonObject json = new JsonObject();
                json.addProperty("success", true);
                json.addProperty("message", "Tin nhắn đã được gửi!");
                
                PrintWriter out = response.getWriter();
                out.print(json.toString());
                out.flush();
            } else {
                sendError(response, "Không thể gửi tin nhắn. Vui lòng thử lại!");
            }
        } catch (NumberFormatException e) {
            sendError(response, "ID sản phẩm không hợp lệ!");
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

