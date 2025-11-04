package model;

import java.sql.Date; // Dùng java.sql.Date

/**
 * Đây là Model (M)
 * Đại diện cho một đối tượng "Order" (Đơn hàng)
 */
public class Order {
    private int order_id;
    private int user_id;
    private Date order_date; // Ngày đặt hàng
    private double total_amount; // Tổng tiền
    private String status; // Trạng thái: Pending, Processing, Shipped...

    // === THÊM 3 TRƯỜNG MỚI (ĐỂ KHỚP VỚI OrderDAO) ===
    private String shipping_name;
    private String shipping_phone;
    private String shipping_address;

    // Constructor
    public Order() {
    }

    // === THÊM GETTER/SETTER CHO 3 TRƯỜNG MỚI ===
    // (Bạn có thể tự generate: Chuột phải -> Insert Code...)

    public String getShipping_name() {
        return shipping_name;
    }

    public void setShipping_name(String shipping_name) {
        this.shipping_name = shipping_name;
    }

    public String getShipping_phone() {
        return shipping_phone;
    }

    public void setShipping_phone(String shipping_phone) {
        this.shipping_phone = shipping_phone;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }
    
    // --- CÁC GETTER/SETTER CŨ (Giữ nguyên) ---

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}