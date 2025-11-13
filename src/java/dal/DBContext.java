package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {

    protected Connection connection; // Biến này để các lớp DAO kế thừa và sử dụng

    public DBContext() {
        try {
            // ----- THAY ĐỔI THÔNG SỐ CỦA BẠN TẠI ĐÂY -----
            String dbURL = "jdbc:mysql://localhost:3306/music_shop_db"; // Tên CSDL
            String username = "root"; // User XAMPP mặc định
            String password = "";     // Password XAMPP mặc định là rỗng
            // ------------------------------------------

            String driver = "com.mysql.cj.jdbc.Driver"; // Driver cho MySQL
            
            Class.forName(driver); // Nạp driver
            connection = DriverManager.getConnection(dbURL, username, password); // Mở kết nối
            
            System.out.println("KẾT NỐI DATABASE THÀNH CÔNG!"); // In ra để kiểm tra

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("KẾT NỐI DATABASE THẤT BẠI: " + ex.getMessage());
        }
    }

    // ✅ Thêm phương thức static để gọi trong try-with-resources
    public static Connection getConnection() throws SQLException {
        String dbURL = "jdbc:mysql://localhost:3306/music_shop_db";
        String username = "root";
        String password = "";
        return DriverManager.getConnection(dbURL, username, password);
    }

    // Test kết nối
    public static void main(String[] args) {
        try (Connection con = DBContext.getConnection()) {
            System.out.println("Test kết nối thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}
