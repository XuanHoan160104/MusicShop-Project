package dal;

import model.Voucher;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO cho quản lý Vouchers (Mã giảm giá)
 */
public class VoucherDAO extends DBContext {

    /**
     * Lấy tất cả vouchers (cho admin)
     */
    public List<Voucher> getAllVouchers() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers ORDER BY created_at DESC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Voucher v = mapResultSetToVoucher(rs);
                list.add(v);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy tất cả vouchers", ex);
        } finally {
            closeResources(rs, st);
        }
        return list;
    }

    /**
     * Lấy voucher theo ID
     */
    public Voucher getVoucherById(int voucherId) {
        String sql = "SELECT * FROM vouchers WHERE voucher_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, voucherId);
            rs = st.executeQuery();
            if (rs.next()) {
                return mapResultSetToVoucher(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy voucher theo ID", ex);
        } finally {
            closeResources(rs, st);
        }
        return null;
    }

    /**
     * Lấy voucher theo code
     */
    public Voucher getVoucherByCode(String code) {
        String sql = "SELECT * FROM vouchers WHERE code = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, code);
            rs = st.executeQuery();
            if (rs.next()) {
                return mapResultSetToVoucher(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy voucher theo code", ex);
        } finally {
            closeResources(rs, st);
        }
        return null;
    }

    /**
     * Validate và lấy voucher có thể sử dụng (cho checkout)
     * Kiểm tra: code tồn tại, active, trong thời gian hiệu lực, được publish trong news,
     * và user chưa sử dụng mã này (tránh lạm dụng bằng cách đặt nhiều đơn hàng nhỏ)
     */
    public Voucher validateVoucherForUse(String code, int userId) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        // Kiểm tra voucher có tồn tại và đang active
        Voucher voucher = getVoucherByCode(code);
        if (voucher == null || !voucher.isIs_active()) {
            return null;
        }

        // Kiểm tra thời gian hiệu lực
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        if (voucher.getStart_date().after(now) || voucher.getEnd_date().before(now)) {
            return null;
        }

        // QUAN TRỌNG: Kiểm tra voucher có được publish trong news không
        // Người dùng chỉ có thể dùng voucher khi admin đã đăng trong tin tức
        String checkNewsSql = "SELECT COUNT(*) FROM news WHERE voucher_id = ? AND is_published = 1";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(checkNewsSql);
            st.setInt(1, voucher.getVoucher_id());
            rs = st.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Voucher chưa được đăng trong tin tức
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi kiểm tra news cho voucher", ex);
            return null;
        } finally {
            closeResources(rs, st);
        }

        // QUAN TRỌNG: Kiểm tra user đã sử dụng mã này chưa (tránh lạm dụng)
        // Mỗi user chỉ được sử dụng một mã giảm giá một lần
        if (hasUserUsedVoucher(voucher.getVoucher_id(), userId)) {
            return null; // User đã sử dụng mã này rồi
        }

        return voucher;
    }

    /**
     * Tính số tiền giảm giá
     */
    public double calculateDiscount(Voucher voucher, double totalAmount) {
        if (voucher == null) {
            return 0.0;
        }

        if ("percentage".equalsIgnoreCase(voucher.getDiscount_type())) {
            // Giảm theo phần trăm
            double discount = totalAmount * voucher.getDiscount_value() / 100.0;
            return Math.min(discount, totalAmount); // Không giảm quá tổng tiền
        } else if ("fixed".equalsIgnoreCase(voucher.getDiscount_type())) {
            // Giảm cố định
            return Math.min(voucher.getDiscount_value(), totalAmount); // Không giảm quá tổng tiền
        }

        return 0.0;
    }

    /**
     * Kiểm tra voucher đã được sử dụng bởi user này chưa
     * Mỗi user chỉ được sử dụng một mã giảm giá một lần để tránh lạm dụng
     */
    public boolean hasUserUsedVoucher(int voucherId, int userId) {
        String sql = "SELECT COUNT(*) FROM voucher_usage WHERE voucher_id = ? AND user_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, voucherId);
            st.setInt(2, userId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Đã sử dụng nếu count > 0
            }
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi kiểm tra voucher đã dùng", ex);
        } finally {
            closeResources(rs, st);
        }
        return false;
    }

    /**
     * Lưu voucher usage (khi đơn hàng được tạo)
     */
    public boolean saveVoucherUsage(int voucherId, int orderId, int userId, double discountAmount) {
        String sql = "INSERT INTO voucher_usage (voucher_id, order_id, user_id, discount_amount) VALUES (?, ?, ?, ?)";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, voucherId);
            st.setInt(2, orderId);
            st.setInt(3, userId);
            st.setDouble(4, discountAmount);
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lưu voucher usage", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Tạo voucher mới (admin)
     */
    public boolean createVoucher(Voucher voucher) {
        String sql = "INSERT INTO vouchers (code, discount_type, discount_value, start_date, end_date, description, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, voucher.getCode());
            st.setString(2, voucher.getDiscount_type());
            st.setDouble(3, voucher.getDiscount_value());
            st.setTimestamp(4, voucher.getStart_date());
            st.setTimestamp(5, voucher.getEnd_date());
            st.setString(6, voucher.getDescription());
            st.setBoolean(7, voucher.isIs_active());
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi tạo voucher", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Cập nhật voucher (admin)
     */
    public boolean updateVoucher(Voucher voucher) {
        String sql = "UPDATE vouchers SET code = ?, discount_type = ?, discount_value = ?, start_date = ?, end_date = ?, description = ?, is_active = ? WHERE voucher_id = ?";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, voucher.getCode());
            st.setString(2, voucher.getDiscount_type());
            st.setDouble(3, voucher.getDiscount_value());
            st.setTimestamp(4, voucher.getStart_date());
            st.setTimestamp(5, voucher.getEnd_date());
            st.setString(6, voucher.getDescription());
            st.setBoolean(7, voucher.isIs_active());
            st.setInt(8, voucher.getVoucher_id());
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi cập nhật voucher", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Xóa voucher (admin)
     */
    public boolean deleteVoucher(int voucherId) {
        String sql = "DELETE FROM vouchers WHERE voucher_id = ?";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, voucherId);
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi xóa voucher", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Lấy các vouchers đang active và trong thời gian hiệu lực (cho trang tin tức)
     */
    public List<Voucher> getActivePublishedVouchers() {
        List<Voucher> list = new ArrayList<>();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        String sql = "SELECT DISTINCT v.* FROM vouchers v "
                + "INNER JOIN news n ON v.voucher_id = n.voucher_id "
                + "WHERE v.is_active = 1 "
                + "AND n.is_published = 1 "
                + "AND v.start_date <= ? "
                + "AND v.end_date >= ? "
                + "ORDER BY v.created_at DESC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setTimestamp(1, now);
            st.setTimestamp(2, now);
            rs = st.executeQuery();
            while (rs.next()) {
                Voucher v = mapResultSetToVoucher(rs);
                list.add(v);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VoucherDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy active published vouchers", ex);
        } finally {
            closeResources(rs, st);
        }
        return list;
    }

    /**
     * Helper method: Map ResultSet to Voucher
     */
    private Voucher mapResultSetToVoucher(ResultSet rs) throws SQLException {
        Voucher v = new Voucher();
        v.setVoucher_id(rs.getInt("voucher_id"));
        v.setCode(rs.getString("code"));
        v.setDiscount_type(rs.getString("discount_type"));
        v.setDiscount_value(rs.getDouble("discount_value"));
        v.setStart_date(rs.getTimestamp("start_date"));
        v.setEnd_date(rs.getTimestamp("end_date"));
        v.setDescription(rs.getString("description"));
        v.setIs_active(rs.getBoolean("is_active"));
        v.setCreated_at(rs.getTimestamp("created_at"));
        return v;
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




