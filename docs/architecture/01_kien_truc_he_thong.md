# Kiến trúc hệ thống MinLish

## 1. Tổng quan

MinLish được xây dựng theo mô hình client-server:

* Client: ứng dụng Android viết bằng Kotlin và Jetpack Compose.
* Backend: Appwrite dùng cho xác thực, một database chung, storage và realtime nếu cần.

Ứng dụng nên tách rõ giao diện, quản lý trạng thái, xử lý nghiệp vụ và truy cập dữ liệu. Tài liệu này chỉ mô tả hướng kiến trúc, không chốt sẵn tên class, tên file hoặc schema database.

## 2. Sơ đồ kiến trúc mức khái niệm

```text
Giao diện Jetpack Compose
        |
        v
Quản lý trạng thái màn hình
        |
        v
Xử lý nghiệp vụ và truy cập dữ liệu
        |
        v
Appwrite SDK hoặc tiện ích cục bộ
        |
        v
Appwrite Auth + Database + Storage
```

## 3. Các tầng chính

### 3.1. Tầng giao diện

Phụ trách:

* Hiển thị màn hình.
* Nhận thao tác người dùng.
* Hiển thị trạng thái tải, lỗi và dữ liệu rỗng.
* Không xử lý trực tiếp logic backend phức tạp.

### 3.2. Tầng quản lý trạng thái

Phụ trách:

* Nhận sự kiện từ giao diện.
* Điều phối luồng xử lý.
* Cập nhật dữ liệu hiển thị cho màn hình.
* Chuyển lỗi kỹ thuật thành thông báo dễ hiểu.

### 3.3. Tầng dữ liệu

Phụ trách:

* Làm việc với Appwrite.
* Đọc và ghi dữ liệu theo phạm vi của người dùng hiện tại.
* Chuyển dữ liệu backend thành dữ liệu app có thể dùng.
* Ẩn chi tiết backend khỏi tầng giao diện.

### 3.4. Tầng nghiệp vụ

Phụ trách:

* Tính lịch ôn tập.
* Tính tiến độ học.
* Tính độ chính xác.
* Validate dữ liệu.
* Xử lý các quy tắc không phụ thuộc giao diện.

## 4. Module chức năng

| Module | Vai trò |
| --- | --- |
| Tài khoản và hồ sơ | Quản lý đăng nhập, đăng ký, hồ sơ |
| Từ vựng | Quản lý bộ từ và từ vựng |
| Học tập | Flashcard, SRS và kế hoạch học |
| Luyện tập | Quiz và bài luyện tập |
| Tiến độ | Dashboard và thống kê |
| Nhắc học | Notification và cấu hình nhắc học |

## 5. Luồng dữ liệu mẫu

### 5.1. Luồng học flashcard

```text
Người dùng mở màn hình học
 -> App lấy danh sách từ cần học hoặc cần ôn
 -> Người dùng lật thẻ và chọn mức độ ghi nhớ
 -> App tính kết quả học
 -> App lưu kết quả vào backend
 -> Màn hình cập nhật trạng thái phiên học
```

### 5.2. Luồng xem tiến độ

```text
Người dùng mở dashboard
 -> App lấy dữ liệu học và luyện tập
 -> App tính số liệu tổng quan
 -> Màn hình hiển thị số từ đã học, độ chính xác và chuỗi ngày học
```

## 6. Xử lý dữ liệu người dùng

Mọi dữ liệu cá nhân phải xác định được người sở hữu. Khi truy vấn dữ liệu riêng tư, app chỉ được lấy dữ liệu thuộc người dùng hiện tại.

Nhóm sẽ chốt tên collection, tên field và kiểu dữ liệu trong buổi thiết kế schema riêng. Không chốt chi tiết schema trong tài liệu kiến trúc này.

## 7. Chức năng mở rộng trong kiến trúc

Kiến trúc cần cho phép mở rộng:

* Google login.
* Import/export CSV.
* AI/LLM tạo ví dụ hoặc câu hỏi.
* Biểu đồ thống kê nâng cao.
* Offline cache nếu cần.

