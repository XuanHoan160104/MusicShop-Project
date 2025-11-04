package dal;

import model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// === THÊM IMPORT MỚI ===
import java.util.ArrayList;
import java.util.List;
// ======================
import java.util.logging.Level;
import java.util.logging.Logger;
// Import thư viện băm mật khẩu
import org.mindrot.jbcrypt.BCrypt;

/**
 * Lớp DAO (Data Access Object) cho User
 * Xử lý đăng nhập và đăng ký
 */
public class UserDAO extends DBContext {

    /**
     * Kiểm tra thông tin đăng nhập.
     * @param username Tên đăng nhập
     * @param rawPassword Mật khẩu GỐC (chưa băm) mà người dùng nhập
     * @return Đối tượng User nếu đăng nhập thành công, ngược lại trả về null
     */
    public User checkLogin(String username, String rawPassword) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, username);
            rs = st.executeQuery();

            if (rs.next()) {
                String hashedPasswordFromDB = rs.getString("password");
                try {
                     if (BCrypt.checkpw(rawPassword, hashedPasswordFromDB)) {
                        User user = new User();
                        user.setUser_id(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setEmail(rs.getString("email"));
                        user.setFull_name(rs.getString("full_name"));
                        user.setAddress(rs.getString("address"));
                        user.setRole(rs.getString("role"));
                        return user;
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Lỗi BCrypt khi kiểm tra user '" + username + "': " + e.getMessage());
                    return null;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi SQL khi checkLogin", ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return null;
    }

    /**
     * Kiểm tra xem username đã tồn tại trong CSDL chưa
     * @param username Tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, username);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi SQL khi checkUsernameExists", ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return false;
    }

    /**
     * Đăng ký một người dùng mới (đã băm mật khẩu)
     * @param username
     * @param rawPassword Mật khẩu GỐC (sẽ được băm)
     * @param email
     * @param fullName
     * @param address
     */
    public void registerUser(String username, String rawPassword, String email, String fullName, String address) {
        String sql = "INSERT INTO Users (username, password, email, full_name, address, role) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            st.setString(1, username);
            st.setString(2, hashedPassword);
            st.setString(3, email);
            st.setString(4, fullName);
            st.setString(5, address);
            if (username.equals("admin")) {
                 st.setString(6, "admin");
            } else {
                 st.setString(6, "customer");
            }
            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi SQL khi registerUser", ex);
        } finally {
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // === CÁC HÀM CHO QUẢN LÝ NGƯỜI DÙNG ===

    /**
     * Lấy TẤT CẢ người dùng từ CSDL
     * @return Danh sách tất cả người dùng
     */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY user_id ASC";
         PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUser_id(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setFull_name(rs.getString("full_name"));
                u.setAddress(rs.getString("address"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi SQL khi getAllUsers", ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

    /**
     * Xóa một người dùng và TẤT CẢ dữ liệu liên quan (Đơn hàng, Chi tiết đơn hàng).
     * @param userId ID của người dùng cần xóa.
     * @return true nếu xóa thành công, false nếu có lỗi.
     */
    public boolean deleteUser(int userId) {
        boolean success = false;
        PreparedStatement stFindOrders = null;
        PreparedStatement stDeleteDetails = null;
        PreparedStatement stOrders = null;
        PreparedStatement stUser = null;
        ResultSet rsOrders = null;

        try {
            connection.setAutoCommit(false);

            // Bước 1: Xóa Chi tiết Đơn hàng
            String findOrdersSQL = "SELECT order_id FROM Orders WHERE user_id = ?";
            stFindOrders = connection.prepareStatement(findOrdersSQL);
            stFindOrders.setInt(1, userId);
            rsOrders = stFindOrders.executeQuery();

            String deleteDetailsSQL = "DELETE FROM OrderDetails WHERE order_id = ?";
            stDeleteDetails = connection.prepareStatement(deleteDetailsSQL);
            boolean hasOrders = false;
            while (rsOrders.next()) {
                hasOrders = true;
                int orderId = rsOrders.getInt("order_id");
                stDeleteDetails.setInt(1, orderId);
                stDeleteDetails.addBatch();
            }
            if (hasOrders) {
                 stDeleteDetails.executeBatch();
                 System.out.println("Đã xóa các chi tiết đơn hàng liên quan đến user ID: " + userId);
            }

            // Bước 2: Xóa Đơn hàng
            String deleteOrdersSQL = "DELETE FROM Orders WHERE user_id = ?";
            stOrders = connection.prepareStatement(deleteOrdersSQL);
            stOrders.setInt(1, userId);
            stOrders.executeUpdate();
            System.out.println("Đã xóa các đơn hàng của user ID: " + userId);

            // Bước 3: Xóa Người dùng
            String deleteUserSQL = "DELETE FROM Users WHERE user_id = ?";
            stUser = connection.prepareStatement(deleteUserSQL);
            stUser.setInt(1, userId);
            int rowsAffected = stUser.executeUpdate();

            connection.commit();
            success = (rowsAffected > 0);

        } catch (SQLException ex) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException e) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi khi rollback xóa user", e);
            }
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi nghiêm trọng khi xóa user ID " + userId, ex);
            success = false;
        } finally {
            try {
                if (rsOrders != null) rsOrders.close();
                if (stFindOrders != null) stFindOrders.close();
                if (stDeleteDetails != null) stDeleteDetails.close();
                if (stOrders != null) stOrders.close();
                if (stUser != null) stUser.close();
                if (connection != null) connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đóng tài nguyên hoặc bật lại autoCommit", ex);
            }
        }
        return success;
    }

    // === THÊM HÀM SỬA VAI TRÒ USER ===

    /**
     * Cập nhật vai trò (role) của một người dùng.
     * @param userId ID của người dùng cần cập nhật.
     * @param newRole Vai trò mới ('admin' hoặc 'customer').
     * @return true nếu cập nhật thành công, false nếu có lỗi.
     */
    public boolean updateUserRole(int userId, String newRole) {
        if (!"admin".equals(newRole) && !"customer".equals(newRole)) {
            System.err.println("Vai trò mới không hợp lệ: " + newRole);
            return false;
        }
        String sql = "UPDATE Users SET role = ? WHERE user_id = ?";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, newRole);
            st.setInt(2, userId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi khi cập nhật vai trò cho user ID " + userId, ex);
            return false;
        } finally {
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // === THÊM HÀM CẬP NHẬT PROFILE ===

    /**
     * Cập nhật thông tin cơ bản của người dùng (không bao gồm mật khẩu, vai trò).
     * @param userId ID người dùng cần cập nhật.
     * @param fullName Họ tên mới.
     * @param email Email mới.
     * @param address Địa chỉ mới.
     * @return true nếu cập nhật thành công, false nếu có lỗi.
     */
    public boolean updateUserProfile(int userId, String fullName, String email, String address) {
        String sql = "UPDATE Users SET full_name = ?, email = ?, address = ? WHERE user_id = ?";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, fullName);
            st.setString(2, email);
            st.setString(3, address);
            st.setInt(4, userId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi khi cập nhật profile cho user ID " + userId, ex);
            return false;
        } finally {
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // === THÊM HÀM ĐỔI MẬT KHẨU ===

    /**
     * Thay đổi mật khẩu cho người dùng.
     * @param userId ID của người dùng.
     * @param oldRawPassword Mật khẩu cũ (chưa băm) người dùng nhập.
     * @param newRawPassword Mật khẩu mới (chưa băm) người dùng nhập.
     * @return 0 nếu thành công, 1 nếu mật khẩu cũ sai, 2 nếu có lỗi SQL hoặc không tìm thấy user.
     */
    public int changePassword(int userId, String oldRawPassword, String newRawPassword) {
        String sqlSelect = "SELECT password FROM Users WHERE user_id = ?";
        String sqlUpdate = "UPDATE Users SET password = ? WHERE user_id = ?";
        PreparedStatement stSelect = null;
        PreparedStatement stUpdate = null;
        ResultSet rs = null;

        try {
            // Bước 1: Lấy mật khẩu đã băm hiện tại
            stSelect = connection.prepareStatement(sqlSelect);
            stSelect.setInt(1, userId);
            rs = stSelect.executeQuery();

            if (rs.next()) {
                String hashedPasswordFromDB = rs.getString("password");

                // Bước 2: Kiểm tra mật khẩu cũ
                if (BCrypt.checkpw(oldRawPassword, hashedPasswordFromDB)) {
                    // Bước 3: Băm mật khẩu mới và cập nhật
                    String newHashedPassword = BCrypt.hashpw(newRawPassword, BCrypt.gensalt());
                    stUpdate = connection.prepareStatement(sqlUpdate);
                    stUpdate.setString(1, newHashedPassword);
                    stUpdate.setInt(2, userId);
                    stUpdate.executeUpdate();
                    return 0; // Thành công
                } else {
                    return 1; // Mật khẩu cũ sai
                }
            } else {
                return 2; // Không tìm thấy user ID
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Lỗi SQL khi đổi mật khẩu cho user ID " + userId, ex);
            return 2; // Lỗi SQL
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stSelect != null) stSelect.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stUpdate != null) stUpdate.close(); } catch (SQLException e) { /* ignored */ }
        }
    }


    // Hàm main để test DAO
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        // --- Test 1: Đăng ký user ADMIN ---
        // === ĐÃ GHI CHÚ LẠI 3 DÒNG NÀY ===
        // System.out.println("Đang đăng ký user 'admin'...");
        // dao.registerUser("admin", "admin", "admin@shop.com", "Quản Trị Viên", "123 Admin Street");
        // System.out.println("Đăng ký xong.");
        // ================================

        // --- Test 2: Kiểm tra đăng nhập ADMIN (ĐÚNG) ---
        System.out.println("Kiểm tra đăng nhập 'admin' với pass 'admin':");
        User user1 = dao.checkLogin("admin", "admin");
        if (user1 != null) {
            System.out.println("Đăng nhập thành công! Chào " + user1.getFull_name() + ", Role: " + user1.getRole());
        } else {
            System.out.println("Đăng nhập thất bại.");
        }

        // --- Test 3: Kiểm tra đăng nhập ADMIN (SAI) ---
        System.out.println("Kiểm tra đăng nhập 'admin' với pass 'saimatkhau':");
        User user2 = dao.checkLogin("admin", "saimatkhau");
        if (user2 != null) {
            System.out.println("Đăng nhập thành công! Chào " + user2.getFull_name());
        } else {
            System.out.println("Đăng nhập thất bại.");
        }

        // --- Test 4: Lấy tất cả user ---
         System.out.println("\nKiểm tra hàm getAllUsers:");
         List<User> allUsers = dao.getAllUsers();
         for (User u : allUsers) {
             System.out.println(u.getUser_id() + ": " + u.getUsername() + " (" + u.getRole() + ")");
         }

         // --- Test 5: Xóa user (Ví dụ: xóa user có ID = ?) ---
         // !!! CẨN THẬN KHI CHẠY TEST NÀY !!!
         // int userIdToDelete = ?;
         // System.out.println("\nĐang thử xóa user ID: " + userIdToDelete);
         // boolean deleted = dao.deleteUser(userIdToDelete);
         // if (deleted) { System.out.println("Xóa thành công."); } else { System.out.println("Xóa thất bại."); }

        // --- Test 6: Sửa vai trò (Ví dụ: đổi user ID ? thành admin) ---
        // int userIdToUpdate = ?;
        // String roleToSet = "admin"; // Hoặc "customer"
        // System.out.println("\nĐang thử đổi vai trò user ID " + userIdToUpdate + " thành " + roleToSet);
        // boolean updated = dao.updateUserRole(userIdToUpdate, roleToSet);
        // if (updated) { System.out.println("Cập nhật vai trò thành công."); } else { System.out.println("Cập nhật vai trò thất bại."); }
       
        // --- Test 7: Cập nhật profile (Ví dụ: user ID ?) ---
        // int profileUserId = ?;
        // System.out.println("\nĐang thử cập nhật profile user ID " + profileUserId);
        // boolean profileUpdated = dao.updateUserProfile(profileUserId, "Tên Mới", "emailmoi@test.com", "Địa Chỉ Mới");
        // if (profileUpdated) { System.out.println("Cập nhật profile thành công."); } else { System.out.println("Cập nhật profile thất bại."); }
        
        // --- Test 8: Đổi mật khẩu (Ví dụ: user ID ?, pass cũ '123456', pass mới 'abcdef') ---
        // int changePassUserId = ?;
        // String oldPass = "123456";
        // String newPass = "abcdef";
        // System.out.println("\nĐang thử đổi mật khẩu user ID " + changePassUserId);
        // int changeResult = dao.changePassword(changePassUserId, oldPass, newPass);
        // if (changeResult == 0) { System.out.println("Đổi mật khẩu thành công."); }
        // else if (changeResult == 1) { System.out.println("Mật khẩu cũ không đúng."); }
        // else { System.out.println("Đổi mật khẩu thất bại (Lỗi SQL hoặc user không tồn tại)."); }
    }
}