package admin.controller;

import dal.ProductDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (C)
 * Xử lý việc XÓA sản phẩm
 * (AdminFilter sẽ bảo vệ URL này)
 */
@WebServlet(name = "DeleteProductServlet", urlPatterns = {"/admin/delete-product"})
public class DeleteProductServlet extends HttpServlet {

    // Nút Xóa thường dùng GET (link) thay vì POST (form)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy product_id (pid) từ URL
        String id_raw = request.getParameter("pid");
        int id = 0;
        try {
            id = Integer.parseInt(id_raw);
        } catch (NumberFormatException e) {
            System.err.println("Lỗi ID sản phẩm không hợp lệ khi xóa: " + e.getMessage());
            // Nếu ID lỗi, cứ chuyển về trang quản lý
            response.sendRedirect("manage-products");
            return;
        }

        // 2. (C) gọi (DAO) để xóa
        ProductDAO productDAO = new ProductDAO();
        productDAO.deleteProduct(id); // Gọi hàm xóa

        // 3. Chuyển hướng (redirect) admin về lại trang quản lý sản phẩm
        response.sendRedirect("manage-products");
    }

    // doPost có thể bỏ trống hoặc cũng redirect về manage-products
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("manage-products");
    }
}