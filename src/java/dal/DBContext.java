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
    
    // Bạn có thể thêm một phương thức main để test kết nối ngay lập lức
    public static void main(String[] args) {
        DBContext db = new DBContext();
        if (db.connection != null) {
            System.out.println("Test kết nối thành công.");
        } else {
            System.out.println("Test kết nối thất bại.");
        }
    }
}