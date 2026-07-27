
  # TechStore E-Commerce Backend API 🚀
  
  **Hệ thống API RESTful hiệu suất cao dành cho nền tảng thương mại điện tử.** <br/>
  Được xây dựng với kiến trúc phân lớp, bảo mật chặt chẽ và tối ưu hóa cho môi trường Production.

  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
  [![Java](https://img.shields.io/badge/Java-21-orange.svg?logo=java)](https://www.oracle.com/java/)
  [![MySQL](https://img.shields.io/badge/MySQL-Database-blue.svg?logo=mysql)](https://www.mysql.com/)
  [![JWT](https://img.shields.io/badge/JWT-Security-black.svg?logo=jsonwebtokens)](https://jwt.io/)
  [![Cloudinary](https://img.shields.io/badge/Cloudinary-Image%20Management-blue.svg?logo=cloudinary)](https://cloudinary.com/)
  [![Gemini AI](https://img.shields.io/badge/Google%20Gemini-AI%20Chatbot-blue?logo=google)](https://deepmind.google/technologies/gemini/)
  [![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D.svg?logo=swagger)](https://swagger.io/)
  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

</div>

---

## 📑 Mục Lục
- [Tổng Quan Dự Án](#-tổng-quan-dự-án)
- [Tính Năng Cốt Lõi](#-tính-năng-cốt-lõi)
- [Kiến Trúc Hệ Thống](#-kiến-trúc-hệ-thống)
- [Yêu Cầu Hệ Thống](#-yêu-cầu-hệ-thống)
- [Cài Đặt & Khởi Chạy](#-cài-đặt--khởi-chạy)
- [Biến Môi Trường (Environment Variables)](#-biến-môi-trường)
- [Tài Liệu API (API Docs)](#-tài-liệu-api)
- [Quy Chuẩn Viết Code & Đóng Góp](#-quy-chuẩn-viết-code--đóng-góp)
- [Bảo Mật (Security)](#-bảo-mật)

---

## 📖 Tổng Quan Dự Án

**TechStore Backend** là hạt nhân xử lý toàn bộ nghiệp vụ (business logic) cho hệ thống thương mại điện tử đồ công nghệ. Dự án cung cấp các endpoint RESTful cho phép các client (Web, Mobile App) giao tiếp một cách an toàn và trơn tru.

Dự án áp dụng nguyên lý **Clean Architecture** và **SOLID**, đảm bảo khả năng mở rộng (scalability), dễ bảo trì (maintainability) và dễ dàng viết test.

---

## ✨ Tính Năng Cốt Lõi

- 🔐 **Xác thực & Phân quyền (Auth & Security):** 
  - Đăng nhập/Đăng ký với Spring Security.
  - Sử dụng **Stateless JWT (JSON Web Token)** để cấp quyền truy cập.
  - Phân quyền động theo vai trò (Role-based Access Control: `ADMIN`, `USER`).

- 🤖 **Trợ lý ảo AI (Gemini AI Chatbox):**
  - Tích hợp trực tiếp Google Gemini API.
  - Hỗ trợ tư vấn khách hàng tự động, trả lời các câu hỏi liên quan đến sản phẩm công nghệ.
  
- 📦 **Quản lý Sản phẩm & Danh mục (Product & Category Catalog):**
  - CRUD chi tiết sản phẩm.
  - Phân trang (Pagination) và Lọc/Sắp xếp (Sorting & Filtering) nâng cao.
  
- 🛒 **Quản lý Đơn hàng & Giỏ hàng (Order & Cart Management):**
  - Xử lý giỏ hàng của người dùng.
  - Quản lý quy trình xử lý đơn hàng (Pending, Processing, Shipped, Delivered, Canceled).
  
- 📊 **Quản lý Kho Hàng (Inventory Management):**
  - Quản lý chính xác số lượng tồn kho theo thời gian thực.
  - Lưu lại nhật ký giao dịch xuất/nhập kho chi tiết (`InventoryTransaction`).
  
- ☁️ **Lưu Trữ Ảnh Đám Mây (Cloud Storage):**
  - Tích hợp **Cloudinary** để upload ảnh, tự động resize và tối ưu dung lượng băng thông.

- ❤️ **Danh Sách Yêu Thích (Wishlist):**
  - Quản lý danh sách sản phẩm yêu thích của từng người dùng.

---

## 🏗️ Kiến Trúc Hệ Thống

Dự án tuân theo kiến trúc **Layered Architecture (N-Tier)** chuẩn mực của Spring Boot:

```mermaid
graph TD
    Client[Client (Web/Mobile)] -->|HTTP Request| Controller[Controller Layer (REST API)]
    Controller -->|DTO| Service[Service Layer (Business Logic)]
    Service -->|Entity| Repository[Repository Layer (Data Access)]
    Repository -->|JPA / Hibernate| Database[(MySQL Database)]
    
    Service -->|Upload Image| Cloudinary[Cloudinary (CDN)]
```

### Cấu Trúc Thư Mục Thực Tế:
```text
src/main/java/com/techstore
 ┣ 📂 config        # Khởi tạo bean, cấu hình Security, Swagger, CORS.
 ┣ 📂 controller    # Expose các RESTful API endpoints.
 ┣ 📂 dto           # Objects trung chuyển dữ liệu, Validate đầu vào.
 ┣ 📂 entity        # Lớp ánh xạ Database (JPA / Hibernate).
 ┣ 📂 enums         # Định nghĩa các tập hợp hằng số (Status, Role...).
 ┣ 📂 exception     # ControllerAdvice bắt và xử lý lỗi đồng nhất toàn cục.
 ┣ 📂 repository    # Interface kế thừa JpaRepository tương tác với DB.
 ┣ 📂 security      # Bộ lọc bảo mật (JWT Filter, UserDetails...).
 ┗ 📂 service       # Trái tim dự án: Chứa toàn bộ Business Logic.
```

---

## 💻 Yêu Cầu Hệ Thống

Trước khi bắt đầu, hãy đảm bảo hệ thống của bạn đã cài đặt các công cụ sau:
- **Java:** JDK 21+
- **Database:** MySQL 8.0+
- **Build Tool:** Maven 3.8+ (Hoặc dùng `mvnw` tích hợp sẵn trong source)
- **Tài khoản Cloudinary:** Lấy thông tin API Key để upload hình ảnh.

---

## ⚙️ Biến Môi Trường

Bạn cần tạo file `application-dev.yml` (hoặc thiết lập biến môi trường) với các thông số bắt buộc sau trước khi chạy:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/techstore_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: ${MYSQL_PASSWORD:your_password}

jwt:
  secret: ${JWT_SECRET:your_very_long_secure_secret_key_here}
  expiration: 86400000

gemini:
  api:
    key: ${GEMINI_API_KEY:your_gemini_api_key_here}

cloudinary:
  cloud-name: ${CLOUDINARY_NAME:your_name}
  api-key: ${CLOUDINARY_API_KEY:your_api_key}
  api-secret: ${CLOUDINARY_API_SECRET:your_api_secret}
```

> [!WARNING]
> **Không bao giờ commit file chứa Secret thật lên Git.** Khuyến nghị sử dụng biến môi trường (Environment Variables) trên máy thật (Production).

---

## 🚀 Cài Đặt & Khởi Chạy

### 1. Dành Cho Môi Trường Phát Triển (Local Development)

```bash
# 1. Clone dự án
git clone https://github.com/PhamQuocAnh-bit/EcomerceBackend.git
cd EcomerceBackend/techstore

# 2. Tạo Database trống trong MySQL
# mysql -u root -p -e "CREATE DATABASE techstore_db;"

# 3. Chạy ứng dụng (Sử dụng Maven Wrapper)
# Window:
.\mvnw spring-boot:run
# Linux/Mac:
./mvnw spring-boot:run
```

### 2. Triển Khai Production (Build file JAR)

```bash
# Build bỏ qua Unit Test
.\mvnw clean package -DskipTests

# Khởi chạy bằng Java trực tiếp
java -jar target/techstore-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
```

---

## 📚 Tài Liệu API

Chúng tôi sử dụng **Springdoc OpenAPI (Swagger 3)** để tự động hóa việc viết tài liệu API.
Khi ứng dụng khởi chạy thành công (mặc định port `8080`), truy cập:

- 🌐 **Swagger UI (Giao diện trực quan):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- 📝 **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

**Cách Test API Yêu Cầu Đăng Nhập:**
1. Gọi API `POST /api/v1/auth/login` (hoặc tạo tài khoản mới).
2. Sao chép chuỗi `accessToken` trả về.
3. Click vào nút **Authorize** có biểu tượng ổ khóa trên Swagger.
4. Dán token vào với tiền tố `Bearer ` (Ví dụ: `Bearer eyJhbG...`).

---

## 🛡️ Bảo Mật

Chuẩn bảo mật được áp dụng ở nhiều tầng để đảm bảo an toàn tối đa cho hệ thống:
1. **Password Hashing:** Thuật toán `Bcrypt` (chuẩn công nghiệp) với work factor an toàn.
2. **Stateless Sessions:** Sử dụng JWT, chống tấn công CSRF tự nhiên.
3. **Data Validation:** Sử dụng `@Valid` (Hibernate Validator) để loại trừ payload độc hại ngay từ DTO.
4. **Global Exception Handling:** Xóa dấu vết của Exception/Stacktrace hệ thống, chỉ trả ra JSON error message đã chuẩn hóa cho Client.

---

## 🤝 Quy Chuẩn Viết Code & Đóng Góp

Dự án sử dụng mô hình Git Flow. Nếu bạn là một phần của team hoặc muốn đóng góp:

1. Trỏ vào nhánh `develop` làm nhánh cơ sở.
2. Đặt tên nhánh theo cấu trúc: `feature/<tên-tính-năng>`, `bugfix/<tên-bug>`.
   *(VD: `feature/add-payment-gateway`)*.
3. Code cần format đúng chuẩn (khuyên dùng plugin SonarLint).
4. Tạo Pull Request (PR) về nhánh `develop` và assign một reviewer.

---
<div align="center">
  <b>Được phát triển với ☕ và ❤️. </b>
</div>
