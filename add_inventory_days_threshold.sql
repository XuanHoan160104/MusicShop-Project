-- =====================================================
-- THÊM CỘT inventory_days_threshold VÀO BẢNG PRODUCTS
-- =====================================================

USE music_shop_db;

-- Thêm cột inventory_days_threshold (số ngày tùy chỉnh để tính tồn kho)
ALTER TABLE products 
ADD COLUMN inventory_days_threshold INT(11) DEFAULT 30 COMMENT 'Số ngày tùy chỉnh để tính tồn kho (mặc định 30 ngày)';

-- Cập nhật các sản phẩm hiện có với giá trị mặc định 30 ngày
UPDATE products SET inventory_days_threshold = 30 WHERE inventory_days_threshold IS NULL;


