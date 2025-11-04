package admin.controller;

import dal.UserDAO;
import model.User; // Import User để lấy thông tin user đang đăng nhập
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Import HttpSession

/**
 * Controller (C)
 * Xử lý việc XÓA người dùng
 * (AdminFilter sẽ bảo vệ URL này)
 */
@WebServlet(name = "DeleteUserServlet", urlPatterns = {"/admin/delete-user"})
public class DeleteUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy user_id cần xóa từ URL (đặt tên là uid)
        String id_raw = request.getParameter("uid");
        int userIdToDelete = 0;
        try {
            userIdToDelete = Integer.parseInt(id_raw);
        } catch (NumberFormatException e) {
            System.err.println("Lỗi ID người dùng không hợp lệ khi xóa: " + e.getMessage());
            // Thiết lập thông báo lỗi và chuyển về trang quản lý
            HttpSession session = request.getSession();
            session.setAttribute("deleteError", "ID người dùng không hợp lệ.");
            response.sendRedirect("manage-users");
            return;
        }

        // 2. Lấy thông tin admin đang đăng nhập từ session
        HttpSession session = request.getSession();
        User loggedInAdmin = (User) session.getAttribute("account");

        // 3. KIỂM TRA QUAN TRỌNG: Không cho phép admin tự xóa chính mình!
        if (loggedInAdmin != null && loggedInAdmin.getUser_id() == userIdToDelete) {
            // Nếu admin cố xóa chính mình, gửi lỗi về trang quản lý
             session.setAttribute("deleteError", "Bạn không thể xóa tài khoản admin đang đăng nhập!");
             response.sendRedirect("manage-users");
             return; // Dừng xử lý
        }

        // 4. Nếu không phải tự xóa, tiến hành gọi DAO để xóa
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.deleteUser(userIdToDelete); // Gọi hàm xóa mới (đã xử lý transaction)

        // 5. Gửi thông báo kết quả về trang quản lý qua session
        if (success) {
            session.setAttribute("deleteSuccess", "Đã xóa người dùng thành công!");
        } else {
             session.setAttribute("deleteError", "Xóa người dùng thất bại! (Kiểm tra lại Log Server để biết chi tiết)");
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