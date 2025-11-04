package dal;

import model.CartItem;
import model.User;
// === THÊM IMPORT MỚI ===
import model.Order;
import model.OrderHistoryItem; // Thêm import cho model mới
import java.util.ArrayList;
import java.util.List;
import java.sql.Date; // Thêm import cho java.sql.Date
// ======================
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp DAO (Data Access Object) cho Order
 * Xử lý logic checkout phức tạp
 */
public class OrderDAO extends DBContext {

    /**
     * Tạo một đơn hàng mới (Sử dụng Transaction)
     * @param user Người dùng đặt hàng
     * @param cart Giỏ hàng (Map)
     * @param shipName Tên người nhận
     * @param shipPhone SĐT người nhận
     * @param shipAddress Địa chỉ người nhận
     * @return true nếu đặt hàng thành công, false nếu thất bại (hết hàng, lỗi CSDL)
     */
    public boolean createOrder(User user, Map<String, CartItem> cart, String shipName, String shipPhone, String shipAddress) {

        PreparedStatement checkSt = null; // Khai báo ngoài try
        PreparedStatement orderSt = null;
        PreparedStatement detailSt = null;
        PreparedStatement stockSt = null;
        ResultSet generatedKeys = null;
        ResultSet rs = null;

        try {
            // ----- BẮT ĐẦU TRANSACTION -----
            connection.setAutoCommit(false);

            // 2. Kiểm tra tồn kho
            for (CartItem item : cart.values()) {
                String checkStockSql = "SELECT stock_quantity FROM Products WHERE product_id = ?";
                checkSt = connection.prepareStatement(checkStockSql);
                checkSt.setInt(1, item.getProduct().getProduct_id());
                rs = checkSt.executeQuery();

                if (rs.next()) {
                    int stock = rs.getInt("stock_quantity");
                    if (stock < item.getQuantity()) {
                        System.out.println("Hết hàng: Sản phẩm ID " + item.getProduct().getProduct_id());
                        connection.rollback();
                        return false; // Báo thất bại sớm, finally sẽ xử lý AutoCommit
                    }
                } else {
                    System.out.println("Không tìm thấy sản phẩm ID " + item.getProduct().getProduct_id());
                     connection.rollback();
                     return false;
                }
                 rs.close();
                 checkSt.close();
            }

            // 3. Tạo đơn hàng (INSERT vào Orders)
            String createOrderSql = "INSERT INTO Orders (user_id, total_amount, status, shipping_name, shipping_phone, shipping_address) VALUES (?, ?, 'Pending', ?, ?, ?)";
            orderSt = connection.prepareStatement(createOrderSql, Statement.RETURN_GENERATED_KEYS);

            double totalAmount = 0;
            for (CartItem item : cart.values()) {
                // === SỬA LỖI Ở ĐÂY ===
                totalAmount += item.getTotalItemPrice(); // Dùng getTotalItemPrice()
            }

            orderSt.setInt(1, user.getUser_id());
            orderSt.setDouble(2, totalAmount);
            orderSt.setString(3, shipName);
            orderSt.setString(4, shipPhone);
            orderSt.setString(5, shipAddress);
            orderSt.executeUpdate();

            // 4. Lấy ID đơn hàng vừa tạo
            generatedKeys = orderSt.getGeneratedKeys();
            int orderId = -1;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Không thể lấy ID đơn hàng vừa tạo.");
            }
             generatedKeys.close();
             orderSt.close();


            // 5. Thêm chi tiết và Trừ kho
            String insertDetailSql = "INSERT INTO OrderDetails (order_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
            String updateStockSql = "UPDATE Products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";

            detailSt = connection.prepareStatement(insertDetailSql);
            stockSt = connection.prepareStatement(updateStockSql);

            for (CartItem item : cart.values()) {
                detailSt.setInt(1, orderId);
                detailSt.setInt(2, item.getProduct().getProduct_id());
                detailSt.setInt(3, item.getQuantity());
                detailSt.setDouble(4, item.getProduct().getPrice());
                detailSt.addBatch();

                stockSt.setInt(1, item.getQuantity());
                stockSt.setInt(2, item.getProduct().getProduct_id());
                stockSt.addBatch();
            }

            // 6. Thực thi batch
            detailSt.executeBatch();
            stockSt.executeBatch();

            // 7. COMMIT
            connection.commit();
            return true;

        } catch (SQLException ex) {
            // 8. ROLLBACK
            try { if (connection != null) { connection.rollback(); } } catch (SQLException e) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi rollback tạo đơn hàng", e); }
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi SQL khi tạo đơn hàng", ex); return false;
        } finally {
            // 9. Đóng tài nguyên và Bật lại auto-commit
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { /* ignored */ } try { if (checkSt != null) checkSt.close(); } catch (SQLException e) { /* ignored */ } try { if (orderSt != null) orderSt.close(); } catch (SQLException e) { /* ignored */ } try { if (detailSt != null) detailSt.close(); } catch (SQLException e) { /* ignored */ } try { if (stockSt != null) stockSt.close(); } catch (SQLException e) { /* ignored */ }
            try { if (connection != null) { connection.setAutoCommit(true); } } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi bật lại autoCommit", ex); }
        }
    }

    // === CÁC HÀM CHO ADMIN DASHBOARD ===

    /**
     * Lấy TẤT CẢ đơn hàng từ CSDL, sắp xếp mới nhất lên đầu
     * @return một danh sách (List) các đối tượng Order
     */
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY order_id DESC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrder_id(rs.getInt("order_id"));
                o.setUser_id(rs.getInt("user_id"));
                o.setOrder_date(rs.getDate("order_date"));
                o.setTotal_amount(rs.getDouble("total_amount"));
                o.setStatus(rs.getString("status"));
                o.setShipping_name(rs.getString("shipping_name"));
                o.setShipping_phone(rs.getString("shipping_phone"));
                o.setShipping_address(rs.getString("shipping_address"));
                list.add(o);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy tất cả đơn hàng", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

    /**
     * Cập nhật trạng thái của một đơn hàng
     * @param orderId ID đơn hàng cần cập nhật
     * @param newStatus Trạng thái mới (Pending, Processing, Shipped, Cancelled)
     */
    public void updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, newStatus);
            st.setInt(2, orderId);
            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi cập nhật trạng thái đơn hàng", ex);
        } finally {
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    /**
     * Lấy TỔNG DOANH THU (chỉ tính đơn đã giao 'Shipped')
     */
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM Orders WHERE status = 'Shipped'";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi tính tổng doanh thu", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return 0;
    }

    /**
     * Đếm số đơn hàng CHỜ XỬ LÝ
     */
    public int countPendingOrders() {
        String sql = "SELECT COUNT(*) FROM Orders WHERE status = 'Pending'";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đếm đơn hàng chờ xử lý", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return 0;
    }

    /**
     * Lấy TỔNG SỐ KHÁCH HÀNG (đã đăng ký)
     */
    public int countTotalCustomers() {
         String sql = "SELECT COUNT(*) FROM Users WHERE role = 'customer'";
         PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đếm khách hàng", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return 0;
    }

    /**
     * Lấy tất cả đơn hàng của một người dùng cụ thể, sắp xếp mới nhất lên đầu.
     * @param userId ID của người dùng.
     * @return Danh sách các đơn hàng của người dùng đó.
     */
    public List<Order> getOrdersByUserID(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE user_id = ? ORDER BY order_id DESC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            rs = st.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrder_id(rs.getInt("order_id"));
                o.setUser_id(rs.getInt("user_id"));
                o.setOrder_date(rs.getDate("order_date")); // Lấy ngày tháng
                o.setTotal_amount(rs.getDouble("total_amount"));
                o.setStatus(rs.getString("status"));
                o.setShipping_name(rs.getString("shipping_name"));
                o.setShipping_phone(rs.getString("shipping_phone"));
                o.setShipping_address(rs.getString("shipping_address"));
                list.add(o);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy đơn hàng theo User ID", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }
    
    /**
     * Lấy lịch sử chi tiết các món hàng (đã JOIN) của một người dùng.
     * @param userId ID của người dùng.
     * @return Danh sách các món hàng (OrderHistoryItem) đã mua.
     */
    public List<model.OrderHistoryItem> getOrderHistoryByUserID(int userId) {
        List<model.OrderHistoryItem> list = new ArrayList<>();
        
        String sql = "SELECT p.product_id, p.name, p.image_url, p.description, " +
                     "od.quantity, od.price_at_purchase, o.status, o.order_date " +
                     "FROM Orders o " +
                     "JOIN OrderDetails od ON o.order_id = od.order_id " +
                     "JOIN Products p ON od.product_id = p.product_id " +
                     "WHERE o.user_id = ? " +
                     "ORDER BY o.order_id DESC, p.name ASC";

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            rs = st.executeQuery();

            while (rs.next()) {
                model.OrderHistoryItem item = new model.OrderHistoryItem();
                
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("name"));
                item.setProductImageUrl(rs.getString("image_url"));
                item.setProductDescription(rs.getString("description"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPriceAtPurchase(rs.getDouble("price_at_purchase"));
                item.setOrderStatus(rs.getString("status"));
                item.setOrderDate(rs.getDate("order_date"));
                
                list.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy lịch sử đơn hàng chi tiết", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

    // === THÊM 2 HÀM MỚI CHO TRANG "CHI TIẾT ĐƠN HÀNG" (ADMIN) ===

    /**
     * Lấy thông tin của MỘT đơn hàng cụ thể.
     * @param orderId ID của đơn hàng cần tìm.
     * @return Một đối tượng Order.
     */
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM Orders WHERE order_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, orderId);
            rs = st.executeQuery();
            if (rs.next()) {
                Order o = new Order();
                o.setOrder_id(rs.getInt("order_id"));
                o.setUser_id(rs.getInt("user_id"));
                o.setOrder_date(rs.getDate("order_date"));
                o.setTotal_amount(rs.getDouble("total_amount"));
                o.setStatus(rs.getString("status"));
                o.setShipping_name(rs.getString("shipping_name"));
                o.setShipping_phone(rs.getString("shipping_phone"));
                o.setShipping_address(rs.getString("shipping_address"));
                return o;
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy đơn hàng theo ID", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return null; // Không tìm thấy
    }

    /**
     * Lấy CHI TIẾT (các sản phẩm) của một đơn hàng cụ thể.
     * @param orderId ID của đơn hàng.
     * @return Danh sách các món hàng (OrderHistoryItem) trong đơn hàng đó.
     */
    public List<model.OrderHistoryItem> getDetailsForOrder(int orderId) {
        List<model.OrderHistoryItem> list = new ArrayList<>();
        
        String sql = "SELECT p.product_id, p.name, p.image_url, p.description, " +
                     "od.quantity, od.price_at_purchase, o.status, o.order_date " +
                     "FROM Orders o " +
                     "JOIN OrderDetails od ON o.order_id = od.order_id " +
                     "JOIN Products p ON od.product_id = p.product_id " +
                     "WHERE o.order_id = ? " + // Lọc theo order_id
                     "ORDER BY p.name ASC";

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, orderId);
            rs = st.executeQuery();

            while (rs.next()) {
                model.OrderHistoryItem item = new model.OrderHistoryItem();
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("name"));
                item.setProductImageUrl(rs.getString("image_url"));
                item.setProductDescription(rs.getString("description"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPriceAtPurchase(rs.getDouble("price_at_purchase"));
                item.setOrderStatus(rs.getString("status"));
                item.setOrderDate(rs.getDate("order_date"));
                list.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy chi tiết đơn hàng", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return list;
    }

} // End of class OrderDAO