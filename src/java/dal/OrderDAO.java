package dal;

import model.CartItem;
import model.User;
import model.Order;
import model.OrderHistoryItem;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.util.LinkedHashMap;



public class OrderDAO extends DBContext {

   
    public boolean createOrder(User user, Map<String, CartItem> cart, String shipName, String shipPhone, String shipAddress) {

        PreparedStatement checkSt = null;
        PreparedStatement orderSt = null;
        PreparedStatement detailSt = null;
        PreparedStatement stockSt = null;
        ResultSet generatedKeys = null;
        ResultSet rs = null;

        try {
            connection.setAutoCommit(false);

            // 2. Kiểm tra tồn kho
            for (CartItem item : cart.values()) {
                String checkStockSql = "SELECT stock_quantity FROM Products WHERE product_id = ?";
                checkSt = connection.prepareStatement(checkStockSql);
                checkSt.setInt(1, item.getProduct().getProduct_id());
                rs = checkSt.executeQuery();

                if (rs.next()) {
                    int stock = rs.getInt("stock_quantity");
                    if (stock < item.getQuantity()) { // Logic (stock < quantity) đã đúng
                        System.out.println("Hết hàng: Sản phẩm ID " + item.getProduct().getProduct_id());
                        connection.rollback();
                        return false; 
                    }
                } else {
                    System.out.println("Không tìm thấy sản phẩm ID " + item.getProduct().getProduct_id());
                     connection.rollback();
                     return false;
                }
                 
                // === LỖI ĐÃ SỬA: Xóa rs.close() và checkSt.close() khỏi vòng lặp ===
            }
            
            // 3. Tạo đơn hàng (INSERT vào Orders)
            String createOrderSql = "INSERT INTO Orders (user_id, total_amount, status, shipping_name, shipping_phone, shipping_address) VALUES (?, ?, 'Pending', ?, ?, ?)";
            orderSt = connection.prepareStatement(createOrderSql, Statement.RETURN_GENERATED_KEYS);

            double totalAmount = 0;
            for (CartItem item : cart.values()) {
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
            // 9. Đóng tất cả tài nguyên và Bật lại auto-commit
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } 
             try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { /* ignored */ } 
             try { if (checkSt != null) checkSt.close(); } catch (SQLException e) { /* ignored */ } 
             try { if (orderSt != null) orderSt.close(); } catch (SQLException e) { /* ignored */ } 
             try { if (detailSt != null) detailSt.close(); } catch (SQLException e) { /* ignored */ } 
             try { if (stockSt != null) stockSt.close(); } catch (SQLException e) { /* ignored */ }
            try { if (connection != null) { connection.setAutoCommit(true); } } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi bật lại autoCommit", ex); }
        }
    }

    // === CÁC HÀM CHO ADMIN DASHBOARD ===
    
    public List<Order> getAllOrders() { 
        List<Order> list = new ArrayList<>(); String sql = "SELECT * FROM Orders ORDER BY order_id DESC"; PreparedStatement st = null; ResultSet rs = null;
        try { st = connection.prepareStatement(sql); rs = st.executeQuery();
            while (rs.next()) { Order o = new Order(); o.setOrder_id(rs.getInt("order_id")); o.setUser_id(rs.getInt("user_id")); o.setOrder_date(rs.getDate("order_date")); o.setTotal_amount(rs.getDouble("total_amount")); o.setStatus(rs.getString("status")); o.setShipping_name(rs.getString("shipping_name")); o.setShipping_phone(rs.getString("shipping_phone")); o.setShipping_address(rs.getString("shipping_address")); list.add(o); }
        } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy tất cả đơn hàng", ex);
        } finally { try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
        return list;
    }
    
