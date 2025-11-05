
# 🎸 Dự án Website Bán Nhạc Cụ (MusicShop)

Đây là dự án website thương mại điện tử MusicShop, được xây dựng từ đầu sử dụng công nghệ **Java Servlet/JSP** theo mô hình **MVC**. Dự án mô phỏng một cửa hàng nhạc cụ trực tuyến với đầy đủ các chức năng cho cả khách hàng và quản trị viên.

*(Bạn nên chụp một bức ảnh đẹp của dự án và thay thế link `https://i.imgur.com/your-screenshot-url.png` này)*

-----

## 🚀 Công nghệ sử dụng

  * **Backend:** Java Servlet, JSP, JSTL (cho Java EE)
  * **Frontend:** HTML, CSS, Bootstrap 5, JavaScript
  * **Cơ sở dữ liệu:** MySQL
  * **Web Server:** Apache Tomcat 8.5.96
  * **IDE & Build:** Apache NetBeans 23 (sử dụng Ant)
  * **Bảo mật:** jBCrypt (để băm mật khẩu người dùng)
  * **Mô hình:** MVC (Model-View-Controller)

-----

## ✨ Tính năng chính

### 👨‍💻 Dành cho Khách hàng (User)

  * **Trang chủ:** Hiển thị sản phẩm nổi bật, slider, danh mục.
  * **Xem sản phẩm:** Xem theo danh mục (có phân trang) và xem chi tiết sản phẩm.
  * **Tìm kiếm:** Tìm kiếm sản phẩm theo tên (có phân trang).
  * **Giỏ hàng:**
      * Lưu giỏ hàng vào `HttpSession` (cho khách) và CSDL (cho user - *chức năng này chúng ta đã bàn nhưng chưa làm*).
      * Thêm, Xóa, Cập nhật số lượng (tự động submit).
  * **Tài khoản:**
      * Đăng ký tài khoản (mật khẩu được băm bằng `jBCrypt`).
      * Đăng nhập (so sánh mật khẩu băm).
      * Đăng xuất.
  * **Trang Cá nhân (Profile):**
      * Chỉnh sửa thông tin cá nhân (Họ tên, Email, Địa chỉ).
      * Đổi mật khẩu (yêu cầu nhập mật khẩu cũ).
      * Xem lịch sử đơn hàng (với giao diện trực quan, có ảnh, trạng thái).
      * Nút "Mua lại" từ lịch sử đơn hàng.
  * **Thanh toán (Checkout):**
      * Quy trình thanh toán (COD).
      * Yêu cầu đăng nhập để thanh toán.
      * Cho phép nhập thông tin giao hàng riêng.
      * **Quản lý Tồn kho:** Tự động trừ kho (`stock_quantity`) khi đặt hàng thành công.

### 👑 Dành cho Quản trị viên (Admin)

  * **Bảo mật:** Khu vực Admin (`/admin/*`) được bảo vệ bằng `Filter` (yêu cầu đăng nhập và `role == 'admin'`).
  * **Dashboard:**
      * Thống kê trực quan: Tổng doanh thu (chỉ tính đơn "Đã giao"), Đơn hàng chờ xử lý, Tổng số khách hàng.
  * **Quản lý Đơn hàng (CRUD):**
      * Hiển thị danh sách tất cả đơn hàng.
      * Cập nhật trạng thái đơn hàng (Chờ xử lý, Đang xử lý, Đã giao, Đã hủy).
      * **Xem Chi tiết Đơn hàng:** Xem thông tin người nhận và danh sách sản phẩm (ảnh, tên, SL) của 1 đơn.
      * **In đơn:** In đơn lẻ (từ trang chi tiết) và In hàng loạt (từ Dashboard).
  * **Quản lý Sản phẩm (CRUD):**
      * Hiển thị danh sách tất cả sản phẩm.
      * **Thêm** sản phẩm mới (với chức năng **Upload file ảnh**).
      * **Sửa** sản phẩm (có thể upload ảnh mới hoặc giữ ảnh cũ).
      * **Xóa** sản phẩm (tự động xóa các chi tiết đơn hàng liên quan).
  * **Quản lý Người dùng (CRUD):**
      * Hiển thị danh sách tất cả người dùng.
      * **Sửa vai trò** (đổi `customer` $\leftrightarrow$ `admin`).
      * **Xóa** người dùng (tự động xóa đơn hàng và chi tiết đơn hàng của họ bằng Transaction).

-----

## 🔧 Hướng dẫn Cài đặt và Chạy dự án

### 1\. Yêu cầu phần mềm

  * **JDK 23** (hoặc 11/17)
  * **Apache NetBeans 23** (hoặc IDE tương tự hỗ trợ Ant)
  * **XAMPP** (để chạy Apache MySQL)
  * **Apache Tomcat 8.5.96** (cần được tích hợp vào XAMPP hoặc cài đặt riêng)

### 2\. Cài đặt Cơ sở dữ liệu (MySQL)

1.  Mở **XAMPP Control Panel**, khởi động **Apache** và **MySQL**.
2.  Truy cập `http://localhost/phpmyadmin`.
3.  Tạo một CSDL mới với tên: `music_shop_db`.
4.  Chọn CSDL `music_shop_db`, mở tab **SQL** và dán toàn bộ script SQL dưới đây vào để tạo bảng và dữ liệu mẫu (đã bao gồm tài khoản `admin` được băm):

