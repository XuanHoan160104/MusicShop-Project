package admin.controller;

import dal.CategoryDAO;
import dal.ProductDAO;
import model.Category;
import model.Product;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner; // Import Scanner

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig; // <-- THÊM ANNOTATION
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part; // <-- THÊM IMPORT

/**
 * Controller (C)
 * Xử lý việc SỬA thông tin sản phẩm
 * ĐÃ NÂNG CẤP: Hỗ trợ Upload File Ảnh
 */
@WebServlet(name = "EditProductServlet", urlPatterns = {"/admin/edit-product"})
@MultipartConfig( // <-- THÊM ANNOTATION
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class EditProductServlet extends HttpServlet {

    // doGet: Hiển thị form (Giữ nguyên)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String id_raw = request.getParameter("pid");

        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categoryList = categoryDAO.getAllCategories();
        request.setAttribute("categoryList", categoryList);

        ProductDAO productDAO = new ProductDAO();
        try {
            Product productToEdit = productDAO.getProductByID(id_raw);
            request.setAttribute("product", productToEdit);
            request.getRequestDispatcher("/admin/edit_product.jsp").forward(request, response);
        } catch (NumberFormatException e) {
             response.sendRedirect("manage-products");
        }
    }

    // doPost: Xử lý form (Thay đổi hoàn toàn)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // === BƯỚC 1: LẤY CÁC TRƯỜNG TEXT TỪ FORM MULTIPART ===
        int id = 0;
        double price = 0;
        int stock = 0;
        int categoryId = 0;
        String name = "";
        String description = "";
        String oldImageUrl = ""; // Lấy đường dẫn ảnh cũ
        
        try {
            id = Integer.parseInt(getStringFromPart(request.getPart("id")));
            price = Double.parseDouble(getStringFromPart(request.getPart("price")));
            stock = Integer.parseInt(getStringFromPart(request.getPart("stock")));
            categoryId = Integer.parseInt(getStringFromPart(request.getPart("category")));
            name = getStringFromPart(request.getPart("name"));
            description = getStringFromPart(request.getPart("description"));
            oldImageUrl = getStringFromPart(request.getPart("oldImageUrl")); // Lấy ảnh cũ từ input ẩn
        } catch (Exception e) {
            System.err.println("Lỗi parse dữ liệu khi sửa sản phẩm: " + e.getMessage());
        }

        // === BƯỚC 2: XỬ LÝ FILE UPLOAD (NẾU CÓ) ===
        Part filePart = request.getPart("imageFile");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String dbPath; // Đường dẫn sẽ lưu vào CSDL

        if (fileName != null && !fileName.isEmpty()) {
            // == Người dùng ĐÃ upload ảnh mới ==
            String uploadPath = getServletContext().getRealPath("/images");
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path filePath = Paths.get(uploadPath, fileName);
            try (InputStream fileContent = filePart.getInputStream()) {
                Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Upload file mới thành công: " + filePath.toString());
            } catch (IOException e) {
                System.err.println("Lỗi khi lưu file mới: " + e.getMessage());
            }
            dbPath = "images/" + fileName; // Dùng ảnh mới
        } else {
            // == Người dùng KHÔNG upload ảnh mới ==
            // Giữ nguyên ảnh cũ
            dbPath = oldImageUrl;
        }

        // === BƯỚC 3: GỌI DAO ĐỂ CẬP NHẬT CSDL ===
        ProductDAO productDAO = new ProductDAO();
        productDAO.updateProduct(id, name, description, price, dbPath, stock, categoryId);

        // 4. Chuyển hướng về trang quản lý
        response.sendRedirect("manage-products");
    }

    /**
     * Hàm tiện ích để đọc giá trị String từ một Part (trong form multipart).
     */
    private String getStringFromPart(Part part) throws IOException {
        try (InputStream inputStream = part.getInputStream();
             Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            return scanner.hasNext() ? scanner.useDelimiter("\\A").next() : "";
        }
    }
}