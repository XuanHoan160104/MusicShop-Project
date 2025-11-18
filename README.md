
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

-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th10 17, 2025 lúc 09:24 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `music_shop_db`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `categories`
--

CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `categories`
--

INSERT INTO `categories` (`category_id`, `name`) VALUES
(1, 'Guitar'),
(2, 'Piano'),
(3, 'Organ'),
(4, 'Nhạc cụ dân tộc'),
(5, 'Trống'),
(6, 'Ukulele'),
(7, 'Đàn đã qua sử dụng'),
(8, 'Phụ kiện'),
(9, 'Thiết bị âm thanh'),
(10, 'Cho thuê nhạc cụ');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `messages`
--

CREATE TABLE `messages` (
  `message_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL COMMENT 'User gửi tin nhắn (NULL nếu là admin)',
  `admin_id` int(11) DEFAULT NULL COMMENT 'Admin trả lời (NULL nếu là user)',
  `message` text NOT NULL COMMENT 'Nội dung tin nhắn',
  `product_id` int(11) DEFAULT NULL COMMENT 'Sản phẩm liên quan (optional)',
  `is_from_admin` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1 = tin nhắn từ admin, 0 = từ user',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1 = đã đọc, 0 = chưa đọc',
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `messages`
--

INSERT INTO `messages` (`message_id`, `user_id`, `admin_id`, `message`, `product_id`, `is_from_admin`, `is_read`, `created_at`) VALUES
(1, 4, NULL, 'xin chào', NULL, 0, 1, '2025-11-16 11:21:40'),
(2, 4, NULL, 'hello sếp', NULL, 0, 1, '2025-11-16 11:23:37'),
(3, 4, 1, 'chào con cẹc', NULL, 1, 1, '2025-11-16 11:23:56'),
(4, 4, 1, 'hoàn cẹc', NULL, 1, 1, '2025-11-17 12:17:20'),
(5, 4, 1, 'hoàn chây cách cách', NULL, 1, 1, '2025-11-17 12:41:47');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `news`
--

CREATE TABLE `news` (
  `news_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `voucher_id` int(11) DEFAULT NULL COMMENT 'Mã giảm giá được đăng trong tin tức này',
  `is_published` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1 = published, 0 = draft',
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `news`
--

INSERT INTO `news` (`news_id`, `title`, `content`, `image_url`, `voucher_id`, `is_published`, `created_at`, `updated_at`) VALUES
(1, 'giảm giá khuyến mãi siêu mạnh', 'Hãy nhập code DUNGDZ', '', 1, 1, '2025-11-16 11:25:12', '2025-11-16 11:25:12');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `orderdetails`
--

CREATE TABLE `orderdetails` (
  `order_detail_id` int(11) NOT NULL,
  `order_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `price_at_purchase` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `orderdetails`
--

INSERT INTO `orderdetails` (`order_detail_id`, `order_id`, `product_id`, `quantity`, `price_at_purchase`) VALUES
(1, 1, 1, 1, 2800000.00),
(2, 2, 2, 1, 2500000.00),
(3, 3, 1, 2, 2800000.00),
(4, 4, 6, 1, 80000.00),
(5, 5, 4, 1, 3200000.00),
(6, 6, 1, 1, 2800000.00),
(7, 6, 2, 1, 2500000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `orders`
--

CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `order_date` datetime DEFAULT current_timestamp(),
  `total_amount` decimal(10,2) NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'Pending',
  `shipping_name` varchar(100) NOT NULL,
  `shipping_phone` varchar(20) NOT NULL,
  `shipping_address` text NOT NULL,
  `shipped_date` datetime DEFAULT NULL COMMENT 'Ngày giao hàng (chỉ có khi status = Shipped)',
  `voucher_id` int(11) DEFAULT NULL COMMENT 'Mã giảm giá được áp dụng',
  `discount_amount` decimal(10,2) DEFAULT 0.00 COMMENT 'Số tiền giảm giá'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `orders`
--

INSERT INTO `orders` (`order_id`, `user_id`, `order_date`, `total_amount`, `status`, `shipping_name`, `shipping_phone`, `shipping_address`, `shipped_date`, `voucher_id`, `discount_amount`) VALUES
(1, 2, '2025-11-11 04:18:45', 2800000.00, 'Shipped', 'Khách Hàng 1', '0123456789', '123 Customer Street', '2025-11-11 04:18:45', NULL, 0.00),
(2, 2, '2025-11-13 04:18:45', 2500000.00, 'Shipped', 'Khách Hàng 1', '0123456789', '123 Customer Street', '2025-11-13 04:18:45', NULL, 0.00),
(3, 3, '2025-11-15 04:18:45', 5600000.00, 'Shipped', 'Khách Hàng 2', '0987654321', '456 Customer Street', '2025-11-15 04:18:45', NULL, 0.00),
(4, 2, '2025-11-16 04:18:45', 80000.00, 'Pending', 'Khách Hàng 1', '0123456789', '123 Customer Street', NULL, NULL, 0.00),
(5, 3, '2025-11-16 04:18:45', 3200000.00, 'Processing', 'Khách Hàng 2', '0987654321', '456 Customer Street', NULL, NULL, 0.00),
(6, 4, '2025-11-16 11:25:47', 4770000.00, 'Shipped', 'Nguyễn Xuân Hoàn', '0123456789', 'varid', '2025-11-16 11:26:02', 1, 530000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `products`
--

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `stock_quantity` int(11) NOT NULL DEFAULT 0,
  `category_id` int(11) DEFAULT NULL,
  `total_sold` int(11) NOT NULL DEFAULT 0 COMMENT 'Tổng số lượng đã bán',
  `warehouse_date` date DEFAULT NULL COMMENT 'Ngày nhập kho',
  `inventory_days_threshold` int(11) DEFAULT 30 COMMENT 'Số ngày tùy chỉnh để tính tồn kho (mặc định 30 ngày)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `products`
--

INSERT INTO `products` (`product_id`, `name`, `description`, `price`, `image_url`, `stock_quantity`, `category_id`, `total_sold`, `warehouse_date`, `inventory_days_threshold`) VALUES
(1, 'Đàn Guitar Acoustic Yamaha F310', 'Guitar acoustic phổ thông, âm thanh ấm, phù hợp cho người mới bắt đầu.', 2800000.00, 'images/guitar1.jpg', 1, 1, 0, '2025-10-03', 30),
(2, 'Đàn Guitar Classic Yamaha C70', 'Guitar classic (dây nilon) bán chạy nhất, âm thanh hay, giá rẻ.', 2500000.00, 'images/guitar2.jpg', 2000, 1, 0, '2025-09-18', 30),
(3, 'Đàn Piano Điện Roland RP-102', 'Piano điện 88 phím, âm thanh SuperNATURAL, kết nối Bluetooth.', 15500000.00, 'images/piano1.jpg', 10, 2, 0, '2025-10-13', 30),
(4, 'Đàn Organ Casio CT-S200', 'Organ 61 phím, nhỏ gọn, nhiều điệu nhạc, dễ dàng mang đi.', 3200000.00, 'images/organ1.jpg', 30, 3, 0, '2025-11-02', 30),
(5, 'Ukulele Soprano Tanglewood TWT1', 'Ukulele soprano nhỏ gọn, gỗ Mahogany, âm thanh vui nhộn.', 1200000.00, 'images/ukulele1.jpg', 111, 6, 0, '2025-11-07', 30),
(6, 'Dây đàn Guitar Alice A107', 'Dây đàn classic, loại tốt, độ bền cao.', 80000.00, 'images/daydan1.jpg', 98, 8, 0, NULL, 30),
(8, 'Sáo nứa hun bắc REAL', 'Sáo làm thủ công bằng tay Nghệ nhân làng sáo 100%', 900000.00, 'images/saotruc1.jpg', 1, 4, 0, NULL, 30),
(24, 'Đàn Guitar Acoustic Taylor 114e', 'Dáng Grand Auditorium, tích hợp EQ, âm thanh sáng và rõ nét.', 18500000.00, 'images/guitar3.jpg', 15, 1, 0, NULL, 30),
(25, 'Đàn Guitar Điện Fender Stratocaster', 'Cây đàn huyền thoại, phù hợp cho rock, blues, và pop.', 22000000.00, 'images/guitar4.jpg', 10, 1, 0, NULL, 30),
(26, 'Đàn Guitar Bass Yamaha TRBX174', 'Dòng bass 4 dây, cần đàn thoải mái, âm thanh mạnh mẽ.', 5500000.00, 'images/guitar5.jpg', 20, 1, 0, NULL, 30),
(27, 'Đàn Guitar Acoustic Kapok LD-14', 'Giá rẻ, bền bỉ, lựa chọn hàng đầu cho học sinh, sinh viên.', 1800000.00, 'images/guitar6.jpg', 30, 1, 0, NULL, 30),
(28, 'Đàn Piano Điện Yamaha P-125', 'Piano di động, 88 phím GHS, âm thanh CF sound engine tinh khiết.', 17000000.00, 'images/piano2.jpg', 10, 2, 0, NULL, 30),
(29, 'Đàn Piano Cơ Yamaha U1J', 'Dòng Upright piano tiêu chuẩn công nghiệp, âm thanh phong phú.', 95000000.00, 'images/piano3.jpg', 5, 2, 0, NULL, 30),
(30, 'Đàn Piano Điện Casio CDP-S150', 'Thiết kế mỏng nhẹ, 88 phím, nguồn âm thanh AiR.', 11200000.00, 'images/piano4.jpg', 20, 2, 0, NULL, 30),
(31, 'Đàn Piano Điện Kawai KDP120', 'Bàn phím Responsive Hammer Compact II, âm thanh Shigeru Kawai.', 21500000.00, 'images/piano5.jpg', 8, 2, 0, NULL, 30),
(32, 'Đàn Piano Điện Roland F-107', 'Thiết kế hiện đại, mỏng, phù hợp không gian nhỏ.', 19800000.00, 'images/piano6.jpg', 12, 2, 0, NULL, 30),
(33, 'Đàn Organ Yamaha PSR-E473', '61 phím, chức năng Super Articulation Lite, chuyên nghiệp cho biểu diễn.', 7800000.00, 'images/organ2.jpg', 25, 3, 0, NULL, 30),
(34, 'Đàn Organ Casio LK-S250', '61 phím sáng, tích hợp chế độ học nhạc Dance Music Mode.', 4500000.00, 'images/organ3.jpg', 19, 3, 0, NULL, 30),
(35, 'Đàn Organ Roland E-X50', 'Organ giải trí, âm thanh chuyên nghiệp, loa stereo mạnh mẽ.', 9500000.00, 'images/organ4.jpg', 15, 3, 0, NULL, 30),
(36, 'Đàn Organ Yamaha PSR-F52', 'Đơn giản, dễ sử dụng, phù hợp cho người mới làm quen.', 2900000.00, 'images/organ5.jpg', 35, 3, 0, NULL, 30),
(37, 'Đàn Organ Casio CT-X700', '61 phím, nguồn âm thanh AiX, 600 tiếng và 195 điệu nhạc.', 5100000.00, 'images/organ6.jpg', 22, 3, 0, NULL, 30),
(38, 'Ukulele Concert Kala KA-C', 'Dáng Concert, gỗ Mahogany, âm thanh ấm và đầy đặn hơn Soprano.', 2300000.00, 'images/ukulele2.jpg', 30, 6, 0, NULL, 30),
(39, 'Ukulele Tenor Cordoba 15TM', 'Dáng Tenor, gỗ Mahogany, phù hợp cho đệm hát chuyên nghiệp.', 3500000.00, 'images/ukulele3.jpg', 20, 6, 0, NULL, 30),
(40, 'Ukulele Soprano Deviser UK-21', 'Giá rẻ, nhiều màu sắc, phù hợp cho người mới chơi.', 750000.00, 'images/ukulele4.jpg', 50, 6, 0, NULL, 30),
(41, 'Ukulele Baritone Kala KA-B', 'Kích thước lớn nhất, âm thanh trầm ấm gần giống guitar.', 4200000.00, 'images/ukulele5.jpg', 10, 6, 0, NULL, 30),
(42, 'Guitar Electric', 'Sản phẩm guitar điện cho người sành chơi', 3000000.00, 'images/guitar7.jpg', 30, 1, 0, NULL, 30),
(43, 'Sáo trúc tone La', 'Sáo trúc tone La âm trầm', 200000.00, 'images/sao1.jpg', 20, 4, 0, NULL, 30),
(44, 'Sáo nứa hun bắc tone C', 'Sáo nứa hun bắc tone C âm cao', 400000.00, 'images/sao2.jpg', 20, 4, 0, NULL, 30),
(45, 'Sáo trúc tone La trầm', 'Sáo trúc tone La trầm', 500000.00, 'images/sao3.jpg', 20, 4, 0, NULL, 30),
(46, 'Đàn Guitar Acoustic Test Tồn Kho', 'Sản phẩm test cho tính năng tồn kho - đã trong kho hơn 30 ngày', 3500000.00, 'images/guitar1.jpg', 3000, 1, 0, '2025-11-11', 30),
(47, 'Đàn Piano Test Mới Nhập', 'Sản phẩm test - mới nhập kho chưa đủ 30 ngày', 12000000.00, 'images/piano1.jpg', 15, 2, 0, '2025-10-28', 30),
(48, 'Đàn Organ Không Có Ngày Nhập', 'Sản phẩm test - không có thông tin ngày nhập kho', 5000000.00, 'images/organ1.jpg', 30, 3, 0, '2025-11-10', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `role` varchar(10) NOT NULL DEFAULT 'customer'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `email`, `full_name`, `address`, `role`) VALUES
(1, 'admin', '$2a$10$x7M8ltS2Jnso1n55/R8vhOGLuhhz1mdIt3fq0AQm41M6ttFa9viGW', 'admin@shop.com', 'Quản Trị Viên', '123 Admin Street', 'admin'),
(2, 'customer1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'customer1@shop.com', 'Khách Hàng 1', '123 Customer Street', 'customer'),
(3, 'customer2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'customer2@shop.com', 'Khách Hàng 2', '456 Customer Street', 'customer'),
(4, 'hoanvarid', '$2a$10$rh3sX9g4a97aqsSedxXe9uuioee.jvNlBdRzVAq5kgBJgCho7dqZ2', 'hoan@gmail.com', 'Nguyễn Xuân Hoàn', 'varid', 'customer');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `vouchers`
--

CREATE TABLE `vouchers` (
  `voucher_id` int(11) NOT NULL,
  `code` varchar(50) NOT NULL,
  `discount_type` varchar(20) NOT NULL COMMENT 'percentage hoặc fixed',
  `discount_value` decimal(10,2) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `description` text DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1 = active, 0 = inactive',
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `vouchers`
--

INSERT INTO `vouchers` (`voucher_id`, `code`, `discount_type`, `discount_value`, `start_date`, `end_date`, `description`, `is_active`, `created_at`) VALUES
(1, 'DUNGDZ', 'percentage', 10.00, '2025-11-12 11:24:00', '2025-11-17 11:24:00', 'siêu mạnh', 1, '2025-11-16 11:24:44');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `voucher_usage`
--

CREATE TABLE `voucher_usage` (
  `usage_id` int(11) NOT NULL,
  `voucher_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `discount_amount` decimal(10,2) NOT NULL,
  `used_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `voucher_usage`
--

INSERT INTO `voucher_usage` (`usage_id`, `voucher_id`, `order_id`, `user_id`, `discount_amount`, `used_at`) VALUES
(1, 1, 6, 4, 530000.00, '2025-11-16 11:25:47');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Chỉ mục cho bảng `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`message_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `admin_id` (`admin_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `idx_created_at` (`created_at`),
  ADD KEY `idx_is_read` (`is_read`);

--
-- Chỉ mục cho bảng `news`
--
ALTER TABLE `news`
  ADD PRIMARY KEY (`news_id`),
  ADD KEY `voucher_id` (`voucher_id`),
  ADD KEY `idx_is_published` (`is_published`);

--
-- Chỉ mục cho bảng `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD PRIMARY KEY (`order_detail_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Chỉ mục cho bảng `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `voucher_id` (`voucher_id`),
  ADD KEY `idx_orders_shipped_date` (`shipped_date`),
  ADD KEY `idx_orders_status_shipped_date` (`status`,`shipped_date`),
  ADD KEY `idx_orders_order_date` (`order_date`);

--
-- Chỉ mục cho bảng `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `category_id` (`category_id`),
  ADD KEY `idx_products_category` (`category_id`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Chỉ mục cho bảng `vouchers`
--
ALTER TABLE `vouchers`
  ADD PRIMARY KEY (`voucher_id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `idx_start_date` (`start_date`),
  ADD KEY `idx_end_date` (`end_date`),
  ADD KEY `idx_is_active` (`is_active`);

--
-- Chỉ mục cho bảng `voucher_usage`
--
ALTER TABLE `voucher_usage`
  ADD PRIMARY KEY (`usage_id`),
  ADD KEY `voucher_id` (`voucher_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT cho bảng `messages`
--
ALTER TABLE `messages`
  MODIFY `message_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT cho bảng `news`
--
ALTER TABLE `news`
  MODIFY `news_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT cho bảng `orderdetails`
--
ALTER TABLE `orderdetails`
  MODIFY `order_detail_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT cho bảng `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `vouchers`
--
ALTER TABLE `vouchers`
  MODIFY `voucher_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--[music_shop_db.sql](https://github.com/user-attachments/files/23598353/music_shop_db.sql)

-- AUTO_INCREMENT cho bảng `voucher_usage`
--
ALTER TABLE `voucher_usage`
  MODIFY `usage_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `messages_ibfk_3` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE SET NULL;

--
-- Các ràng buộc cho bảng `news`
--
ALTER TABLE `news`
  ADD CONSTRAINT `news_ibfk_1` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`voucher_id`) ON DELETE SET NULL;

--
-- Các ràng buộc cho bảng `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD CONSTRAINT `orderdetails_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orderdetails_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Các ràng buộc cho bảng `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`voucher_id`) ON DELETE SET NULL;

--
-- Các ràng buộc cho bảng `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`);

--
-- Các ràng buộc cho bảng `voucher_usage`
--
ALTER TABLE `voucher_usage`
  ADD CONSTRAINT `voucher_usage_ibfk_1` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`voucher_id`),
  ADD CONSTRAINT `voucher_usage_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  ADD CONSTRAINT `voucher_usage_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;


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
