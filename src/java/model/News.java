package model;

import java.sql.Timestamp;

/**
 * Model đại diện cho News (Tin tức)
 */
public class News {
    private int news_id;
    private String title;
    private String content;
    private String image_url;
    private Integer voucher_id; // Có thể null nếu không có voucher
    private boolean is_published;
    private Timestamp created_at;
    private Timestamp updated_at;

    // Constructors
    public News() {
    }

    public News(int news_id, String title, String content, String image_url,
                Integer voucher_id, boolean is_published, Timestamp created_at, Timestamp updated_at) {
        this.news_id = news_id;
        this.title = title;
        this.content = content;
        this.image_url = image_url;
        this.voucher_id = voucher_id;
        this.is_published = is_published;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // Getters and Setters
    public int getNews_id() {
        return news_id;
    }

    public void setNews_id(int news_id) {
        this.news_id = news_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Integer getVoucher_id() {
        return voucher_id;
    }

    public void setVoucher_id(Integer voucher_id) {
        this.voucher_id = voucher_id;
    }

    public boolean isIs_published() {
        return is_published;
    }

    public void setIs_published(boolean is_published) {
        this.is_published = is_published;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }
}




