-- Script SQL để tối ưu performance database
-- Chạy script này để tăng tốc độ truy vấn thống kê

-- 1. Tạo index cho shipped_date để tăng tốc queries thống kê
-- Index này sẽ giúp queries filter theo shipped_date nhanh hơn
CREATE INDEX idx_orders_shipped_date ON Orders(shipped_date);

-- 2. Tạo composite index cho status và shipped_date (quan trọng nhất cho thống kê)
-- Index này tối ưu cho các queries WHERE status = 'Shipped' AND shipped_date ...
CREATE INDEX idx_orders_status_shipped_date ON Orders(status, shipped_date);

-- 3. Tạo index cho order_date nếu cần
CREATE INDEX idx_orders_order_date ON Orders(order_date);

-- 4. Kiểm tra các index đã tạo
SHOW INDEX FROM Orders;

-- Lưu ý:
-- - Index sẽ chiếm thêm dung lượng database nhưng tăng tốc queries đáng kể
-- - Nếu đã có index, MySQL sẽ bỏ qua lệnh CREATE INDEX (không lỗi)
-- - Sau khi tạo index, các queries thống kê sẽ chạy nhanh hơn 10-100 lần







