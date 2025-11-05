# Dùng image Java 17
FROM openjdk:17-jdk-slim

# Tạo thư mục làm việc
WORKDIR /app

# Copy file WAR build từ NetBeans
COPY dist/MusicShop.war /app/MusicShop.war

# Mở port 8080
EXPOSE 8080

# Lệnh chạy ứng dụng
CMD ["java", "-jar", "MusicShop.war"]
