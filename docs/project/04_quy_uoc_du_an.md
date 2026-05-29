# Quy ước phát triển dự án MinLish

## 1. Quy ước chung

* Mọi thành viên làm việc trên cùng repository Git.
* Mỗi chức năng nên được phát triển trên branch riêng.
* Không sửa trực tiếp phần code của thành viên khác nếu chưa trao đổi.
* Khi merge cần kiểm tra app vẫn build được.
* Commit message cần rõ nội dung thay đổi.
* Không tự ý tạo chuẩn đặt tên class, file hoặc schema khi chưa thống nhất nhóm.

## 2. Quy ước Git

### Branch

Tên branch cần thể hiện loại công việc và module phụ trách. Nhóm thống nhất format cụ thể trước khi bắt đầu code để tránh mỗi người đặt một kiểu.

Ví dụ loại branch:

* Branch cho chức năng mới.
* Branch cho sửa lỗi.
* Branch cho tài liệu.
* Branch cho chỉnh giao diện.

### Commit message

Commit message cần ngắn gọn và nêu được nội dung thay đổi:

* Thêm chức năng gì.
* Sửa lỗi gì.
* Cập nhật tài liệu gì.
* Điều chỉnh giao diện gì.

Không dùng commit message quá chung chung như `update`, `fix`, `done`.

## 3. Quy ước kiến trúc

Ứng dụng đi theo hướng tách trách nhiệm:

* Giao diện chỉ hiển thị dữ liệu và nhận thao tác người dùng.
* Lớp quản lý trạng thái xử lý logic hiển thị và gọi dữ liệu.
* Lớp dữ liệu chịu trách nhiệm làm việc với Appwrite hoặc nguồn dữ liệu khác.
* Logic nghiệp vụ độc lập nên được tách khỏi giao diện để dễ kiểm thử.

Không gọi trực tiếp Appwrite trong màn hình giao diện nếu logic đó có thể đưa ra lớp xử lý riêng.

## 4. Quy ước thư mục

* Source code nên chia theo module chức năng.
* Module nào do thành viên nào phụ trách thì thành viên đó chịu trách nhiệm chính.
* Code dùng chung nhiều module mới đưa vào khu vực dùng chung.
* Không đưa toàn bộ màn hình của app vào một thư mục duy nhất.
* Không tạo file code rỗng chỉ để “giữ chỗ” nếu chưa cần.

## 5. Quy ước đặt tên

Tài liệu này không chốt sẵn tên class, tên file hoặc tên package chi tiết. Khi bắt đầu code, nhóm cần thống nhất:

* Cách đặt tên màn hình.
* Cách đặt tên lớp quản lý trạng thái.
* Cách đặt tên lớp xử lý dữ liệu.
* Cách đặt tên model.
* Cách đặt tên hàm và biến.

Nguyên tắc chung:

* Tên phải dễ hiểu.
* Tên phản ánh đúng trách nhiệm.
* Không viết tắt khó hiểu.
* Không đặt tên khác nhau cho cùng một khái niệm.

## 6. Quy ước UI/UX

* Dùng Material 3 và Jetpack Compose.
* Ưu tiên giao diện đơn giản, rõ thao tác chính.
* Các màn hình nhập liệu phải có validate cơ bản.
* Trạng thái tải, dữ liệu rỗng và lỗi phải được xử lý.
* Nút chính, màu sắc và khoảng cách cần thống nhất toàn app.
* Không để text quá dài làm vỡ layout trên mobile.
* Stitch/Figma chỉ dùng để tham khảo prototype, không thay thế code UI.

## 7. Quy ước Appwrite

* Dùng một database chung cho toàn bộ ứng dụng.
* Không tạo schema chi tiết trước khi nhóm thống nhất.
* Mọi dữ liệu riêng tư phải xác định được người sở hữu.
* Query dữ liệu riêng tư phải giới hạn trong phạm vi người dùng hiện tại.
* Không lưu mật khẩu trong database.
* Appwrite Auth quản lý tài khoản và mật khẩu.
* Quyền đọc/sửa dữ liệu phải được kiểm tra trước khi demo.
* Dữ liệu thời gian phải dùng một định dạng thống nhất.

## 8. Quy ước xử lý lỗi

* Lỗi từ backend phải được chuyển thành thông báo dễ hiểu.
* Không hiển thị stack trace cho người dùng.
* Khi mất mạng, hiển thị thông báo rõ ràng.
* Khi dữ liệu rỗng, hiển thị trạng thái rỗng thay vì màn hình trắng.
* Khi thao tác đang chạy, cần có trạng thái tải nếu thao tác mất thời gian.

## 9. Quy ước kiểm thử

* Logic SRS cần được kiểm thử.
* Logic thống kê cần được kiểm thử.
* Các luồng chính cần test thủ công trước demo.
* Mỗi thành viên tự test module của mình trước khi merge.
* Sau khi merge, nhóm test lại luồng tích hợp.

## 10. Definition of Done

Một chức năng được xem là hoàn thành khi:

* Có giao diện hoàn chỉnh.
* Có xử lý logic cần thiết.
* Có kết nối dữ liệu nếu chức năng cần dữ liệu.
* Có xử lý trạng thái tải, lỗi và dữ liệu rỗng nếu phù hợp.
* Chạy được trên thiết bị hoặc emulator.
* Không làm hỏng module khác.
* Có cập nhật tài liệu nếu chức năng ảnh hưởng đến thiết kế.

