# Database Appwrite - Nguyên tắc thiết kế

## 1. Mục tiêu

Tài liệu này dùng để thống nhất cách nhóm thiết kế dữ liệu cho MinLish. Tài liệu chỉ mô tả nguyên tắc và quy trình, không chốt sẵn tên collection, tên field, kiểu dữ liệu chi tiết hoặc index.

## 2. Quyết định chung

* Ứng dụng dùng một database Appwrite chung.
* Không chia database riêng theo thành viên hoặc theo module.
* Mỗi module có thể có nhóm dữ liệu riêng, nhưng phải liên kết được với các module khác.
* Schema chi tiết chỉ được tạo sau khi cả nhóm thống nhất.
* Không tạo hoặc sửa dữ liệu thật trên Appwrite nếu chưa cập nhật tài liệu và báo nhóm.

## 3. Lý do dùng một database chung

Một database chung giúp:

* Dữ liệu người dùng, từ vựng, học tập và thống kê liên kết nhất quán.
* Dễ kiểm soát phân quyền.
* Dễ tích hợp giữa các module.
* Dễ demo và bảo trì.
* Tránh tình trạng mỗi thành viên thiết kế dữ liệu theo một kiểu riêng.

## 4. Nhóm dữ liệu cần thiết

Nhóm cần thống nhất dữ liệu ở mức nghiệp vụ cho các phần sau:

* Thông tin người dùng và cấu hình học tập.
* Bộ từ vựng.
* Từ vựng trong từng bộ.
* Trạng thái học và lịch ôn tập.
* Kết quả luyện tập.
* Thống kê tiến độ theo ngày.
* Cấu hình nhắc học.

Tên collection và field cụ thể sẽ được quyết định sau trong buổi thiết kế schema.

## 5. Vai trò khi thiết kế dữ liệu

| Vai trò | Trách nhiệm |
| --- | --- |
| Người phụ trách tài khoản | Đề xuất dữ liệu hồ sơ và cấu hình người dùng |
| Người phụ trách từ vựng | Đề xuất dữ liệu bộ từ và từ vựng |
| Người phụ trách học tập | Đề xuất dữ liệu trạng thái học và lịch ôn |
| Người phụ trách luyện tập/thống kê | Đề xuất dữ liệu kết quả luyện tập và tiến độ |
| Người tổng hợp schema | Kiểm tra tính thống nhất, tránh trùng lặp và mâu thuẫn |

## 6. Quy trình chốt schema

1. Mỗi thành viên liệt kê dữ liệu module của mình cần đọc và ghi.
2. Nhóm xác định dữ liệu nào dùng riêng và dữ liệu nào dùng chung.
3. Nhóm thống nhất quan hệ giữa các nhóm dữ liệu.
4. Nhóm thống nhất tên collection, tên field, kiểu dữ liệu và quyền truy cập.
5. Người tổng hợp cập nhật tài liệu schema chính thức.
6. Sau khi được duyệt, nhóm mới tạo cấu trúc thật trên Appwrite.

## 7. Ràng buộc bắt buộc

* Dữ liệu riêng tư phải gắn được với người dùng sở hữu dữ liệu đó.
* Người dùng không được đọc hoặc sửa dữ liệu riêng của người khác.
* Mật khẩu không được lưu trong database của app.
* Dữ liệu dùng chung giữa nhiều module phải được thống nhất trước khi code.
* Không đổi tên hoặc xóa dữ liệu đã được module khác sử dụng nếu chưa trao đổi.
* Dữ liệu thời gian phải dùng một định dạng thống nhất trong toàn app.

## 8. Kiểm tra trước khi tạo schema thật

Trước khi tạo trên Appwrite, nhóm cần trả lời được:

* Dữ liệu này thuộc module nào?
* Dữ liệu này có thuộc riêng một người dùng không?
* Module khác có cần đọc dữ liệu này không?
* Dữ liệu này có bị trùng với dữ liệu đã có không?
* Khi xóa dữ liệu này, các dữ liệu liên quan bị ảnh hưởng thế nào?
* Cần quyền đọc, tạo, sửa, xóa như thế nào?

## 9. Nguyên tắc thay đổi schema

Nếu cần thay đổi schema sau khi đã code:

* Ghi rõ lý do thay đổi.
* Xác định module bị ảnh hưởng.
* Cập nhật tài liệu trước.
* Thông báo cho cả nhóm.
* Chỉ thay đổi sau khi các thành viên liên quan đồng ý.
