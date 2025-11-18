-- =====================================================
-- MUSIC SHOP DATABASE - COMPLETE SETUP
-- Tạo lại toàn bộ database từ đầu
-- =====================================================

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- =====================================================
-- 1. XÓA DATABASE CŨ (NẾU CÓ)
-- =====================================================
DROP DATABASE IF EXISTS music_shop_db;
CREATE DATABASE music_shop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE music_shop_db;

-- =====================================================
-- 2. TẠO CÁC BẢNG
-- =====================================================

-- Bảng Categories
CREATE TABLE categories (
  category_id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Users
CREATE TABLE users (
  user_id INT(11) NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL,
  full_name VARCHAR(100) DEFAULT NULL,
  address TEXT DEFAULT NULL,
  role VARCHAR(10) NOT NULL DEFAULT 'customer',
  PRIMARY KEY (user_id),
  UNIQUE KEY username (username),
  UNIQUE KEY email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Products
CREATE TABLE products (
  product_id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description TEXT DEFAULT NULL,
  price DECIMAL(10,2) NOT NULL,
  image_url VARCHAR(255) DEFAULT NULL,
  stock_quantity INT(11) NOT NULL DEFAULT 0,
  category_id INT(11) DEFAULT NULL,
  total_sold INT(11) NOT NULL DEFAULT 0 COMMENT 'Tổng số lượng đã bán',
  PRIMARY KEY (product_id),
  KEY category_id (category_id),
  CONSTRAINT products_ibfk_1 FOREIGN KEY (category_id) REFERENCES categories (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Vouchers (Mã giảm giá) - Phải tạo trước Orders vì Orders tham chiếu đến Vouchers
CREATE TABLE vouchers (
  voucher_id INT(11) NOT NULL AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL,
  discount_type VARCHAR(20) NOT NULL COMMENT 'percentage hoặc fixed',
  discount_value DECIMAL(10,2) NOT NULL,
  start_date DATETIME NOT NULL,
  end_date DATETIME NOT NULL,
  description TEXT DEFAULT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1 = active, 0 = inactive',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (voucher_id),
  UNIQUE KEY code (code),
  KEY idx_start_date (start_date),
  KEY idx_end_date (end_date),
  KEY idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Orders (CÓ shipped_date và voucher_id)
CREATE TABLE orders (
  order_id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) DEFAULT NULL,
  order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  total_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'Pending',
  shipping_name VARCHAR(100) NOT NULL,
  shipping_phone VARCHAR(20) NOT NULL,
  shipping_address TEXT NOT NULL,
  shipped_date DATETIME DEFAULT NULL COMMENT 'Ngày giao hàng (chỉ có khi status = Shipped)',
  voucher_id INT(11) DEFAULT NULL COMMENT 'Mã giảm giá được áp dụng',
  discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Số tiền giảm giá',
  PRIMARY KEY (order_id),
  KEY user_id (user_id),
  KEY voucher_id (voucher_id),
  CONSTRAINT orders_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (user_id),
  CONSTRAINT orders_ibfk_2 FOREIGN KEY (voucher_id) REFERENCES vouchers (voucher_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng OrderDetails
CREATE TABLE orderdetails (
  order_detail_id INT(11) NOT NULL AUTO_INCREMENT,
  order_id INT(11) DEFAULT NULL,
  product_id INT(11) DEFAULT NULL,
  quantity INT(11) NOT NULL,
  price_at_purchase DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (order_detail_id),
  KEY order_id (order_id),
  KEY product_id (product_id),
  CONSTRAINT orderdetails_ibfk_1 FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
  CONSTRAINT orderdetails_ibfk_2 FOREIGN KEY (product_id) REFERENCES products (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng News (Tin tức - Admin đăng mã giảm giá lên đây)
CREATE TABLE news (
  news_id INT(11) NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  image_url VARCHAR(255) DEFAULT NULL,
  voucher_id INT(11) DEFAULT NULL COMMENT 'Mã giảm giá được đăng trong tin tức này',
  is_published TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1 = published, 0 = draft',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (news_id),
  KEY voucher_id (voucher_id),
  KEY idx_is_published (is_published),
  CONSTRAINT news_ibfk_1 FOREIGN KEY (voucher_id) REFERENCES vouchers (voucher_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Voucher Usage (Theo dõi mã giảm giá đã dùng)
CREATE TABLE voucher_usage (
  usage_id INT(11) NOT NULL AUTO_INCREMENT,
  voucher_id INT(11) NOT NULL,
  order_id INT(11) NOT NULL,
  user_id INT(11) NOT NULL,
  discount_amount DECIMAL(10,2) NOT NULL,
  used_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (usage_id),
  KEY voucher_id (voucher_id),
  KEY order_id (order_id),
  KEY user_id (user_id),
  CONSTRAINT voucher_usage_ibfk_1 FOREIGN KEY (voucher_id) REFERENCES vouchers (voucher_id),
  CONSTRAINT voucher_usage_ibfk_2 FOREIGN KEY (order_id) REFERENCES orders (order_id),
  CONSTRAINT voucher_usage_ibfk_3 FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng Messages (Chat giữa người dùng và admin về sản phẩm)
CREATE TABLE messages (
  message_id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) DEFAULT NULL COMMENT 'User gửi tin nhắn (NULL nếu là admin)',
  admin_id INT(11) DEFAULT NULL COMMENT 'Admin trả lời (NULL nếu là user)',
  message TEXT NOT NULL COMMENT 'Nội dung tin nhắn',
  product_id INT(11) DEFAULT NULL COMMENT 'Sản phẩm liên quan (optional)',
  is_from_admin TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1 = tin nhắn từ admin, 0 = từ user',
  is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1 = đã đọc, 0 = chưa đọc',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (message_id),
  KEY user_id (user_id),
  KEY admin_id (admin_id),
  KEY product_id (product_id),
  KEY idx_created_at (created_at),
  KEY idx_is_read (is_read),
  CONSTRAINT messages_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
  CONSTRAINT messages_ibfk_2 FOREIGN KEY (admin_id) REFERENCES users (user_id) ON DELETE SET NULL,
  CONSTRAINT messages_ibfk_3 FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. TẠO INDEXES ĐỂ TỐI ƯU PERFORMANCE
-- =====================================================

-- Index cho Orders - shipped_date (quan trọng cho thống kê)
CREATE INDEX idx_orders_shipped_date ON orders(shipped_date);

-- Composite index cho status và shipped_date (tối ưu queries thống kê)
CREATE INDEX idx_orders_status_shipped_date ON orders(status, shipped_date);

-- Index cho order_date
CREATE INDEX idx_orders_order_date ON orders(order_date);

-- Index cho Products - category_id (đã có từ foreign key nhưng thêm để chắc chắn)
CREATE INDEX idx_products_category ON products(category_id);

-- =====================================================
-- 4. THÊM DỮ LIỆU MẪU
-- =====================================================

-- Categories
INSERT INTO categories (category_id, name) VALUES
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

-- Users (Mật khẩu đã được hash bằng jBCrypt)
-- admin / admin
-- customer / 123456
INSERT INTO users (user_id, username, password, email, full_name, address, role) VALUES
(1, 'admin', '$2a$10$x7M8ltS2Jnso1n55/R8vhOGLuhhz1mdIt3fq0AQm41M6ttFa9viGW', 'admin@shop.com', 'Quản Trị Viên', '123 Admin Street', 'admin'),
(2, 'customer1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'customer1@shop.com', 'Khách Hàng 1', '123 Customer Street', 'customer'),
(3, 'customer2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'customer2@shop.com', 'Khách Hàng 2', '456 Customer Street', 'customer');

-- Products (Đầy đủ tất cả sản phẩm)
INSERT INTO products (product_id, name, description, price, image_url, stock_quantity, category_id, total_sold) VALUES
(1, 'Đàn Guitar Acoustic Yamaha F310', 'Guitar acoustic phổ thông, âm thanh ấm, phù hợp cho người mới bắt đầu.', 2800000.00, 'images/guitar1.jpg', 2, 1, 0),
(2, 'Đàn Guitar Classic Yamaha C70', 'Guitar classic (dây nilon) bán chạy nhất, âm thanh hay, giá rẻ.', 2500000.00, 'images/guitar2.jpg', 70, 1, 0),
(3, 'Đàn Piano Điện Roland RP-102', 'Piano điện 88 phím, âm thanh SuperNATURAL, kết nối Bluetooth.', 15500000.00, 'images/piano1.jpg', 10, 2, 0),
(4, 'Đàn Organ Casio CT-S200', 'Organ 61 phím, nhỏ gọn, nhiều điệu nhạc, dễ dàng mang đi.', 3200000.00, 'images/organ1.jpg', 30, 3, 0),
(5, 'Ukulele Soprano Tanglewood TWT1', 'Ukulele soprano nhỏ gọn, gỗ Mahogany, âm thanh vui nhộn.', 1200000.00, 'images/ukulele1.jpg', 111, 6, 0),
(6, 'Dây đàn Guitar Alice A107', 'Dây đàn classic, loại tốt, độ bền cao.', 80000.00, 'images/daydan1.jpg', 98, 8, 0),
(8, 'Sáo nứa hun bắc REAL', 'Sáo làm thủ công bằng tay Nghệ nhân làng sáo 100%', 900000.00, 'images/saotruc1.jpg', 1, 4, 0),
(24, 'Đàn Guitar Acoustic Taylor 114e', 'Dáng Grand Auditorium, tích hợp EQ, âm thanh sáng và rõ nét.', 18500000.00, 'images/guitar3.jpg', 15, 1, 0),
(25, 'Đàn Guitar Điện Fender Stratocaster', 'Cây đàn huyền thoại, phù hợp cho rock, blues, và pop.', 22000000.00, 'images/guitar4.jpg', 10, 1, 0),
(26, 'Đàn Guitar Bass Yamaha TRBX174', 'Dòng bass 4 dây, cần đàn thoải mái, âm thanh mạnh mẽ.', 5500000.00, 'images/guitar5.jpg', 20, 1, 0),
(27, 'Đàn Guitar Acoustic Kapok LD-14', 'Giá rẻ, bền bỉ, lựa chọn hàng đầu cho học sinh, sinh viên.', 1800000.00, 'images/guitar6.jpg', 30, 1, 0),
(28, 'Đàn Piano Điện Yamaha P-125', 'Piano di động, 88 phím GHS, âm thanh CF sound engine tinh khiết.', 17000000.00, 'images/piano2.jpg', 10, 2, 0),
(29, 'Đàn Piano Cơ Yamaha U1J', 'Dòng Upright piano tiêu chuẩn công nghiệp, âm thanh phong phú.', 95000000.00, 'images/piano3.jpg', 5, 2, 0),
(30, 'Đàn Piano Điện Casio CDP-S150', 'Thiết kế mỏng nhẹ, 88 phím, nguồn âm thanh AiR.', 11200000.00, 'images/piano4.jpg', 20, 2, 0),
(31, 'Đàn Piano Điện Kawai KDP120', 'Bàn phím Responsive Hammer Compact II, âm thanh Shigeru Kawai.', 21500000.00, 'images/piano5.jpg', 8, 2, 0),
(32, 'Đàn Piano Điện Roland F-107', 'Thiết kế hiện đại, mỏng, phù hợp không gian nhỏ.', 19800000.00, 'images/piano6.jpg', 12, 2, 0),
(33, 'Đàn Organ Yamaha PSR-E473', '61 phím, chức năng Super Articulation Lite, chuyên nghiệp cho biểu diễn.', 7800000.00, 'images/organ2.jpg', 25, 3, 0),
(34, 'Đàn Organ Casio LK-S250', '61 phím sáng, tích hợp chế độ học nhạc Dance Music Mode.', 4500000.00, 'images/organ3.jpg', 19, 3, 0),
(35, 'Đàn Organ Roland E-X50', 'Organ giải trí, âm thanh chuyên nghiệp, loa stereo mạnh mẽ.', 9500000.00, 'images/organ4.jpg', 15, 3, 0),
(36, 'Đàn Organ Yamaha PSR-F52', 'Đơn giản, dễ sử dụng, phù hợp cho người mới làm quen.', 2900000.00, 'images/organ5.jpg', 35, 3, 0),
(37, 'Đàn Organ Casio CT-X700', '61 phím, nguồn âm thanh AiX, 600 tiếng và 195 điệu nhạc.', 5100000.00, 'images/organ6.jpg', 22, 3, 0),
(38, 'Ukulele Concert Kala KA-C', 'Dáng Concert, gỗ Mahogany, âm thanh ấm và đầy đặn hơn Soprano.', 2300000.00, 'images/ukulele2.jpg', 30, 6, 0),
(39, 'Ukulele Tenor Cordoba 15TM', 'Dáng Tenor, gỗ Mahogany, phù hợp cho đệm hát chuyên nghiệp.', 3500000.00, 'images/ukulele3.jpg', 20, 6, 0),
(40, 'Ukulele Soprano Deviser UK-21', 'Giá rẻ, nhiều màu sắc, phù hợp cho người mới chơi.', 750000.00, 'images/ukulele4.jpg', 50, 6, 0),
(41, 'Ukulele Baritone Kala KA-B', 'Kích thước lớn nhất, âm thanh trầm ấm gần giống guitar.', 4200000.00, 'images/ukulele5.jpg', 10, 6, 0),
(42, 'Guitar Electric', 'Sản phẩm guitar điện cho người sành chơi', 3000000.00, 'images/guitar7.jpg', 30, 1, 0),
(43, 'Sáo trúc tone La', 'Sáo trúc tone La âm trầm', 200000.00, 'images/sao1.jpg', 20, 4, 0),
(44, 'Sáo nứa hun bắc tone C', 'Sáo nứa hun bắc tone C âm cao', 400000.00, 'images/sao2.jpg', 20, 4, 0),
(45, 'Sáo trúc tone La trầm', 'Sáo trúc tone La trầm', 500000.00, 'images/sao3.jpg', 20, 4, 0);

-- Orders (Một số đơn hàng mẫu với status Shipped để test thống kê)
INSERT INTO orders (order_id, user_id, order_date, total_amount, status, shipping_name, shipping_phone, shipping_address, shipped_date) VALUES
(1, 2, NOW() - INTERVAL 5 DAY, 2800000.00, 'Shipped', 'Khách Hàng 1', '0123456789', '123 Customer Street', NOW() - INTERVAL 5 DAY),
(2, 2, NOW() - INTERVAL 3 DAY, 2500000.00, 'Shipped', 'Khách Hàng 1', '0123456789', '123 Customer Street', NOW() - INTERVAL 3 DAY),
(3, 3, NOW() - INTERVAL 1 DAY, 5600000.00, 'Shipped', 'Khách Hàng 2', '0987654321', '456 Customer Street', NOW() - INTERVAL 1 DAY),
(4, 2, NOW(), 80000.00, 'Pending', 'Khách Hàng 1', '0123456789', '123 Customer Street', NULL),
(5, 3, NOW(), 3200000.00, 'Processing', 'Khách Hàng 2', '0987654321', '456 Customer Street', NULL);

-- OrderDetails
INSERT INTO orderdetails (order_detail_id, order_id, product_id, quantity, price_at_purchase) VALUES
(1, 1, 1, 1, 2800000.00),
(2, 2, 2, 1, 2500000.00),
(3, 3, 1, 2, 2800000.00),
(4, 4, 6, 1, 80000.00),
(5, 5, 4, 1, 3200000.00);

-- =====================================================
-- 5. RESET AUTO_INCREMENT
-- =====================================================
ALTER TABLE categories AUTO_INCREMENT = 11;
ALTER TABLE users AUTO_INCREMENT = 4;
ALTER TABLE products AUTO_INCREMENT = 46;
ALTER TABLE orders AUTO_INCREMENT = 6;
ALTER TABLE orderdetails AUTO_INCREMENT = 6;
ALTER TABLE vouchers AUTO_INCREMENT = 1;
ALTER TABLE news AUTO_INCREMENT = 1;
ALTER TABLE voucher_usage AUTO_INCREMENT = 1;
ALTER TABLE messages AUTO_INCREMENT = 1;

-- =====================================================
-- 6. KIỂM TRA DỮ LIỆU
-- =====================================================

-- Kiểm tra số lượng records
SELECT 'Categories' as TableName, COUNT(*) as Count FROM categories
UNION ALL
SELECT 'Users', COUNT(*) FROM users
UNION ALL
SELECT 'Products', COUNT(*) FROM products
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders
UNION ALL
SELECT 'OrderDetails', COUNT(*) FROM orderdetails
UNION ALL
SELECT 'Vouchers', COUNT(*) FROM vouchers
UNION ALL
SELECT 'News', COUNT(*) FROM news
UNION ALL
SELECT 'VoucherUsage', COUNT(*) FROM voucher_usage
UNION ALL
SELECT 'Messages', COUNT(*) FROM messages;

-- Kiểm tra đơn hàng đã giao
SELECT order_id, status, shipped_date, total_amount 
FROM orders 
WHERE status = 'Shipped' 
ORDER BY shipped_date DESC;

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- =====================================================
-- HƯỚNG DẪN SỬ DỤNG:
-- =====================================================
-- 1. Mở phpMyAdmin: http://localhost/phpmyadmin
-- 2. Chọn database music_shop_db (hoặc tạo mới nếu chưa có)
-- 3. Vào tab SQL và paste toàn bộ file này
-- 4. Click "Go" để chạy
-- 
-- TÀI KHOẢN MẪU:
-- - Admin: username = "admin", password = "admin"
-- - Customer: username = "customer1", password = "123456"
-- =====================================================

