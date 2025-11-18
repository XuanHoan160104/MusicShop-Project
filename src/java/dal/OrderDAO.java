package dal;

import model.CartItem;
import model.User;
import model.Order;
import model.OrderHistoryItem;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.sql.DriverManager;
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

    /**
     * Đảm bảo connection luôn sẵn sàng (không null và không đóng)
     */
    private void ensureConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Tạo lại connection nếu null hoặc đã đóng
                String dbURL = "jdbc:mysql://localhost:3306/music_shop_db";
                String username = "root";
                String password = "";
                String driver = "com.mysql.cj.jdbc.Driver";
                Class.forName(driver);
                connection = DriverManager.getConnection(dbURL, username, password);
                System.out.println("OrderDAO: Đã tạo lại connection");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi tạo lại connection", ex);
            throw new SQLException("Không thể tạo kết nối database", ex);
        }
    }

    public boolean createOrder(User user, Map<String, CartItem> cart, String shipName, String shipPhone, String shipAddress, Integer voucherId, double discountAmount) {

        PreparedStatement checkSt = null;
        PreparedStatement orderSt = null;
        PreparedStatement detailSt = null;
        PreparedStatement stockSt = null;
        PreparedStatement voucherUsageSt = null;
        ResultSet generatedKeys = null;
        ResultSet rs = null;

        try {
            // Đảm bảo connection sẵn sàng
            ensureConnection();
            
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

            // 3. Tính tổng tiền và áp dụng giảm giá
            double totalAmount = 0;
            for (CartItem item : cart.values()) {
                totalAmount += item.getTotalItemPrice(); // Dùng getTotalItemPrice()
            }
            
            // Áp dụng giảm giá nếu có voucher
            double finalAmount = totalAmount - discountAmount;
            if (finalAmount < 0) {
                finalAmount = 0;
            }

            // 4. Tạo đơn hàng (INSERT vào Orders) - CÓ voucher_id và discount_amount
            String createOrderSql = "INSERT INTO Orders (user_id, total_amount, status, shipping_name, shipping_phone, shipping_address, voucher_id, discount_amount) VALUES (?, ?, 'Pending', ?, ?, ?, ?, ?)";
            orderSt = connection.prepareStatement(createOrderSql, Statement.RETURN_GENERATED_KEYS);

            orderSt.setInt(1, user.getUser_id());
            orderSt.setDouble(2, finalAmount); // Tổng tiền sau khi giảm
            orderSt.setString(3, shipName);
            orderSt.setString(4, shipPhone);
            orderSt.setString(5, shipAddress);
            if (voucherId != null && voucherId > 0) {
                orderSt.setInt(6, voucherId);
            } else {
                orderSt.setNull(6, java.sql.Types.INTEGER);
            }
            orderSt.setDouble(7, discountAmount);
            orderSt.executeUpdate();

            // 5. Lấy ID đơn hàng vừa tạo
            generatedKeys = orderSt.getGeneratedKeys();
            int orderId = -1;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Không thể lấy ID đơn hàng vừa tạo.");
            }
            generatedKeys.close();
            orderSt.close();

            // 6. Lưu voucher usage nếu có voucher
            if (voucherId != null && voucherId > 0 && discountAmount > 0) {
                String insertVoucherUsageSql = "INSERT INTO voucher_usage (voucher_id, order_id, user_id, discount_amount) VALUES (?, ?, ?, ?)";
                voucherUsageSt = connection.prepareStatement(insertVoucherUsageSql);
                voucherUsageSt.setInt(1, voucherId);
                voucherUsageSt.setInt(2, orderId);
                voucherUsageSt.setInt(3, user.getUser_id());
                voucherUsageSt.setDouble(4, discountAmount);
                voucherUsageSt.executeUpdate();
                voucherUsageSt.close();
            }

            // 7. Thêm chi tiết và Trừ kho
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

            // 8. Thực thi batch
            detailSt.executeBatch();
            stockSt.executeBatch();

            // 9. COMMIT
            connection.commit();
            return true;

        } catch (SQLException ex) {
            // 8. ROLLBACK
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi rollback tạo đơn hàng", e);
            }
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi SQL khi tạo đơn hàng", ex);
            return false;
        } finally {
            // 9. Đóng tất cả tài nguyên và Bật lại auto-commit
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (checkSt != null) {
                    checkSt.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (orderSt != null) {
                    orderSt.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (detailSt != null) {
                    detailSt.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (stockSt != null) {
                    stockSt.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (voucherUsageSt != null) {
                    voucherUsageSt.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi bật lại autoCommit", ex);
            }
        }
    }

    // === CÁC HÀM CHO ADMIN DASHBOARD ===
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY order_id DESC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            ensureConnection();
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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
        }
        return list;
    }

    /**
     * Cập nhật trạng thái của một đơn hàng VÀ hoàn kho nếu đơn bị hủy, HOẶC ghi
     * lại ngày giao hàng nếu đơn được giao.
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        Order oldOrder = getOrderById(orderId);
        if (oldOrder == null) {
            return false;
        }
        String oldStatus = oldOrder.getStatus();
        if (oldStatus.equals(newStatus)) {
            return true;
        }
        PreparedStatement stUpdateOrder = null;
        PreparedStatement stUpdateStock = null;
        try {
            // Đảm bảo connection sẵn sàng
            ensureConnection();
            
            connection.setAutoCommit(false);

            // SỬA CÂU SQL: Thêm logic cho shipped_date
            // SỬA: Chỉ khi status = 'Shipped' mới có shipped_date, các trường hợp khác đều set NULL
            String sqlUpdateOrder = "UPDATE Orders SET status = ?, "
                    + "shipped_date = CASE "
                    + "    WHEN ? = 'Shipped' THEN CURRENT_TIMESTAMP "
                    + // Nếu trạng thái mới là 'Shipped', set ngày
                    "    ELSE NULL "
                    + // Tất cả các trạng thái khác đều set NULL (Pending, Processing, Cancelled)
                    "END "
                    + "WHERE order_id = ?";

            stUpdateOrder = connection.prepareStatement(sqlUpdateOrder);
            stUpdateOrder.setString(1, newStatus);
            stUpdateOrder.setString(2, newStatus); // Tham số cho CASE WHEN
            stUpdateOrder.setInt(3, orderId);
            stUpdateOrder.executeUpdate();

            // Xử lý hoàn kho nếu đơn hàng bị hủy
            if (newStatus.equals("Cancelled") && !oldStatus.equals("Cancelled")) {
                List<OrderHistoryItem> details = getDetailsForOrder(orderId);
                String sqlUpdateStock = "UPDATE Products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
                stUpdateStock = connection.prepareStatement(sqlUpdateStock);
                for (OrderHistoryItem item : details) {
                    stUpdateStock.setInt(1, item.getQuantity());
                    stUpdateStock.setInt(2, item.getProductId());
                    stUpdateStock.addBatch();
                }
                stUpdateStock.executeBatch();
                System.out.println("Đã hoàn kho cho đơn hàng ID: " + orderId);
            }

            connection.commit();
            return true;
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi rollback cập nhật trạng thái", e);
            }
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi cập nhật trạng thái đơn hàng", ex);
            return false;
        } finally {
            try {
                if (stUpdateOrder != null) {
                    stUpdateOrder.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (stUpdateStock != null) {
                    stUpdateStock.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi bật lại autoCommit", ex);
            }
        }
    }

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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
        }
        return 0;
    }

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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
        }
        return 0;
    }

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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
        }
        return 0;
    }

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
                o.setOrder_date(rs.getDate("order_date"));
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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
        }
        return list;
    }

    public List<model.OrderHistoryItem> getOrderHistoryByUserID(int userId) {
        List<model.OrderHistoryItem> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name, p.image_url, p.description, od.quantity, od.price_at_purchase, o.status, o.order_date FROM Orders o JOIN OrderDetails od ON o.order_id = od.order_id JOIN Products p ON od.product_id = p.product_id WHERE o.user_id = ? ORDER BY o.order_id DESC, p.name ASC";
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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
        }
        return list;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM Orders WHERE order_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            ensureConnection();
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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
        }
        return null;
    }

    public List<model.OrderHistoryItem> getDetailsForOrder(int orderId) {
        List<model.OrderHistoryItem> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name, p.image_url, p.description, od.quantity, od.price_at_purchase, o.status, o.order_date FROM Orders o JOIN OrderDetails od ON o.order_id = od.order_id JOIN Products p ON od.product_id = p.product_id WHERE o.order_id = ? ORDER BY p.name ASC";
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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                /* ignored */ }
        }
        return list;
    }

    // === CÁC HÀM MỚI CHO BÁO CÁO DOANH THU ===
    
    /**
     * Lấy doanh thu theo ngày cụ thể (chỉ đơn 'Shipped')
     * TỐI ƯU: Sử dụng timestamp comparison thay vì DATE() để tăng performance
     * SỬA: Bao gồm cả ngày đến cuối ngày (23:59:59)
     */
    public double getRevenueByDate(LocalDate date) {
        String sql = "SELECT SUM(total_amount) FROM Orders "
                + "WHERE status = 'Shipped' AND shipped_date IS NOT NULL "
                + "AND shipped_date >= ? AND shipped_date < DATE_ADD(?, INTERVAL 1 DAY)";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            java.sql.Timestamp startOfDay = java.sql.Timestamp.valueOf(date.atStartOfDay());
            java.sql.Timestamp endOfDay = java.sql.Timestamp.valueOf(date.plusDays(1).atStartOfDay());
            st.setTimestamp(1, startOfDay);
            st.setTimestamp(2, endOfDay);
            rs = st.executeQuery();
            if (rs.next()) {
                double result = rs.getDouble(1);
                return rs.wasNull() ? 0.0 : result;
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy doanh thu theo ngày", ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                /* ignored */ }
        }
        return 0.0;
    }
    
    /**
     * Lấy tổng doanh thu trong khoảng ngày (chỉ đơn 'Shipped')
     * TỐI ƯU: Sử dụng timestamp comparison thay vì DATE() để tăng performance
     * SỬA: Bao gồm cả ngày cuối cùng (endDate) đến cuối ngày
     */
    public double getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT SUM(total_amount) FROM Orders "
                + "WHERE status = 'Shipped' AND shipped_date IS NOT NULL "
                + "AND shipped_date >= ? AND shipped_date < DATE_ADD(?, INTERVAL 1 DAY)";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setTimestamp(1, java.sql.Timestamp.valueOf(startDate.atStartOfDay()));
            // Bao gồm cả ngày endDate đến cuối ngày
            st.setTimestamp(2, java.sql.Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
            rs = st.executeQuery();
            if (rs.next()) {
                double result = rs.getDouble(1);
                return rs.wasNull() ? 0.0 : result;
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy doanh thu theo khoảng ngày", ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                /* ignored */ }
        }
        return 0.0;
    }
    
    /**
     * Lấy doanh thu chi tiết theo ngày trong khoảng (cho biểu đồ)
     * TỐI ƯU: Xóa debug logs, tối ưu query với timestamp comparison
     */
    public Map<LocalDate, Double> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> dailyRevenue = new LinkedHashMap<>();

        // KHỞI TẠO TRƯỚC tất cả các ngày với giá trị 0
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dailyRevenue.put(current, 0.0);
            current = current.plusDays(1);
        }

        // TỐI ƯU: Sử dụng CAST thay vì DATE() để tăng performance
        // SỬA: Bao gồm cả ngày cuối cùng (endDate) đến cuối ngày (23:59:59)
        String sql = "SELECT CAST(shipped_date AS DATE) AS sale_date, SUM(total_amount) AS daily_total "
                + "FROM Orders "
                + "WHERE status = 'Shipped' AND shipped_date IS NOT NULL "
                + "AND shipped_date >= ? AND shipped_date < DATE_ADD(?, INTERVAL 1 DAY) "
                + "GROUP BY CAST(shipped_date AS DATE) "
                + "ORDER BY sale_date ASC";

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            // Start: đầu ngày của startDate
            st.setTimestamp(1, java.sql.Timestamp.valueOf(startDate.atStartOfDay()));
            // End: đầu ngày của endDate + 1 ngày (để bao gồm cả ngày endDate)
            st.setTimestamp(2, java.sql.Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                Date saleDate = rs.getDate("sale_date");
                double total = rs.getDouble("daily_total");
                if (saleDate != null && !rs.wasNull()) {
                    LocalDate date = saleDate.toLocalDate();
                    dailyRevenue.put(date, total);
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy doanh thu theo ngày", ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                // ignored
            }
        }
        
        return dailyRevenue;
    }
    
    /**
     * Lấy doanh thu theo tháng (cho biểu đồ 12 tháng)
     * TỐI ƯU: Sử dụng CAST và tối ưu query
     */
    public Map<String, Double> getMonthlyRevenue(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        
        // TỐI ƯU: Sử dụng CAST và so sánh timestamp thay vì DATE()
        // SỬA: Bao gồm cả ngày cuối cùng (endDate) đến cuối ngày
        String sql = "SELECT YEAR(shipped_date) as year, MONTH(shipped_date) as month, SUM(total_amount) as monthly_total "
                + "FROM Orders "
                + "WHERE status = 'Shipped' AND shipped_date IS NOT NULL "
                + "AND shipped_date >= ? AND shipped_date < DATE_ADD(?, INTERVAL 1 DAY) "
                + "GROUP BY YEAR(shipped_date), MONTH(shipped_date) "
                + "ORDER BY year, month";

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setTimestamp(1, java.sql.Timestamp.valueOf(startDate.atStartOfDay()));
            // Bao gồm cả ngày endDate đến cuối ngày
            st.setTimestamp(2, java.sql.Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                double total = rs.getDouble("monthly_total");
                if (!rs.wasNull()) {
                    String monthKey = String.format("%02d/%d", month, year);
                    monthlyRevenue.put(monthKey, total);
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy doanh thu theo tháng", ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                /* ignored */ }
        }
        
        return monthlyRevenue;
    }
    
    /**
     * Lấy top sản phẩm bán chạy
     */
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        
        String sql = "SELECT p.product_id, p.name, p.image_url, SUM(od.quantity) as total_sold, SUM(od.quantity * od.price_at_purchase) as total_revenue "
                + "FROM OrderDetails od "
                + "JOIN Products p ON od.product_id = p.product_id "
                + "JOIN Orders o ON od.order_id = o.order_id "
                + "WHERE o.status = 'Shipped' "
                + "GROUP BY p.product_id, p.name, p.image_url "
                + "ORDER BY total_sold DESC "
                + "LIMIT ?";

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, limit);
            rs = st.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new LinkedHashMap<>();
                product.put("product_id", rs.getInt("product_id"));
                product.put("name", rs.getString("name"));
                product.put("image_url", rs.getString("image_url"));
                product.put("total_sold", rs.getInt("total_sold"));
                product.put("total_revenue", rs.getDouble("total_revenue"));
                topProducts.add(product);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy top sản phẩm bán chạy", ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                /* ignored */ }
        }
        
        return topProducts;
    }
}