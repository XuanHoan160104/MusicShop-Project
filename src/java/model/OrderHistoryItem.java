package model;

import java.sql.Date;

/**
 * Đây là một "View Model" (POJO)
 * Đã cập nhật: Thêm productId và productDescription
 */
public class OrderHistoryItem {
    
    // Từ bảng Products
    private int productId; // THÊM MỚI
    private String productName;
    private String productImageUrl;
    private String productDescription; // THÊM MỚI
    
    // Từ bảng OrderDetails
    private int quantity;
    private double priceAtPurchase;
    
    // Từ bảng Orders
    private String orderStatus;
    private Date orderDate;

    // Constructors
    public OrderHistoryItem() {
    }

    // (Constructor đầy đủ - chúng ta sẽ không dùng cái cũ nữa)

    // Getters and Setters
    // (Bạn có thể dùng NetBeans để tự động generate)

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    // Hàm tiện ích để tính tổng tiền cho món hàng này
    public double getTotalItemPrice() {
        return this.priceAtPurchase * this.quantity;
    }
}