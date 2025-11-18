package admin.controller;

import dal.NewsDAO;
import dal.VoucherDAO;
import model.News;
import model.Voucher;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet quản lý news cho admin
 */
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet(name = "AdminNewsServlet", urlPatterns = {"/admin/news"})
public class AdminNewsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        NewsDAO newsDAO = new NewsDAO();
        VoucherDAO voucherDAO = new VoucherDAO();
        
        List<News> newsList = newsDAO.getAllNews();
        List<Voucher> voucherList = voucherDAO.getAllVouchers(); // Để admin chọn voucher khi đăng tin
        
        // Load news để edit nếu có news_id
        String newsIdStr = request.getParameter("news_id");
        if (newsIdStr != null && !newsIdStr.isEmpty()) {
            try {
                News editNews = newsDAO.getNewsById(Integer.parseInt(newsIdStr));
                request.setAttribute("editNews", editNews);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        request.setAttribute("newsList", newsList);
        request.setAttribute("voucherList", voucherList);
        request.setAttribute("activePage", "news");
        request.getRequestDispatcher("/admin/news.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        NewsDAO newsDAO = new NewsDAO();
        
        if ("create".equals(action)) {
            try {
                News news = new News();
                news.setTitle(getStringFromPart(request.getPart("title")));
                news.setContent(getStringFromPart(request.getPart("content")));
                
                // Xử lý upload file ảnh
                Part filePart = request.getPart("imageFile");
                String fileName = null;
                if (filePart != null) {
                    fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                }
                String dbPath = null;
                
                if (fileName != null && !fileName.isEmpty()) {
                    String uploadPath = getServletContext().getRealPath("/images/news");
                    Path path = Paths.get(uploadPath);
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    }
                    Path filePath = Paths.get(uploadPath, fileName);
                    try (InputStream fileContent = filePart.getInputStream()) {
                        Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
                        dbPath = "images/news/" + fileName;
                    } catch (IOException e) {
                        System.err.println("Lỗi khi lưu file: " + e.getMessage());
                    }
                }
                news.setImage_url(dbPath);
                
                String voucherIdStr = getStringFromPart(request.getPart("voucher_id"));
                if (voucherIdStr != null && !voucherIdStr.isEmpty()) {
                    news.setVoucher_id(Integer.parseInt(voucherIdStr));
                }
                
                String isPublishedStr = getStringFromPart(request.getPart("is_published"));
                news.setIs_published("1".equals(isPublishedStr));
                
                newsDAO.createNews(news);
                response.sendRedirect("news?success=created");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("news?error=" + e.getMessage());
            }
        } else if ("update".equals(action)) {
            try {
                News news = new News();
                news.setNews_id(Integer.parseInt(getStringFromPart(request.getPart("news_id"))));
                news.setTitle(getStringFromPart(request.getPart("title")));
                news.setContent(getStringFromPart(request.getPart("content")));
                
                // Xử lý upload file ảnh
                String oldImageUrl = getStringFromPart(request.getPart("oldImageUrl"));
                Part filePart = request.getPart("imageFile");
                String fileName = null;
                if (filePart != null) {
                    fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                }
                String dbPath = oldImageUrl; // Mặc định giữ ảnh cũ
                
                if (fileName != null && !fileName.isEmpty()) {
                    // Người dùng đã upload ảnh mới
                    String uploadPath = getServletContext().getRealPath("/images/news");
                    Path path = Paths.get(uploadPath);
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    }
                    Path filePath = Paths.get(uploadPath, fileName);
                    try (InputStream fileContent = filePart.getInputStream()) {
                        Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
                        dbPath = "images/news/" + fileName;
                    } catch (IOException e) {
                        System.err.println("Lỗi khi lưu file: " + e.getMessage());
                    }
                }
                news.setImage_url(dbPath);
                
                String voucherIdStr = getStringFromPart(request.getPart("voucher_id"));
                if (voucherIdStr != null && !voucherIdStr.isEmpty()) {
                    news.setVoucher_id(Integer.parseInt(voucherIdStr));
                } else {
                    news.setVoucher_id(null);
                }
                
                String isPublishedStr = getStringFromPart(request.getPart("is_published"));
                news.setIs_published("1".equals(isPublishedStr));
                
                newsDAO.updateNews(news);
                response.sendRedirect("news?success=updated");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("news?error=" + e.getMessage());
            }
        } else if ("delete".equals(action)) {
            try {
                int newsId = Integer.parseInt(request.getParameter("news_id"));
                newsDAO.deleteNews(newsId);
                response.sendRedirect("news?success=deleted");
            } catch (Exception e) {
                response.sendRedirect("news?error=" + e.getMessage());
            }
        } else {
            response.sendRedirect("news");
        }
    }
    
    /**
     * Hàm tiện ích để đọc giá trị String từ một Part (trong form multipart).
     */
    private String getStringFromPart(Part part) throws IOException {
        if (part == null) {
            return "";
        }
        try (InputStream inputStream = part.getInputStream();
             Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            return scanner.hasNext() ? scanner.useDelimiter("\\A").next() : "";
        }
    }
}


