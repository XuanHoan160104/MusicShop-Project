package model;

/**
 * Đây là lớp Model (hay JavaBean/POJO)
 * Nó đại diện cho một đối tượng "Category" (Danh mục) 
 * Các thuộc tính của nó khớp với các cột trong bảng Categories CSDL.
 */
public class Category {
    private int category_id;
    private String name;

    // Constructor rỗng (Bắt buộc phải có)
    public Category() {
    }

    // Constructor đầy đủ (Dùng để tạo đối tượng nhanh)
    public Category(int category_id, String name) {
        this.category_id = category_id;
        this.name = name;
    }

    //
    // Các phương thức Getters và Setters
    // (Dùng để lấy và gán giá trị cho các thuộc tính)
    //
    
    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}