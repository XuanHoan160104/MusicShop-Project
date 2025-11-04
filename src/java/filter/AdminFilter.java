package filter;

import model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Đây là Bộ lọc (Filter)
 * Nhiệm vụ: Chặn tất cả truy cập vào /admin/*
 * VÀ kiểm tra xem người dùng có phải là Admin không.
 */
// Map Filter này với TẤT CẢ các URL bắt đầu bằng /admin/
@WebFilter(filterName = "AdminFilter", urlPatterns = {"/admin/*"})
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        
        // Lấy thông tin user từ session
        User user = (User) session.getAttribute("account");
        
        // Kiểm tra
        if (user != null && user.getRole().equals("admin")) {
            // Nếu là admin, cho phép đi tiếp
            chain.doFilter(request, response);
        } else {
            // Nếu không phải admin (hoặc chưa đăng nhập),
            // "Đá" họ về trang đăng nhập
            res.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    // Các phương thức init và destroy (không cần đụng đến)
    @Override
    public void destroy() {        
    }
    @Override
    public void init(FilterConfig filterConfig) {        
    }
}