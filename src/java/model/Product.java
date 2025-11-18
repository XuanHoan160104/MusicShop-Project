package model;

import java.sql.Date;

/**
 * Đây là Model (M)
 * Đại diện cho một đối tượng "Product" (Sản phẩm)
 */
public class Product {
    private int product_id;
    private String name;
    private String description;
    private double price; // Dùng double cho giá tiền
    private String image_url;
    private int stock_quantity; // Tồn kho
    private int category_id;
    private Date warehouse_date; // Ngày nhập kho
    private int inventory_days_threshold; // Số ngày tùy chỉnh để tính tồn kho (mặc định 30)

    // Constructor rỗng
    public Product() {
    }

    // Constructor đầy đủ
    public Product(int product_id, String name, String description, double price, String image_url, int stock_quantity, int category_id) {
        this.product_id = product_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
        this.stock_quantity = stock_quantity;
        this.category_id = category_id;
    }

    // Getters and Setters
    // (Bạn có thể dùng NetBeans để tự động generate: 
    // Chuột phải -> Insert Code... -> Getter and Setter... -> Chọn tất cả -> Generate)

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public Date getWarehouse_date() {
        return warehouse_date;
    }

    public void setWarehouse_date(Date warehouse_date) {
        this.warehouse_date = warehouse_date;
    }

    public int getInventory_days_threshold() {
        return inventory_days_threshold;
    }

    public void setInventory_days_threshold(int inventory_days_threshold) {
        this.inventory_days_threshold = inventory_days_threshold;
    }
}