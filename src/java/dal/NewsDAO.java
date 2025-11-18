package dal;

import model.News;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO cho quản lý News (Tin tức)
 */
public class NewsDAO extends DBContext {

    /**
     * Lấy tất cả news (cho admin)
     */
    public List<News> getAllNews() {
        List<News> list = new ArrayList<>();
        String sql = "SELECT * FROM news ORDER BY created_at DESC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                News n = mapResultSetToNews(rs);
                list.add(n);
            }
        } catch (SQLException ex) {
            Logger.getLogger(NewsDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy tất cả news", ex);
        } finally {
            closeResources(rs, st);
        }
        return list;
    }

    /**
     * Lấy các news đã published (cho trang tin tức công khai)
     */
    public List<News> getPublishedNews() {
        List<News> list = new ArrayList<>();
        String sql = "SELECT * FROM news WHERE is_published = 1 ORDER BY created_at DESC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                News n = mapResultSetToNews(rs);
                list.add(n);
            }
        } catch (SQLException ex) {
            Logger.getLogger(NewsDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy published news", ex);
        } finally {
            closeResources(rs, st);
        }
        return list;
    }

    /**
     * Lấy news theo ID
     */
    public News getNewsById(int newsId) {
        String sql = "SELECT * FROM news WHERE news_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, newsId);
            rs = st.executeQuery();
            if (rs.next()) {
                return mapResultSetToNews(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(NewsDAO.class.getName()).log(Level.SEVERE, "Lỗi khi lấy news theo ID", ex);
        } finally {
            closeResources(rs, st);
        }
        return null;
    }

    /**
     * Tạo news mới (admin)
     */
    public boolean createNews(News news) {
        String sql = "INSERT INTO news (title, content, image_url, voucher_id, is_published) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, news.getTitle());
            st.setString(2, news.getContent());
            st.setString(3, news.getImage_url());
            if (news.getVoucher_id() != null) {
                st.setInt(4, news.getVoucher_id());
            } else {
                st.setNull(4, java.sql.Types.INTEGER);
            }
            st.setBoolean(5, news.isIs_published());
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(NewsDAO.class.getName()).log(Level.SEVERE, "Lỗi khi tạo news", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Cập nhật news (admin)
     */
    public boolean updateNews(News news) {
        String sql = "UPDATE news SET title = ?, content = ?, image_url = ?, voucher_id = ?, is_published = ? WHERE news_id = ?";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, news.getTitle());
            st.setString(2, news.getContent());
            st.setString(3, news.getImage_url());
            if (news.getVoucher_id() != null) {
                st.setInt(4, news.getVoucher_id());
            } else {
                st.setNull(4, java.sql.Types.INTEGER);
            }
            st.setBoolean(5, news.isIs_published());
            st.setInt(6, news.getNews_id());
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(NewsDAO.class.getName()).log(Level.SEVERE, "Lỗi khi cập nhật news", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Xóa news (admin)
     */
    public boolean deleteNews(int newsId) {
        String sql = "DELETE FROM news WHERE news_id = ?";
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, newsId);
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(NewsDAO.class.getName()).log(Level.SEVERE, "Lỗi khi xóa news", ex);
            return false;
        } finally {
            closeResources(null, st);
        }
    }

    /**
     * Helper method: Map ResultSet to News
     */
    private News mapResultSetToNews(ResultSet rs) throws SQLException {
        News n = new News();
        n.setNews_id(rs.getInt("news_id"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setImage_url(rs.getString("image_url"));
        int voucherId = rs.getInt("voucher_id");
        n.setVoucher_id(rs.wasNull() ? null : voucherId);
        n.setIs_published(rs.getBoolean("is_published"));
        n.setCreated_at(rs.getTimestamp("created_at"));
        n.setUpdated_at(rs.getTimestamp("updated_at"));
        return n;
    }

    /**
     * Helper method: Close resources
     */
    private void closeResources(ResultSet rs, PreparedStatement st) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) { /* ignored */ }
        try {
            if (st != null) st.close();
        } catch (SQLException e) { /* ignored */ }
    }
}




