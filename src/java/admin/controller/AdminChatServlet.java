package admin.controller;

import dal.MessageDAO;
import dal.UserDAO;
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
 * Servlet quản lý chat cho admin
 * - GET: Hiển thị trang chat (danh sách users và tin nhắn)
 * - POST: Gửi tin nhắn trả lời user
 */
@WebServlet(name = "AdminChatServlet", urlPatterns = {"/admin/chat"})
public class AdminChatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("account");
        
        // Kiểm tra quyền admin
        if (admin == null || !"admin".equals(admin.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        MessageDAO messageDAO = new MessageDAO();
        UserDAO userDAO = new UserDAO();
        
        // Lấy danh sách users đã chat
        List<Integer> userIds = messageDAO.getUsersWithMessages();
        List<User> usersWithMessages = new java.util.ArrayList<>();
        for (Integer userId : userIds) {
            User user = userDAO.getUserById(userId);
            if (user != null) {
                usersWithMessages.add(user);
            }
        }
        
        // Lấy user_id được chọn (nếu có)
        String userIdStr = request.getParameter("user_id");
        List<Message> messages = new java.util.ArrayList<>();
        User selectedUser = null;
        
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                int userId = Integer.parseInt(userIdStr);
                messages = messageDAO.getMessagesByUserId(userId);
                selectedUser = userDAO.getUserById(userId);
                
                // Đánh dấu tin nhắn từ user là đã đọc khi admin xem
                for (Message msg : messages) {
                    if (!msg.isIs_from_admin() && !msg.isIs_read()) {
                        messageDAO.markAsRead(msg.getMessage_id());
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        // Đếm tin nhắn chưa đọc
        int unreadCount = messageDAO.getUnreadCountForAdmin();
        
        request.setAttribute("usersWithMessages", usersWithMessages);
        request.setAttribute("messages", messages);
        request.setAttribute("selectedUser", selectedUser);
        request.setAttribute("unreadCount", unreadCount);
        request.setAttribute("activePage", "chat");
        
        // Set unreadCount vào session để sidebar có thể hiển thị
        session.setAttribute("unreadCount", unreadCount);
        
        request.getRequestDispatcher("/admin/chat.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ĐẶT encoding TRƯỚC khi đọc parameter
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("account");
        
        // Kiểm tra quyền admin
        if (admin == null || !"admin".equals(admin.getRole())) {
            sendError(response, "Bạn không có quyền truy cập!");
            return;
        }
        
        String message = request.getParameter("message");
        String userIdStr = request.getParameter("user_id");
        
        // Debug: in ra console để kiểm tra
        System.out.println("AdminChatServlet - Received message: " + message);
        System.out.println("AdminChatServlet - Received user_id: " + userIdStr);
        System.out.println("AdminChatServlet - Admin ID: " + (admin != null ? admin.getUser_id() : "null"));
        
        if (message == null || message.trim().isEmpty()) {
            System.err.println("AdminChatServlet - Message is empty or null");
            sendError(response, "Nội dung tin nhắn không được để trống!");
            return;
        }
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            System.err.println("AdminChatServlet - User ID is empty or null");
            sendError(response, "User ID không hợp lệ!");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            MessageDAO messageDAO = new MessageDAO();
            
            boolean success = messageDAO.sendMessageFromAdmin(admin.getUser_id(), userId, message.trim());
            
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
            sendError(response, "User ID không hợp lệ!");
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

