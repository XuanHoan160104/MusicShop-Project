package model;

import java.sql.Timestamp;

/**
 * Model đại diện cho Voucher (Mã giảm giá)
 */
public class Voucher {
    private int voucher_id;
    private String code;
    private String discount_type; // "percentage" hoặc "fixed"
    private double discount_value;
    private Timestamp start_date;
    private Timestamp end_date;
    private String description;
    private boolean is_active;
    private Timestamp created_at;

    // Constructors
    public Voucher() {
    }

    public Voucher(int voucher_id, String code, String discount_type, double discount_value,
                   Timestamp start_date, Timestamp end_date, String description, 
                   boolean is_active, Timestamp created_at) {
        this.voucher_id = voucher_id;
        this.code = code;
        this.discount_type = discount_type;
        this.discount_value = discount_value;
        this.start_date = start_date;
        this.end_date = end_date;
        this.description = description;
        this.is_active = is_active;
        this.created_at = created_at;
    }

    // Getters and Setters
    public int getVoucher_id() {
        return voucher_id;
    }

    public void setVoucher_id(int voucher_id) {
        this.voucher_id = voucher_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public double getDiscount_value() {
        return discount_value;
    }

    public void setDiscount_value(double discount_value) {
        this.discount_value = discount_value;
    }

    public Timestamp getStart_date() {
        return start_date;
    }

    public void setStart_date(Timestamp start_date) {
        this.start_date = start_date;
    }

    public Timestamp getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Timestamp end_date) {
        this.end_date = end_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}




