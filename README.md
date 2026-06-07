# English Learning App

Ứng dụng học từ vựng tiếng Anh trên nền tảng Android, được phát triển bằng Kotlin và Jetpack Compose. Hệ thống sử dụng Appwrite làm Backend-as-a-Service để quản lý xác thực người dùng, cơ sở dữ liệu và lưu trữ dữ liệu.

Mục tiêu của ứng dụng là hỗ trợ người học ghi nhớ từ vựng hiệu quả thông qua flashcard, ôn tập theo phương pháp Spaced Repetition System (SRS), bài tập trắc nghiệm và theo dõi tiến độ học tập cá nhân.

## Công nghệ sử dụng

* Kotlin
* Jetpack Compose
* MVVM Architecture
* Coroutines & Flow
* Appwrite (Authentication, Database, Storage)
* WorkManager
* Material Design 3

## Chức năng chính

### 1. Quản lý tài khoản và hồ sơ

* Đăng ký, đăng nhập và đăng xuất.
* Xác thực email.
* Khôi phục mật khẩu.
* Quản lý thông tin cá nhân.

### 2. Quản lý từ vựng

* Tạo, chỉnh sửa và xóa mềm (Soft Delete) bộ từ vựng.
* Thêm, cập nhật và quản lý từ vựng.
* Tìm kiếm và lọc theo bộ từ hoặc thẻ (Tag).

### 3. Học bằng Flashcard

* Hiển thị từ vựng dưới dạng flashcard.
* Hiệu ứng lật thẻ trực quan.
* Hỗ trợ nghĩa, ví dụ sử dụng, collocation và ghi chú.

### 4. Ôn tập thông minh (SRS)

* Áp dụng thuật toán SM-2.
* Tự động xác định thời điểm ôn tập tiếp theo dựa trên mức độ ghi nhớ của người học.
* Cá nhân hóa lịch học cho từng người dùng.

### 5. Luyện tập (Quiz)

* Trắc nghiệm chọn đáp án đúng.
* Chấm điểm tự động.
* Lưu lịch sử và kết quả luyện tập.

### 6. Theo dõi tiến độ học tập

* Dashboard tổng quan.
* Thống kê số lượng từ đã học.
* Theo dõi số từ cần ôn tập.
* Ghi nhận chuỗi ngày học liên tiếp (Streak).
* Thống kê tỷ lệ trả lời đúng và hoạt động học tập theo ngày.

### 7. Thông báo nhắc học

* Nhắc học hằng ngày theo thời gian người dùng thiết lập.
* Triển khai bằng WorkManager.
* Tự động khôi phục lịch nhắc sau khi thiết bị khởi động lại.

## Kiến trúc hệ thống

Ứng dụng được xây dựng theo mô hình MVVM nhằm đảm bảo khả năng mở rộng, dễ bảo trì và tách biệt giữa giao diện người dùng, nghiệp vụ và tầng dữ liệu.

## Kết quả đạt được

* Xây dựng hoàn chỉnh ứng dụng học từ vựng trên Android.
* Tích hợp Backend-as-a-Service bằng Appwrite.
* Triển khai thành công thuật toán SM-2 cho chức năng ôn tập thông minh.
* Áp dụng Jetpack Compose và kiến trúc MVVM trong phát triển ứng dụng thực tế.
