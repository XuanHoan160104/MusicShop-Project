package model;

/**
 * Đây là Model (M)
 * Đại diện cho một món hàng (Item) trong giỏ hàng (Cart)
 */
public class CartItem {
    private Product product; // Sản phẩm
    private int quantity;    // Số lượng

    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    // === SỬA LỖI TẠI ĐÂY ===
    // Sửa tên hàm từ "getTotalPrice" thành "getTotalItemPrice"
    // để khớp với file cart.jsp và profile.jsp
    /**
     * Hàm tiện ích: Tính tổng tiền cho món hàng này
     * @return (giá sản phẩm * số lượng)
     */
    public double getTotalItemPrice() {
        if (product != null) {
            return product.getPrice() * quantity;
        }
        return 0;
    }
    // ======================
}