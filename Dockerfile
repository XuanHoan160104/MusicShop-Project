# Sử dụng OpenJDK 23
FROM openjdk:23-jdk-slim

# Tạo thư mục app trong container
WORKDIR /app

# Copy file WAR từ thư mục dist (đường dẫn build của NetBeans)
COPY dist/MusicShop.war /app/MusicShop.war

# Cổng mà app sẽ chạy (Render cần biết)
EXPOSE 8080

# Lệnh chạy ứng dụng
CMD ["java", "-jar", "MusicShop.war"]
