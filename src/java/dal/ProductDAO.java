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
                p.setStock_quantity(rs.getInt("stock_quantity")); // <-- Đã có
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
                p.setStock_quantity(rs.getInt("stock_quantity")); // <-- Đã có
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

    // === THAY THẾ HÀM getFeaturedProducts() CŨ BẰNG HÀM MỚI NÀY ===

    /**
     * Lấy các sản phẩm nổi bật (là 8 sản phẩm BÁN CHẠY NHẤT)
     * @return một danh sách (List) các đối tượng Product
     */
    public List<Product> getBestSellingProducts() {
        List<Product> list = new ArrayList<>();
        // Câu SQL này JOIN bảng Products và OrderDetails,
        // đếm tổng số lượng (quantity) đã bán của mỗi sản phẩm,
        // sắp xếp giảm dần, và lấy 8 sản phẩm hàng đầu.
        String sql = "SELECT p.*, SUM(od.quantity) AS total_sold " +
                     "FROM Products p " +
                     "JOIN OrderDetails od ON p.product_id = od.product_id " +
                     "GROUP BY p.product_id " +
                     "ORDER BY total_sold DESC " + // Sắp xếp theo tổng số bán được
                     "LIMIT 8"; // Lấy 8 sản phẩm bán chạy nhất

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
                p.setStock_quantity(rs.getInt("stock_quantity")); // Lấy tồn kho
                p.setCategory_id(rs.getInt("category_id"));
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi getBestSellingProducts", ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

    // === CÁC HÀM CHO QUẢN LÝ SẢN PHẨM (ADMIN) ===
    // (Giữ nguyên các hàm getAllProducts, insertProduct, updateProduct, deleteProduct...)
    public List<Product> getAllProducts() { 
        List<Product> list = new ArrayList<>(); String sql = "SELECT * FROM Products ORDER BY product_id DESC"; PreparedStatement st = null; ResultSet rs = null;
        try { st = connection.prepareStatement(sql); rs = st.executeQuery();
            while (rs.next()) { Product p = new Product(); p.setProduct_id(rs.getInt("product_id")); p.setName(rs.getString("name")); p.setDescription(rs.getString("description")); p.setPrice(rs.getDouble("price")); p.setImage_url(rs.getString("image_url")); p.setStock_quantity(rs.getInt("stock_quantity")); p.setCategory_id(rs.getInt("category_id")); list.add(p); }
        } catch (SQLException ex) { Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi getAllProducts", ex);
        } finally { try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
        return list;
    }
    public void insertProduct(String name, String description, double price, String imageUrl, int stock, int categoryId) { 
        String sql = "INSERT INTO Products (name, description, price, image_url, stock_quantity, category_id) VALUES (?, ?, ?, ?, ?, ?)"; PreparedStatement st = null;
        try { st = connection.prepareStatement(sql); st.setString(1, name); st.setString(2, description); st.setDouble(3, price); st.setString(4, imageUrl); st.setInt(5, stock); st.setInt(6, categoryId); st.executeUpdate();
        } catch (SQLException ex) { Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi insertProduct", ex);
        } finally {  try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
    }
    public void updateProduct(int id, String name, String description, double price, String imageUrl, int stock, int categoryId) { 
        String sql = "UPDATE Products SET name = ?, description = ?, price = ?, image_url = ?, stock_quantity = ?, category_id = ? WHERE product_id = ?"; PreparedStatement st = null;
        try { st = connection.prepareStatement(sql); st.setString(1, name); st.setString(2, description); st.setDouble(3, price); st.setString(4, imageUrl); st.setInt(5, stock); st.setInt(6, categoryId); st.setInt(7, id); st.executeUpdate();
        } catch (SQLException ex) { Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi updateProduct", ex);
        } finally {  try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
    }
    public void deleteProduct(int id) { 
        String deleteOrderDetailsSQL = "DELETE FROM OrderDetails WHERE product_id = ?"; String deleteProductSQL = "DELETE FROM Products WHERE product_id = ?"; PreparedStatement st1 = null; PreparedStatement st2 = null;
        try { st1 = connection.prepareStatement(deleteOrderDetailsSQL); st1.setInt(1, id); st1.executeUpdate();
            st2 = connection.prepareStatement(deleteProductSQL); st2.setInt(1, id); st2.executeUpdate();
        } catch (SQLException ex) { Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi xóa sản phẩm ID " + id, ex);
        } finally {  try { if (st1 != null) st1.close(); } catch (SQLException e) { /* ignored */ }  try { if (st2 != null) st2.close(); } catch (SQLException e) { /* ignored */ } }
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
                p.setStock_quantity(rs.getInt("stock_quantity")); // <-- SỬA Ở ĐÂY: Thêm dòng này
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
    
    
    // === CÁC HÀM CHO PHÂN TRANG (CATEGORY) ===

    public int countProductsByCategoryID(String categoryId) {
        String sql = "SELECT COUNT(*) FROM Products WHERE category_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, categoryId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đếm sản phẩm theo category", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return 0;
    }

    public List<Product> getProductsByCategoryIDPaged(String categoryId, int pageNumber, int pageSize) {
        List<Product> list = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
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
                p.setStock_quantity(rs.getInt("stock_quantity")); // <-- SỬA Ở ĐÂY: Thêm dòng này
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
    
    // === CÁC HÀM CHO PHÂN TRANG TÌM KIẾM ===

    public int countProductsBySearch(String keyword) {
        String sql = "SELECT COUNT(*) FROM Products WHERE name LIKE ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + keyword + "%");
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đếm sản phẩm theo tìm kiếm", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return 0;
    }

    public List<Product> searchByNamePaged(String keyword, int pageNumber, int pageSize) {
        List<Product> list = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT * FROM Products WHERE name LIKE ? LIMIT ? OFFSET ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + keyword + "%");
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
                p.setStock_quantity(rs.getInt("stock_quantity")); // <-- SỬA Ở ĐÂY: Thêm dòng này
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy sản phẩm tìm kiếm phân trang", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

    // Hàm main (giữ nguyên)
    public static void main(String[] args) {
        // ... (code test của bạn) ...
    }
}