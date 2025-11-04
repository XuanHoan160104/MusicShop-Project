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

/**
 * Controller (C)
 * Xử lý việc cập nhật thông tin cá nhân (Họ tên, Email, Địa chỉ)
 */
@WebServlet(name = "UpdateProfileServlet", urlPatterns = {"/update-profile"})
public class UpdateProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // Để lấy tiếng Việt
        HttpSession session = request.getSession();

        // 1. Kiểm tra đăng nhập
        User user = (User) session.getAttribute("account");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Lấy thông tin mới từ form (profile.jsp)
        String fullName = request.getParameter("fullname");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        // 3. Gọi DAO để cập nhật
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.updateUserProfile(user.getUser_id(), fullName, email, address);

        // 4. Cập nhật lại thông tin trong session và gửi thông báo
        if (success) {
            // Cập nhật lại đối tượng user trong session
            user.setFull_name(fullName);
            user.setEmail(email);
            user.setAddress(address);
            session.setAttribute("account", user); // Lưu lại user đã cập nhật
            session.setAttribute("profileMessage", "Cập nhật thông tin thành công!");
            session.setAttribute("profileStatus", "success"); // Để JSP biết là thành công
        } else {
            // Lỗi này thường do cố tình đổi email thành email đã tồn tại
            session.setAttribute("profileMessage", "Cập nhật thất bại! Email có thể đã tồn tại.");
            session.setAttribute("profileStatus", "danger"); // Để JSP biết là lỗi
        }

        // 5. Chuyển hướng về lại trang profile
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    // Nếu người dùng gõ thẳng URL /update-profile, cứ đá về trang profile
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         response.sendRedirect(request.getContextPath() + "/profile");
    }
}