# Backend và Appwrite

## 1. Mục tiêu

Tài liệu này mô tả vai trò backend của MinLish ở mức định hướng. Tài liệu không chốt sẵn tên repository, tên hàm, tên class, tên collection hoặc schema chi tiết.

## 2. Vai trò của Appwrite

MinLish sử dụng Appwrite cho các nhóm chức năng backend:

* Xác thực người dùng.
* Lưu dữ liệu học tập.
* Lưu dữ liệu từ vựng.
* Lưu kết quả luyện tập.
* Lưu thống kê tiến độ.
* Lưu file nếu có import/export hoặc ảnh đại diện.
* Xử lý tác vụ mở rộng nếu cần.

## 3. Nguyên tắc backend

* Dùng một Appwrite project và một database chung cho app.
* Không lưu mật khẩu trong database của ứng dụng.
* Không tự triển khai mã hóa mật khẩu nếu đã dùng Appwrite Auth.
* Mọi dữ liệu riêng tư phải có cơ chế xác định chủ sở hữu.
* Các module chỉ đọc và ghi dữ liệu theo phạm vi được thống nhất.
* Không gọi API ngoài có khóa bí mật trực tiếp từ app mobile nếu khóa cần bảo mật.

## 4. Nhóm thao tác backend cần có

### Tài khoản và hồ sơ

* Đăng ký.
* Đăng nhập.
* Đăng xuất.
* Lấy thông tin người dùng hiện tại.
* Tạo hoặc cập nhật hồ sơ người dùng.

### Từ vựng

* Lấy danh sách bộ từ.
* Tạo, sửa, xóa bộ từ.
* Lấy danh sách từ trong một bộ.
* Tạo, sửa, xóa từ vựng.
* Tìm kiếm và lọc từ vựng.

### Học tập

* Lấy danh sách từ cần học.
* Lấy danh sách từ cần ôn.
* Lưu kết quả sau mỗi lượt học.
* Cập nhật lịch ôn tiếp theo.

### Luyện tập và thống kê

* Tạo dữ liệu câu hỏi luyện tập từ danh sách từ.
* Lưu kết quả luyện tập.
* Tính hoặc lấy dữ liệu thống kê.
* Cập nhật tiến độ theo ngày.

### Nhắc học

* Lưu cấu hình nhắc học.
* Đọc cấu hình nhắc học để tạo notification trên thiết bị.

## 5. Phân quyền

Nguyên tắc phân quyền:

* Người dùng chỉ đọc và sửa dữ liệu của chính mình.
* Dữ liệu dùng chung nếu có phải được tách rõ với dữ liệu cá nhân.
* Không cấp quyền public cho dữ liệu cá nhân.
* Quyền truy cập cần được kiểm tra trước khi demo.

## 6. Xử lý lỗi

Các nhóm lỗi cần xử lý:

* Lỗi đăng nhập hoặc đăng ký.
* Lỗi dữ liệu nhập không hợp lệ.
* Lỗi mất mạng.
* Lỗi không có quyền truy cập.
* Lỗi dữ liệu không tồn tại.
* Lỗi đồng bộ dữ liệu giữa các module.

Thông báo lỗi trên UI cần ngắn gọn, dễ hiểu và không hiển thị chi tiết kỹ thuật không cần thiết.

## 7. Chức năng mở rộng

### Import/export

Nếu triển khai import/export, nhóm cần thống nhất:

* Định dạng file hỗ trợ.
* Dữ liệu bắt buộc trong file.
* Cách báo lỗi khi file sai định dạng.
* Cách tránh tạo dữ liệu trùng.

### AI/LLM

Nếu triển khai AI/LLM, nhóm cần thống nhất:

* Tác vụ AI cụ thể.
* Cách bảo mật khóa API.
* Giới hạn số lần gọi.
* Cách xử lý khi AI trả kết quả không phù hợp.