<!-- end list -->

```sql
-- Tạo bảng Categories
CREATE TABLE Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng Users
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    address TEXT,
    role VARCHAR(10) NOT NULL DEFAULT 'customer'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng Products
CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    stock_quantity INT NOT NULL DEFAULT 0,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng Orders (với các cột thông tin giao hàng)
CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Pending',
    shipping_name VARCHAR(100),
    shipping_phone VARCHAR(20),
    shipping_address TEXT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng OrderDetails
CREATE TABLE OrderDetails (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price_at_purchase DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Thêm dữ liệu mẫu cho Categories
INSERT INTO Categories (name) VALUES
('Guitar'), ('Piano'), ('Organ'), ('Nhạc cụ dân tộc'), ('Trống'), ('Ukulele'), ('Sáo'), ('Phụ kiện'), ('Thiết bị âm thanh');

-- Thêm tài khoản admin (pass: admin) và 1 user (pass: 123456)
-- Mật khẩu đã được băm bằng jBCrypt
INSERT INTO Users (username, password, email, full_name, address, role) VALUES
('admin', '$2a$10$sX7M8lts2Jnso1n55/R8vHOGLuhhz1mdlt3fq0AQm41M...', 'admin@shop.com', 'Quản Trị Viên', '123 Admin Street', 'admin'),
('testuser', '$2a$10$Rmp8eZScbxKxO8lHySszuCorkSP9BJQ/C17FSSJF..._g.', 'test@gmail.com', 'Test User', '123 Test Street', 'customer');

-- Thêm dữ liệu mẫu cho Products
INSERT INTO Products (name, description, price, image_url, stock_quantity, category_id) VALUES
('Đàn Guitar Acoustic Yamaha F310', 'Guitar acoustic phổ thông, âm thanh ấm.', 2800000, 'images/guitar1.jpg', 50, 1),
('Đàn Guitar Classic Yamaha C70', 'Guitar classic (dây nilon) bán chạy nhất.', 2500000, 'images/guitar2.jpg', 40, 1),
('Đàn Piano Điện Roland RP-102', 'Piano điện 88 phím, âm thanh SuperNATURAL.', 15500000, 'images/piano1.jpg', 15, 2),
('Đàn Organ Casio CT-S200', 'Organ 61 phím, nhỏ gọn, nhiều điệu nhạc.', 3200000, 'images/organ1.jpg', 30, 3),
('Sáo nứa hun bắc REAL', 'Sáo nứa hun cao cấp làm thủ công 100%', 900000, 'images/saotruc1.jpg', 15, 7),
('Dây đàn Guitar Alice A107', 'Dây đàn classic, loại tốt, độ bền cao.', 80000, 'images/daydan1.jpg', 100, 8);
```

### 3\. Cấu hình Dự án (NetBeans)

1.  **Clone/Tải dự án:** Clone kho chứa GitHub này về máy.
2.  **Mở Dự án:** Mở dự án trong NetBeans.
3.  **Thư viện (Libraries):** Đảm bảo các file `.jar` sau đã được thêm vào thư mục `Libraries` của dự án:
      * `mysql-connector-j-x.x.x.jar` (cho MySQL)
      * `jstl-1.2.jar` (cho JSTL)
      * `jbcrypt-0.4.jar` (để băm mật khẩu)
      * *(Các file `.jar` này thường được đặt trong thư mục `WEB-INF/lib` của dự án)*
4.  **Kết nối CSDL:** Mở file `dal/DBContext.java` và đảm bảo `username` (vd: `root`) và `password` (vd: `""`) khớp với XAMPP MySQL của bạn.

### 4\. Cấu hình Server (Tomcat)

1.  **Tạo Tomcat Admin:** Mở file `C:\xampp\tomcat\conf\tomcat-users.xml` (bằng Notepad quyền Admin) và thêm code sau vào trước `</tomcat-users>` để NetBeans có thể deploy:
    ```xml
    <role rolename="manager-gui"/>
    <role rolename="admin-gui"/>
    <role rolename="manager-script"/> 
    <user username="admin" password="admin" roles="manager-gui,admin-gui,manager-script"/>
    ```
2.  **Khởi động lại Tomcat** trong XAMPP.
3.  **Cấu hình NetBeans:**
      * Vào tab **Services (Dịch vụ)** $\rightarrow$ **Servers**.
      * Chuột phải $\rightarrow$ **Add Server...** $\rightarrow$ Chọn **Apache Tomcat or TomEE**.
      * **Server Location (Catalina Home):** Trỏ đến `C:\xampp\tomcat`.
      * Nhập **Manager Username:** `admin` và **Manager Password:** `admin`.
4.  **Liên kết Dự án:** Chuột phải vào dự án `MusicShop` $\rightarrow$ **Properties** $\rightarrow$ **Run** $\rightarrow$ Chọn **Server** là Apache Tomcat bạn vừa cấu hình.

### 5\. Chạy Dự án

1.  Nhấn **Clean and Build (Shift + F11)**.
2.  Nhấn **Run (F6)**.
3.  Trình duyệt sẽ mở `http://localhost:8080/MusicShop/`.
4.  Đăng nhập bằng tài khoản `admin` / `admin` để truy cập `http://localhost:8080/MusicShop/admin/dashboard`.
