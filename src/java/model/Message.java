package model;

import java.sql.Timestamp;

/**
 * Model class cho bảng Messages (Chat)
 */
public class Message {
    private int message_id;
    private Integer user_id;
    private Integer admin_id;
    private String message;
    private Integer product_id;
    private boolean is_from_admin;
    private boolean is_read;
    private Timestamp created_at;
    
    // Thông tin bổ sung (không lưu trong DB, dùng để hiển thị)
    private String user_name;
    private String admin_name;
    private String product_name;

    public Message() {
    }

    public Message(Integer user_id, Integer admin_id, String message, Integer product_id, boolean is_from_admin) {
        this.user_id = user_id;
        this.admin_id = admin_id;
        this.message = message;
        this.product_id = product_id;
        this.is_from_admin = is_from_admin;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(Integer admin_id) {
        this.admin_id = admin_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public boolean isIs_from_admin() {
        return is_from_admin;
    }

    public void setIs_from_admin(boolean is_from_admin) {
        this.is_from_admin = is_from_admin;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}



