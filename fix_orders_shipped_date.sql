-- Script SQL để sửa dữ liệu không nhất quán trong bảng Orders
-- Vấn đề: Một số đơn hàng có shipped_date nhưng status không phải 'Shipped'
-- Giải pháp: Set shipped_date = NULL cho tất cả đơn hàng có status != 'Shipped'

-- Sửa các đơn hàng có shipped_date nhưng status không phải 'Shipped'
UPDATE Orders 
SET shipped_date = NULL 
WHERE status != 'Shipped' AND shipped_date IS NOT NULL;

-- Kiểm tra kết quả
SELECT order_id, status, shipped_date 
FROM Orders 
WHERE shipped_date IS NOT NULL 
ORDER BY order_id;

-- Kết quả mong đợi: Chỉ các đơn hàng có status = 'Shipped' mới có shipped_date







