# Phân công công việc nhóm - MinLish App

## 1. Mục tiêu

Tài liệu này là bản tóm tắt phân công công việc cho nhóm 4 thành viên. Bản chi tiết đầy đủ nằm tại:

[Phân công chi tiết 4 thành viên](02_phan_cong_chi_tiet_4_thanh_vien.md)

## 2. Nguyên tắc phân chia

Nhóm chia công việc theo module dọc. Mỗi thành viên đều tham gia:

* Phân tích yêu cầu.
* Thiết kế luồng chức năng.
* Thiết kế giao diện.
* Lập trình mobile bằng Kotlin và Jetpack Compose.
* Xử lý logic nghiệp vụ.
* Làm việc với dữ liệu.
* Kiểm thử.
* Viết tài liệu cho phần mình phụ trách.

Cách chia này giúp khối lượng công việc tương đối cân bằng và phù hợp với môn lập trình di động.

## 3. Phân công tổng quan

| Thành viên | Phạm vi chính | Trọng tâm |
| --- | --- | --- |
| Thành viên 1 | Tài khoản, hồ sơ, cấu trúc ứng dụng | Luồng vào app, hồ sơ, điều hướng |
| Thành viên 2 | Quản lý bộ từ và từ vựng | Tạo, sửa, xóa, tìm kiếm, lọc từ |
| Thành viên 3 | Học flashcard, SRS, kế hoạch học | Trải nghiệm học chính và lịch ôn |
| Thành viên 4 | Luyện tập, thống kê, nhắc học | Quiz, dashboard, notification |

## 4. Ràng buộc chung

* Dùng một database Appwrite chung.
* Không chia database riêng theo thành viên.
* Không chốt sẵn schema chi tiết trong tài liệu phân công.
* Không chốt sẵn tên class, tên file code hoặc tên collection trong tài liệu phân công.
* Stitch hoặc Figma chỉ dùng để hỗ trợ prototype.
* Giao diện cuối cùng phải được lập trình bằng Jetpack Compose.
* Chức năng mở rộng chỉ triển khai sau khi chức năng chính ổn định.

## 5. Tiêu chí hoàn thành

Ứng dụng đạt phạm vi chính khi:

* Người dùng đăng ký và đăng nhập được.
* Người dùng quản lý được bộ từ và từ vựng.
* Người dùng học được bằng flashcard.
* Hệ thống lưu được kết quả học để phục vụ ôn tập.
* Người dùng luyện tập được bằng quiz.
* Người dùng xem được tiến độ học cơ bản.
* Có cơ chế nhắc học ở mức demo.
* Các module tích hợp được trong một luồng demo hoàn chỉnh.
