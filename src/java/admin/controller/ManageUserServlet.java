package admin.controller;

import dal.UserDAO; // Import UserDAO
import model.User; // Import model User
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (C)
 * Quản lý việc hiển thị danh sách người dùng trong Admin
 * (AdminFilter sẽ bảo vệ URL này)
 */
@WebServlet(name = "ManageUserServlet", urlPatterns = {"/admin/manage-users"})
public class ManageUserServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        UserDAO userDAO = new UserDAO();

        // 1. Lấy danh sách TẤT CẢ người dùng
        List<User> userList = userDAO.getAllUsers();

        // 2. Đẩy dữ liệu sang View
        request.setAttribute("userList", userList);

        // === SỬA Ở ĐÂY: Thêm dòng này để báo cho sidebar biết trang nào đang active ===
        request.setAttribute("activePage", "users");
        // =======================================================================

        // 3. Chuyển hướng tới trang View
        request.getRequestDispatcher("/admin/manage_users.jsp").forward(request, response);
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