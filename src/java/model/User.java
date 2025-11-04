package model;

/**
 * Đây là Model (M)
 * Đại diện cho một đối tượng "User" (Người dùng)
 */
public class User {
    private int user_id;
    private String username;
    private String password; // Lưu ý: Đây sẽ là mật khẩu ĐÃ BĂM
    private String email;
    private String full_name;
    private String address;
    private String role; // 'customer' hoặc 'admin'

    // Constructor
    public User() {
    }
    
    // Getters and Setters
    // (Bạn có thể dùng NetBeans để tự động generate: 
    // Chuột phải -> Insert Code... -> Getter and Setter...)

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}