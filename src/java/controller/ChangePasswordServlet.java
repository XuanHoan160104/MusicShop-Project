package controller;

import dal.UserDAO;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/change-password"})
public class ChangePasswordServlet extends HttpServlet {

    // Hiển thị form đổi mật khẩu khi truy cập GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        // Chỉ cho phép truy cập nếu đã đăng nhập
        if (session.getAttribute("account") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            request.getRequestDispatcher("change_password.jsp").forward(request, response);
        }
    }

    // Xử lý khi submit form đổi mật khẩu
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("account");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 1. Lấy mật khẩu từ form
        String oldPass = request.getParameter("oldPassword");
        String newPass = request.getParameter("newPassword");
        String confirmPass = request.getParameter("confirmPassword");

        // 2. Kiểm tra mật khẩu mới có khớp không
        if (!newPass.equals(confirmPass)) {
            request.setAttribute("passwordError", "Mật khẩu mới nhập lại không khớp!");
            request.getRequestDispatcher("change_password.jsp").forward(request, response);
            return;
        }

        // 3. Gọi DAO để đổi mật khẩu
        UserDAO userDAO = new UserDAO();
        int result = userDAO.changePassword(user.getUser_id(), oldPass, newPass);

        // 4. Xử lý kết quả
        String message = "";
        String status = "";
        switch (result) {
            case 0: // Thành công
                message = "Đổi mật khẩu thành công!";
                status = "success";
                // Chuyển về trang profile với thông báo thành công
                session.setAttribute("profileMessage", message);
                session.setAttribute("profileStatus", status);
                response.sendRedirect(request.getContextPath() + "/profile");
                return; // Dừng xử lý để tránh forward bên dưới
            case 1: // Mật khẩu cũ sai
                message = "Mật khẩu cũ không đúng!";
                status = "danger";
                break;
            default: // Lỗi SQL hoặc không tìm thấy user
                message = "Đổi mật khẩu thất bại do lỗi hệ thống!";
                status = "danger";
                break;
        }

        // Nếu có lỗi (case 1 hoặc 2), gửi lỗi về lại trang đổi mật khẩu
        request.setAttribute("passwordError", message);
        request.getRequestDispatcher("change_password.jsp").forward(request, response);
    }
}