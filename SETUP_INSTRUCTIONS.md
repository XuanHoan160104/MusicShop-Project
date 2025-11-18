# HƯỚNG DẪN SETUP PROJECT MUSIC SHOP

## 📋 YÊU CẦU HỆ THỐNG

- **JDK**: 11, 17 hoặc 23
- **IDE**: Apache NetBeans 23 (hoặc IDE tương tự)
- **Database**: MySQL 5.7+ hoặc MariaDB 10.4+
- **Server**: Apache Tomcat 8.5+ hoặc 9.x
- **XAMPP**: Để chạy MySQL và Apache

## 🗄️ CÀI ĐẶT DATABASE

### Bước 1: Khởi động MySQL
1. Mở **XAMPP Control Panel**
2. Khởi động **MySQL**

### Bước 2: Tạo Database
1. Truy cập `http://localhost/phpmyadmin`
2. Tạo database mới tên: `music_shop_db`
   - Hoặc chạy file SQL sẽ tự động tạo

### Bước 3: Import Database
1. Chọn database `music_shop_db`
2. Vào tab **SQL**
3. Mở file `music_shop_db_complete.sql`
4. Copy toàn bộ nội dung và paste vào SQL tab
5. Click **Go** để chạy

### Bước 4: Kiểm tra
Chạy query sau để kiểm tra:
```sql
SELECT COUNT(*) as total_orders FROM orders;
SELECT COUNT(*) as shipped_orders FROM orders WHERE status = 'Shipped';
```

## 🔐 TÀI KHOẢN MẪU

### Admin
- **Username**: `admin`
- **Password**: `admin`
- **Role**: `admin`

### Customer
- **Username**: `customer1`
- **Password**: `123456`
- **Role**: `customer`

## ⚙️ CẤU HÌNH PROJECT

### 1. Cấu hình Database Connection
Mở file: `src/java/dal/DBContext.java`

Kiểm tra và sửa nếu cần:
```java
String dbURL = "jdbc:mysql://localhost:3306/music_shop_db";
String username = "root";
String password = ""; // Mặc định XAMPP là rỗng
```

### 2. Build Project
1. Mở NetBeans
2. File → Open Project → Chọn thư mục project
3. Click phải project → Clean and Build

### 3. Deploy lên Tomcat
1. Click phải project → Properties
2. Run → Server: Chọn Tomcat
3. Click phải project → Run

## 🧪 KIỂM TRA

### 1. Kiểm tra kết nối Database
- Mở Console trong NetBeans
- Nếu thấy "KẾT NỐI DATABASE THÀNH CÔNG!" → OK

### 2. Test trang web
1. Truy cập: `http://localhost:8080/MusicShop/`
2. Đăng nhập với tài khoản admin
3. Vào `/admin/dashboard` để xem dashboard
4. Vào `/admin/report` để xem báo cáo doanh thu

### 3. Test thống kê
1. Tạo một đơn hàng mới
2. Vào Dashboard → Cập nhật status thành "Đã giao"
3. Vào trang Báo cáo → Kiểm tra biểu đồ có cập nhật không

## 🔧 TROUBLESHOOTING

### Lỗi kết nối database
- Kiểm tra MySQL đã chạy chưa
- Kiểm tra username/password trong DBContext.java
- Kiểm tra port 3306 có bị chiếm không

### Biểu đồ không hiển thị
1. Mở Console (F12) trong browser
2. Kiểm tra có lỗi JavaScript không
3. Kiểm tra Chart.js đã load chưa
4. Kiểm tra dữ liệu từ servlet có đúng không

### Đơn hàng không cập nhật vào biểu đồ
1. Kiểm tra đơn hàng có `status = 'Shipped'` không
2. Kiểm tra `shipped_date` có giá trị không NULL không
3. Refresh trang báo cáo (F5)
4. Click nút "Làm mới" trên trang báo cáo

## 📝 LƯU Ý QUAN TRỌNG

1. **shipped_date**: Chỉ được set khi `status = 'Shipped'`
2. **Thống kê**: Chỉ tính đơn hàng có `status = 'Shipped'` và `shipped_date IS NOT NULL`
3. **Index**: Đã được tạo tự động để tối ưu performance
4. **Auto-refresh**: Trang báo cáo tự động refresh mỗi 30 giây

## 🚀 DEPLOYMENT

### Production
1. Thay đổi database connection trong DBContext.java
2. Build project thành WAR file
3. Deploy WAR lên Tomcat server
4. Cấu hình database production

### Backup Database
```bash
mysqldump -u root -p music_shop_db > backup.sql
```

### Restore Database
```bash
mysql -u root -p music_shop_db < backup.sql
```