    /**
     * Cập nhật trạng thái của một đơn hàng VÀ hoàn kho nếu đơn bị hủy,
     * HOẶC ghi lại ngày giao hàng nếu đơn được giao.
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        Order oldOrder = getOrderById(orderId); if (oldOrder == null) { return false; } String oldStatus = oldOrder.getStatus();
        if (oldStatus.equals(newStatus)) { return true; }
        PreparedStatement stUpdateOrder = null; PreparedStatement stUpdateStock = null;
        try {
            connection.setAutoCommit(false);
            
            // SỬA CÂU SQL: Thêm logic cho shipped_date
            String sqlUpdateOrder = "UPDATE Orders SET status = ?, " +
                                  "shipped_date = CASE " +
                                  "    WHEN ? = 'Shipped' THEN CURRENT_TIMESTAMP " + // Nếu trạng thái mới là 'Shipped', set ngày
                                  "    WHEN ? = 'Cancelled' THEN NULL " + // Nếu bị Hủy, xóa ngày (nếu có)
                                  "    ELSE shipped_date " + // Ngược lại, giữ nguyên ngày cũ
                                  "END " +
                                  "WHERE order_id = ?";
                                  
            stUpdateOrder = connection.prepareStatement(sqlUpdateOrder);
            stUpdateOrder.setString(1, newStatus);
            stUpdateOrder.setString(2, newStatus); // Tham số cho CASE 1
            stUpdateOrder.setString(3, newStatus); // Tham số cho CASE 2
            stUpdateOrder.setInt(4, orderId);
            stUpdateOrder.executeUpdate();
            
            if (newStatus.equals("Cancelled") && !oldStatus.equals("Cancelled")) {
                List<OrderHistoryItem> details = getDetailsForOrder(orderId);
                String sqlUpdateStock = "UPDATE Products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
                stUpdateStock = connection.prepareStatement(sqlUpdateStock);
                for (OrderHistoryItem item : details) {
                    stUpdateStock.setInt(1, item.getQuantity()); stUpdateStock.setInt(2, item.getProductId()); stUpdateStock.addBatch();
                }
                stUpdateStock.executeBatch();
                 System.out.println("Đã hoàn kho cho đơn hàng ID: " + orderId);
            }
            
            connection.commit(); return true;
        } catch (SQLException ex) {
            try { if (connection != null) connection.rollback(); } catch (SQLException e) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi rollback cập nhật trạng thái", e); }
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi cập nhật trạng thái đơn hàng", ex); return false;
        } finally {
            try { if (stUpdateOrder != null) stUpdateOrder.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stUpdateStock != null) stUpdateStock.close(); } catch (SQLException e) { /* ignored */ }
            try { if (connection != null) connection.setAutoCommit(true); } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi bật lại autoCommit", ex); }
        }
    }
    
    public double getTotalRevenue() { 
        String sql = "SELECT SUM(total_amount) FROM Orders WHERE status = 'Shipped'"; PreparedStatement st = null; ResultSet rs = null;
        try { st = connection.prepareStatement(sql); rs = st.executeQuery(); if (rs.next()) { return rs.getDouble(1); }
        } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi tính tổng doanh thu", ex);
        } finally { try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
        return 0;
    }
    
    public int countPendingOrders() { 
        String sql = "SELECT COUNT(*) FROM Orders WHERE status = 'Pending'"; PreparedStatement st = null; ResultSet rs = null;
        try { st = connection.prepareStatement(sql); rs = st.executeQuery(); if (rs.next()) { return rs.getInt(1); }
        } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đếm đơn hàng chờ xử lý", ex);
        } finally { try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
        return 0;
    }
    
    public int countTotalCustomers() { 
         String sql = "SELECT COUNT(*) FROM Users WHERE role = 'customer'"; PreparedStatement st = null; ResultSet rs = null;
        try { st = connection.prepareStatement(sql); rs = st.executeQuery(); if (rs.next()) { return rs.getInt(1); }
        } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi đếm khách hàng", ex);
        } finally { try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
        return 0;
    }
    
    public List<Order> getOrdersByUserID(int userId) { 
        List<Order> list = new ArrayList<>(); String sql = "SELECT * FROM Orders WHERE user_id = ? ORDER BY order_id DESC"; PreparedStatement st = null; ResultSet rs = null;
        try { st = connection.prepareStatement(sql); st.setInt(1, userId); rs = st.executeQuery();
            while (rs.next()) { Order o = new Order(); o.setOrder_id(rs.getInt("order_id")); o.setUser_id(rs.getInt("user_id")); o.setOrder_date(rs.getDate("order_date")); o.setTotal_amount(rs.getDouble("total_amount")); o.setStatus(rs.getString("status")); o.setShipping_name(rs.getString("shipping_name")); o.setShipping_phone(rs.getString("shipping_phone")); o.setShipping_address(rs.getString("shipping_address")); list.add(o); }
        } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy đơn hàng theo User ID", ex);
        } finally { try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
        return list;
    }
    
    public List<model.OrderHistoryItem> getOrderHistoryByUserID(int userId) { 
        List<model.OrderHistoryItem> list = new ArrayList<>(); String sql = "SELECT p.product_id, p.name, p.image_url, p.description, od.quantity, od.price_at_purchase, o.status, o.order_date FROM Orders o JOIN OrderDetails od ON o.order_id = od.order_id JOIN Products p ON od.product_id = p.product_id WHERE o.user_id = ? ORDER BY o.order_id DESC, p.name ASC"; PreparedStatement st = null; ResultSet rs = null;
        try { st = connection.prepareStatement(sql); st.setInt(1, userId); rs = st.executeQuery();
            while (rs.next()) { model.OrderHistoryItem item = new model.OrderHistoryItem(); item.setProductId(rs.getInt("product_id")); item.setProductName(rs.getString("name")); item.setProductImageUrl(rs.getString("image_url")); item.setProductDescription(rs.getString("description")); item.setQuantity(rs.getInt("quantity")); item.setPriceAtPurchase(rs.getDouble("price_at_purchase")); item.setOrderStatus(rs.getString("status")); item.setOrderDate(rs.getDate("order_date")); list.add(item); }
        } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy lịch sử đơn hàng chi tiết", ex);
        } finally { try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
        return list;
    }
    
    public Order getOrderById(int orderId) { 
        String sql = "SELECT * FROM Orders WHERE order_id = ?"; PreparedStatement st = null; ResultSet rs = null;
        try { st = connection.prepareStatement(sql); st.setInt(1, orderId); rs = st.executeQuery();
            if (rs.next()) { Order o = new Order(); o.setOrder_id(rs.getInt("order_id")); o.setUser_id(rs.getInt("user_id")); o.setOrder_date(rs.getDate("order_date")); o.setTotal_amount(rs.getDouble("total_amount")); o.setStatus(rs.getString("status")); o.setShipping_name(rs.getString("shipping_name")); o.setShipping_phone(rs.getString("shipping_phone")); o.setShipping_address(rs.getString("shipping_address")); return o; }
        } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy đơn hàng theo ID", ex);
        } finally { try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
        return null;
    }
    
    public List<model.OrderHistoryItem> getDetailsForOrder(int orderId) { 
        List<model.OrderHistoryItem> list = new ArrayList<>(); String sql = "SELECT p.product_id, p.name, p.image_url, p.description, od.quantity, od.price_at_purchase, o.status, o.order_date FROM Orders o JOIN OrderDetails od ON o.order_id = od.order_id JOIN Products p ON od.product_id = p.product_id WHERE o.order_id = ? ORDER BY p.name ASC"; PreparedStatement st = null; ResultSet rs = null;
        try { st = connection.prepareStatement(sql); st.setInt(1, orderId); rs = st.executeQuery();
            while (rs.next()) { model.OrderHistoryItem item = new model.OrderHistoryItem(); item.setProductId(rs.getInt("product_id")); item.setProductName(rs.getString("name")); item.setProductImageUrl(rs.getString("image_url")); item.setProductDescription(rs.getString("description")); item.setQuantity(rs.getInt("quantity")); item.setPriceAtPurchase(rs.getDouble("price_at_purchase")); item.setOrderStatus(rs.getString("status")); item.setOrderDate(rs.getDate("order_date")); list.add(item); }
        } catch (SQLException ex) { Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy chi tiết đơn hàng", ex);
        } finally { try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ } try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ } }
        return list;
    }

    // === CÁC HÀM MỚI ĐỂ BÁO CÁO DOANH THU (DỰA TRÊN SHIPPED_DATE) ===
    
    /**
     * Lấy tổng doanh thu (chỉ tính đơn 'Shipped') trong một khoảng ngày (dựa trên ngày giao hàng).
     */
    public double getTotalRevenueByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        // SỬA CÂU SQL: Dùng shipped_date
        String sql = "SELECT SUM(total_amount) FROM Orders " +
                     "WHERE status = 'Shipped' AND shipped_date BETWEEN ? AND ?";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setDate(1, startDate);
            st.setDate(2, endDate);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1); // Trả về tổng
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy doanh thu theo ngày", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return 0; // Trả về 0 nếu không có doanh thu
    }
    
    /**
     * Lấy doanh thu (chỉ đơn 'Shipped') của TỪNG NGÀY trong một khoảng (dựa trên ngày giao hàng).
     * Dùng để vẽ biểu đồ.
     */
    public Map<LocalDate, Double> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> dailyRevenue = new LinkedHashMap<>();
        
        // SỬA CÂU SQL: Dùng "shipped_date" ở cả 3 nơi
        String sql = "SELECT DATE(shipped_date) AS sale_date, SUM(total_amount) AS daily_total " +
                     "FROM Orders " +
                     "WHERE status = 'Shipped' AND shipped_date BETWEEN ? AND ? " +
                     "GROUP BY DATE(shipped_date) " + // Nhóm theo ngày giao
                     "ORDER BY sale_date ASC";

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setDate(1, java.sql.Date.valueOf(startDate));
            st.setDate(2, java.sql.Date.valueOf(endDate));
            rs = st.executeQuery();

            while (rs.next()) {
                // Thêm kiểm tra null cho sale_date (quan trọng)
                Date saleDate = rs.getDate("sale_date");
                if (saleDate != null) {
                    LocalDate date = saleDate.toLocalDate();
                    double total = rs.getDouble("daily_total");
                    dailyRevenue.put(date, total);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy doanh thu theo ngày (cho biểu đồ)", ex);
        } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
             try { if (st != null) st.close(); } catch (SQLException e) { /* ignored */ }
        }
        return dailyRevenue;
    }

} 