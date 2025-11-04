package controller;

import dal.UserDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Đây là Controller (C)
 * Xử lý logic ĐĂNG KÝ
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    
    // Xử lý khi người dùng nhấn nút "Đăng ký" (method="post")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        // Đặt UTF-8 để nhận Tiếng Việt từ form
        request.setCharacterEncoding("UTF-8"); 

        // 1. Lấy dữ liệu từ form (register.jsp)
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        String re_pass = request.getParameter("re_password");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullname");
        String address = request.getParameter("address");

        // 2. (C) gọi (DAO)
        UserDAO dao = new UserDAO();

        // 3. (C) Xử lý logic
        
        // Kiểm tra 1: Mật khẩu có khớp không?
        if (!pass.equals(re_pass)) {
            request.setAttribute("error", "Mật khẩu nhập lại không khớp!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } else {
            // Kiểm tra 2: Tên đăng nhập đã tồn tại chưa?
            boolean userExists = dao.checkUsernameExists(user);
            if (userExists) {
                // Tên đăng nhập đã tồn tại
                request.setAttribute("error", "Tên đăng nhập '" + user + "' đã tồn tại!");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            } else {
                // Thành công! Tiến hành đăng ký
                dao.registerUser(user, pass, email, fullName, address);
                
                // Gửi 1 thông báo thành công sang trang login.jsp
                request.setAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        }
    }

    // Xử lý khi người dùng gõ thẳng URL /register (method="get")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chỉ cần chuyển hướng họ đến trang register.jsp
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}