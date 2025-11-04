package admin.controller;

import dal.CategoryDAO;
import dal.ProductDAO;
import model.Category;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "AddProductServlet", urlPatterns = {"/admin/add-product"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class AddProductServlet extends HttpServlet {

    // doGet: Hiển thị form (Giữ nguyên)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categoryList = categoryDAO.getAllCategories();
        request.setAttribute("categoryList", categoryList);
        request.getRequestDispatcher("/admin/add_product.jsp").forward(request, response);
    }

    // doPost: Xử lý form (Sửa lỗi NullPointerException)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String name = "";
        String description = "";
        double price = 0;
        int stock = 0;
        int categoryId = 0;
        String errorMessage = null; // Biến lưu lỗi (nếu có)

        try {
            // === SỬA LỖI: Lấy và kiểm tra giá trị trước khi parse ===
            String priceStr = getStringFromPart(request.getPart("price"));
            String stockStr = getStringFromPart(request.getPart("stock"));
            String categoryIdStr = getStringFromPart(request.getPart("category"));

            name = getStringFromPart(request.getPart("name"));
            description = getStringFromPart(request.getPart("description"));

            // Kiểm tra null hoặc rỗng trước khi parse
            if (priceStr != null && !priceStr.isEmpty()) {
                price = Double.parseDouble(priceStr);
            } else {
                 errorMessage = "Giá sản phẩm không được để trống.";
            }
            if (stockStr != null && !stockStr.isEmpty()) {
                stock = Integer.parseInt(stockStr);
            } else {
                 errorMessage = "Số lượng tồn kho không được để trống.";
            }
             if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                categoryId = Integer.parseInt(categoryIdStr);
            } else {
                 errorMessage = "Danh mục không được để trống.";
            }
            // =======================================================

        } catch (NumberFormatException e) {
            errorMessage = "Lỗi định dạng số (Giá hoặc Tồn kho phải là số hợp lệ).";
            System.err.println("Lỗi parse số khi thêm sản phẩm: " + e.getMessage());
        } catch (IOException | ServletException e) {
             errorMessage = "Lỗi khi đọc dữ liệu form.";
             System.err.println("Lỗi IO/Servlet khi đọc part: " + e.getMessage());
        }

        // Nếu có lỗi parse, quay lại form báo lỗi
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            // Cần tải lại categoryList để hiển thị lại form
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categoryList = categoryDAO.getAllCategories();
            request.setAttribute("categoryList", categoryList);
            request.getRequestDispatcher("/admin/add_product.jsp").forward(request, response);
            return; // Dừng xử lý
        }


        // === Xử lý File Upload (Giữ nguyên) ===
        Part filePart = request.getPart("imageFile");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String dbPath = "images/placeholder.jpg"; // Mặc định

        if (fileName != null && !fileName.isEmpty()) {
            String uploadPath = getServletContext().getRealPath("/images");
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) { Files.createDirectories(path); }
            Path filePath = Paths.get(uploadPath, fileName);
            try (InputStream fileContent = filePart.getInputStream()) {
                Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
                dbPath = "images/" + fileName;
            } catch (IOException e) {
                System.err.println("Lỗi khi lưu file: " + e.getMessage());
                // Có thể thêm thông báo lỗi vào đây
            }
        }

        // === Gọi DAO để lưu vào CSDL (Giữ nguyên) ===
        ProductDAO productDAO = new ProductDAO();
        productDAO.insertProduct(name, description, price, dbPath, stock, categoryId);

        // Chuyển hướng về trang quản lý
        response.sendRedirect("manage-products");
    }

    // Hàm getStringFromPart (Thêm kiểm tra null cho Part)
    private String getStringFromPart(Part part) throws IOException {
        if (part == null) { // Thêm kiểm tra nếu Part không tồn tại
            return null; // Trả về null nếu không tìm thấy part
        }
        try (InputStream inputStream = part.getInputStream();
             Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            // Trả về chuỗi rỗng "" nếu part không có nội dung
            return scanner.hasNext() ? scanner.useDelimiter("\\A").next() : "";
        }
    }
}