package dal;

import model.Message;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO cho quản lý Messages (Chat)
 */
public class MessageDAO extends DBContext {

    /**
     * Gửi tin nhắn từ user
     */
    public boolean sendMessageFromUser(int userId, String message, Integer productId) {
        String sql = "INSERT INTO messages (user_id, message, product_id, is_from_admin, is_read) VALUES (?, ?, ?, 0, 0)";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            st.setString(2, message);
            if (productId != null) {
                st.setInt(3, productId);
            } else {
                st.setNull(3, java.sql.Types.INTEGER);
            }
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, "Lỗi khi gửi tin nhắn từ user", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Gửi tin nhắn từ admin (trả lời user)
     */
    public boolean sendMessageFromAdmin(int adminId, int userId, String message) {
        String sql = "INSERT INTO messages (user_id, admin_id, message, is_from_admin, is_read) VALUES (?, ?, ?, 1, 0)";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            st.setInt(2, adminId);
            st.setString(3, message);
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, "Lỗi khi gửi tin nhắn từ admin", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Lấy tất cả tin nhắn của một user (cho trang chat của user)
     */
    public List<Message> getUserMessages(int userId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT m.*, u.username as user_name, u2.username as admin_name, p.name as product_name "
                + "FROM messages m "
                + "LEFT JOIN users u ON m.user_id = u.user_id "
                + "LEFT JOIN users u2 ON m.admin_id = u2.user_id "
                + "LEFT JOIN products p ON m.product_id = p.product_id "
                + "WHERE m.user_id = ? "
                + "ORDER BY m.created_at ASC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            rs = st.executeQuery();
            while (rs.next()) {
                Message msg = mapResultSetToMessage(rs);
                list.add(msg);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy tin nhắn của user", ex);
        } finally {
            closeResources(rs, st);
        }
        return list;
    }

    /**
     * Lấy tất cả tin nhắn (cho admin - tất cả users)
     */
    public List<Message> getAllMessagesForAdmin() {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT m.*, u.username as user_name, u.full_name as user_full_name, "
                + "u2.username as admin_name, p.name as product_name "
                + "FROM messages m "
                + "LEFT JOIN users u ON m.user_id = u.user_id "
                + "LEFT JOIN users u2 ON m.admin_id = u2.user_id "
                + "LEFT JOIN products p ON m.product_id = p.product_id "
                + "ORDER BY m.created_at DESC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Message msg = mapResultSetToMessage(rs);
                list.add(msg);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy tất cả tin nhắn cho admin", ex);
        } finally {
            closeResources(rs, st);
        }
        return list;
    }

    /**
     * Lấy danh sách users đã chat (cho admin - để hiển thị danh sách conversation)
     */
    public List<Integer> getUsersWithMessages() {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT DISTINCT user_id FROM messages WHERE user_id IS NOT NULL ORDER BY user_id";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("user_id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy danh sách users", ex);
        } finally {
            closeResources(rs, st);
        }
        return list;
    }

    /**
     * Lấy tin nhắn của một user cụ thể (cho admin xem conversation với user)
     */
    public List<Message> getMessagesByUserId(int userId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT m.*, u.username as user_name, u.full_name as user_full_name, "
                + "u2.username as admin_name, p.name as product_name "
                + "FROM messages m "
                + "LEFT JOIN users u ON m.user_id = u.user_id "
                + "LEFT JOIN users u2 ON m.admin_id = u2.user_id "
                + "LEFT JOIN products p ON m.product_id = p.product_id "
                + "WHERE m.user_id = ? "
                + "ORDER BY m.created_at ASC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            rs = st.executeQuery();
            while (rs.next()) {
                Message msg = mapResultSetToMessage(rs);
                list.add(msg);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy tin nhắn theo user_id", ex);
        } finally {
            closeResources(rs, st);
        }
        return list;
    }

    /**
     * Đánh dấu tin nhắn đã đọc
     */
    public boolean markAsRead(int messageId) {
        String sql = "UPDATE messages SET is_read = 1 WHERE message_id = ?";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, messageId);
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đánh dấu tin nhắn đã đọc", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Đếm số tin nhắn chưa đọc của user
     */
    public int getUnreadCountForUser(int userId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE user_id = ? AND is_read = 0 AND is_from_admin = 1";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đếm tin nhắn chưa đọc", ex);
        } finally {
            closeResources(rs, st);
        }
        return 0;
    }

    /**
     * Đếm số tin nhắn chưa đọc từ users (cho admin)
     */
    public int getUnreadCountForAdmin() {
        String sql = "SELECT COUNT(*) FROM messages WHERE is_read = 0 AND is_from_admin = 0";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đếm tin nhắn chưa đọc cho admin", ex);
        } finally {
            closeResources(rs, st);
        }
        return 0;
    }

    /**
     * Helper method: Map ResultSet to Message
     */
    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message msg = new Message();
        msg.setMessage_id(rs.getInt("message_id"));
        
        int userId = rs.getInt("user_id");
        msg.setUser_id(rs.wasNull() ? null : userId);
        
        int adminId = rs.getInt("admin_id");
        msg.setAdmin_id(rs.wasNull() ? null : adminId);
        
        msg.setMessage(rs.getString("message"));
        
        int productId = rs.getInt("product_id");
        msg.setProduct_id(rs.wasNull() ? null : productId);
        
        msg.setIs_from_admin(rs.getBoolean("is_from_admin"));
        msg.setIs_read(rs.getBoolean("is_read"));
        msg.setCreated_at(rs.getTimestamp("created_at"));
        
        // Thông tin bổ sung
        try {
            msg.setUser_name(rs.getString("user_name"));
            if (rs.getString("user_full_name") != null) {
                msg.setUser_name(rs.getString("user_full_name"));
            }
        } catch (SQLException e) {
            // Ignore if column doesn't exist
        }
        try {
            msg.setAdmin_name(rs.getString("admin_name"));
        } catch (SQLException e) {
            // Ignore if column doesn't exist
        }
        try {
            msg.setProduct_name(rs.getString("product_name"));
        } catch (SQLException e) {
            // Ignore if column doesn't exist
        }
        
        return msg;
    }

    /**
     * Helper method: Close resources
     */
    private void closeResources(ResultSet rs, PreparedStatement st) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) { /* ignored */ }
        try {
            if (st != null) st.close();
        } catch (SQLException e) { /* ignored */ }
    }
}



