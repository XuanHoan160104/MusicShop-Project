package dal;

import model.Category; // Import model mà chúng ta vừa tạo
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Đây là lớp DAO (Data Access Object) cho Category.
 * Nhiệm vụ: Lấy dữ liệu từ bảng 'Categories' trong CSDL.
 * QUAN TRỌNG: Nó "kế thừa" (extends) DBContext để có thể sử dụng biến 'connection'.
 */
public class CategoryDAO extends DBContext {

    /**
     * Hàm lấy TẤT CẢ danh mục từ CSDL
     * @return một danh sách (List) các đối tượng Category
     */
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        // Câu lệnh SQL để lấy tất cả danh mục
        String sql = "SELECT * FROM Categories";

        try {
            // Tạo đối tượng PreparedStatement từ 'connection' (đã được kế thừa)
            PreparedStatement st = connection.prepareStatement(sql);
            
            // Thực thi câu lệnh và nhận kết quả
            ResultSet rs = st.executeQuery();

            // Duyệt qua từng dòng kết quả
            while (rs.next()) {
                // Lấy dữ liệu từ các cột
                int id = rs.getInt("category_id");
                String name = rs.getString("name");
                
                // Tạo một đối tượng Category từ dữ liệu
                Category c = new Category(id, name);
                
                // Thêm đối tượng vào danh sách
                list.add(c);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list; // Trả về danh sách đã chứa đầy đủ dữ liệu
    }
    
    // Bạn có thể thêm một hàm main để test DAO ngay lập tức
    public static void main(String[] args) {
        CategoryDAO dao = new CategoryDAO();
        List<Category> categories = dao.getAllCategories();
        
        if (categories.isEmpty()) {
            System.out.println("Không thể lấy danh sách category.");
        } else {
            System.out.println("Lấy danh sách category thành công:");
            for (Category c : categories) {
                // In ra tên của từng danh mục
                System.out.println("- " + c.getName());
            }
        }
    }
}