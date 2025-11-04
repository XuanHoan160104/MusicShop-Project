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
 * Đây là Controller (C)
 * Xử lý logic ĐĂNG NHẬP
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // 1. Lấy username và password từ form (login.jsp)
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        
        // 2. (C) gọi (DAO) để kiểm tra
        UserDAO dao = new UserDAO();
        User account = dao.checkLogin(user, pass);
        
        // 3. Xử lý kết quả
        if (account == null) {
            // Nếu 'account' là null -> Đăng nhập thất bại
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            // Đăng nhập thành công
            // 4. Lấy Session
            HttpSession session = request.getSession();
            
            // 5. Lưu thông tin người dùng vào Session (đặt tên là "account")
            session.setAttribute("account", account);
            
            // 6. Chuyển hướng (redirect) về trang chủ
            response.sendRedirect("home");
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