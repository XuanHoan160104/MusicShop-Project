package admin.controller;

import dal.UserDAO;
import model.User; // Import User để kiểm tra tự sửa vai trò
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Import HttpSession

/**
 * Controller (C)
 * Xử lý việc SỬA VAI TRÒ người dùng
 * (AdminFilter sẽ bảo vệ URL này)
 */
@WebServlet(name = "UpdateUserRoleServlet", urlPatterns = {"/admin/update-role"})
public class UpdateUserRoleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy user_id và vai trò mới từ URL
        String id_raw = request.getParameter("uid");
        String newRole = request.getParameter("role"); // 'admin' hoặc 'customer'
        int userIdToUpdate = 0;

        try {
            userIdToUpdate = Integer.parseInt(id_raw);
        } catch (NumberFormatException e) {
            System.err.println("Lỗi ID người dùng không hợp lệ khi sửa vai trò: " + e.getMessage());
             HttpSession session = request.getSession();
             session.setAttribute("updateRoleError", "ID người dùng không hợp lệ.");
            response.sendRedirect("manage-users");
            return;
        }

        // Kiểm tra newRole hợp lệ
        if (newRole == null || (!newRole.equals("admin") && !newRole.equals("customer"))) {
             HttpSession session = request.getSession();
             session.setAttribute("updateRoleError", "Vai trò mới không hợp lệ.");
            response.sendRedirect("manage-users");
            return;
        }

        // 2. Lấy thông tin admin đang đăng nhập từ session
        HttpSession session = request.getSession();
        User loggedInAdmin = (User) session.getAttribute("account");

        // 3. KIỂM TRA QUAN TRỌNG: Không cho phép admin tự đổi vai trò của chính mình!
        if (loggedInAdmin != null && loggedInAdmin.getUser_id() == userIdToUpdate) {
             session.setAttribute("updateRoleError", "Bạn không thể tự thay đổi vai trò của chính mình!");
             response.sendRedirect("manage-users");
             return; // Dừng xử lý
        }

        // 4. Gọi DAO để cập nhật vai trò
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.updateUserRole(userIdToUpdate, newRole);

        // 5. Gửi thông báo kết quả về trang quản lý
        if (success) {
            session.setAttribute("updateRoleSuccess", "Cập nhật vai trò thành công!");
        } else {
             session.setAttribute("updateRoleError", "Cập nhật vai trò thất bại!");
        }

        // 6. Chuyển hướng về lại trang quản lý người dùng
        response.sendRedirect("manage-users");
    }

    // doPost có thể bỏ trống hoặc cũng redirect về manage-users
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("manage-users");
    }
}