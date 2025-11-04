package dal;

import model.Product; // Import model Product
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp DAO (Data Access Object) cho Product
 * Kế thừa DBContext để dùng 'connection'
 */
public class ProductDAO extends DBContext {

    /**
     * Lấy tất cả sản phẩm thuộc về một danh mục
     * @param categoryId ID của danh mục cần lấy sản phẩm
     * @return một danh sách (List) các đối tượng Product
     */
    public List<Product> getProductsByCategoryID(String categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE category_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, categoryId);
            rs = st.executeQuery();

            while (rs.next()) {
                Product p = new Product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setImage_url(rs.getString("image_url"));
                p.setStock_quantity(rs.getInt("stock_quantity"));
                p.setCategory_id(rs.getInt("category_id"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi getProductsByCategoryID", ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

    /**
     * Lấy MỘT sản phẩm dựa trên ID của nó
     * @param productID ID của sản phẩm cần tìm
     * @return một đối tượng Product, hoặc null nếu không tìm thấy
     */
    public Product getProductByID(String productID) {
        String sql = "SELECT * FROM Products WHERE product_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, Integer.parseInt(productID));
            rs = st.executeQuery();

            if (rs.next()) {
                Product p = new Product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setImage_url(rs.getString("image_url"));
                p.setStock_quantity(rs.getInt("stock_quantity"));
                p.setCategory_id(rs.getInt("category_id"));
                return p;
            }
        } catch (SQLException | NumberFormatException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi getProductByID", ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return null;
    }

    /**
     * Lấy các sản phẩm nổi bật (ví dụ: 8 sản phẩm đầu tiên)
     * @return một danh sách (List) các đối tượng Product
     */
    public List<Product> getFeaturedProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products LIMIT 8";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setImage_url(rs.getString("image_url"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi getFeaturedProducts", ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

    // === CÁC HÀM CHO QUẢN LÝ SẢN PHẨM (ADMIN) ===

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products ORDER BY product_id DESC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setImage_url(rs.getString("image_url"));
                p.setStock_quantity(rs.getInt("stock_quantity"));
                p.setCategory_id(rs.getInt("category_id"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi getAllProducts", ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

    public void insertProduct(String name, String description, double price, String imageUrl, int stock, int categoryId) {
        String sql = "INSERT INTO Products (name, description, price, image_url, stock_quantity, category_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, name);
            st.setString(2, description);
            st.setDouble(3, price);
            st.setString(4, imageUrl);
            st.setInt(5, stock);
            st.setInt(6, categoryId);
            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi insertProduct", ex);
        } finally {
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    public void updateProduct(int id, String name, String description, double price, String imageUrl, int stock, int categoryId) {
        String sql = "UPDATE Products SET name = ?, description = ?, price = ?, image_url = ?, stock_quantity = ?, category_id = ? WHERE product_id = ?";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, name);
            st.setString(2, description);
            st.setDouble(3, price);
            st.setString(4, imageUrl);
            st.setInt(5, stock);
            st.setInt(6, categoryId);
            st.setInt(7, id);
            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi updateProduct", ex);
        } finally {
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    public void deleteProduct(int id) {
        // Cần xử lý OrderDetails trước khi xóa Product
        String deleteOrderDetailsSQL = "DELETE FROM OrderDetails WHERE product_id = ?";
        String deleteProductSQL = "DELETE FROM Products WHERE product_id = ?";
        PreparedStatement st1 = null;
        PreparedStatement st2 = null;
        try {
            // Nên dùng transaction ở đây, nhưng tạm thời xóa 2 bước
            st1 = connection.prepareStatement(deleteOrderDetailsSQL);
            st1.setInt(1, id);
            st1.executeUpdate(); // Xóa các chi tiết đơn hàng liên quan
            
            st2 = connection.prepareStatement(deleteProductSQL);
            st2.setInt(1, id);
            st2.executeUpdate(); // Xóa sản phẩm
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi xóa sản phẩm ID " + id, ex);
        } finally {
             try { if (st1 != null) st1.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st2 != null) st2.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // === HÀM TÌM KIẾM SẢN PHẨM ===
    
    public List<Product> searchByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE name LIKE ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + keyword + "%");
            rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setImage_url(rs.getString("image_url"));
                p.setCategory_id(rs.getInt("category_id"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi searchByName", ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }
    
    
    // === THÊM 2 HÀM MỚI CHO PHÂN TRANG ===

    /**
     * Đếm tổng số sản phẩm thuộc một danh mục.
     * @param categoryId ID của danh mục.
     * @return Tổng số sản phẩm.
     */
    public int countProductsByCategoryID(String categoryId) {
        String sql = "SELECT COUNT(*) FROM Products WHERE category_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, categoryId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Trả về tổng số đếm được
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đếm sản phẩm theo category", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return 0;
    }

    /**
     * Lấy sản phẩm thuộc một danh mục theo phân trang.
     * @param categoryId ID của danh mục.
     * @param pageNumber Trang hiện tại (bắt đầu từ 1).
     * @param pageSize Số lượng sản phẩm mỗi trang (ví dụ: 9).
     * @return Danh sách sản phẩm của trang đó.
     */
    public List<Product> getProductsByCategoryIDPaged(String categoryId, int pageNumber, int pageSize) {
        List<Product> list = new ArrayList<>();
        // Tính toán offset (vị trí bắt đầu lấy)
        // Trang 1: (1-1) * 9 = 0 (Lấy từ 0)
        // Trang 2: (2-1) * 9 = 9 (Lấy từ 9)
        int offset = (pageNumber - 1) * pageSize;
        
        // Dùng LIMIT (số lượng) và OFFSET (bỏ qua bao nhiêu)
        String sql = "SELECT * FROM Products WHERE category_id = ? LIMIT ? OFFSET ?";
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement(sql);
            st.setString(1, categoryId);
            st.setInt(2, pageSize);
            st.setInt(3, offset);
            rs = st.executeQuery();

            while (rs.next()) {
                Product p = new Product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setImage_url(rs.getString("image_url"));
                p.setCategory_id(rs.getInt("category_id"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy sản phẩm phân trang", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

    // Hàm main để test DAO
    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();
        // Thử lấy sản phẩm của danh mục '1' (là 'Guitar' trong CSDL mẫu)
        List<Product> products = dao.getProductsByCategoryID("1");

        System.out.println("Kiểm tra DAO (lấy sản phẩm theo danh mục 1 - Guitar):");
        if (products.isEmpty()) {
            System.out.println("Không tìm thấy sản phẩm nào.");
        } else {
            for(Product p : products) {
                System.out.println("- " + p.getName()); // In tên sản phẩm ra
            }
        }
    }
}