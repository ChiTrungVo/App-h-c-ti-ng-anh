# Kế hoạch 1 tuần phát triển MinLish App

## 1. Mục tiêu

Nhóm còn khoảng 1 tuần, nên mục tiêu chính là hoàn thành một bản demo ổn định cho môn lập trình di động cơ sở. Kế hoạch này tập trung vào chức năng cốt lõi, chia việc theo từng ngày và không ôm quá nhiều chức năng mở rộng.

Ưu tiên cao nhất:

* App chạy được trên thiết bị hoặc emulator.
* Có luồng demo hoàn chỉnh từ đăng nhập đến học từ.
* Mỗi thành viên có phần code mobile rõ ràng.
* Dữ liệu giữa các module thống nhất.
* Báo cáo và demo giải thích được cách nhóm phân chia công việc.

## 2. Phạm vi bắt buộc trong 1 tuần

Trong 1 tuần, nhóm chỉ nên cam kết các chức năng chính:

* Đăng ký, đăng nhập, đăng xuất ở mức cơ bản.
* Hồ sơ người dùng ở mức cơ bản.
* Tạo bộ từ và thêm từ vựng.
* Xem danh sách bộ từ và danh sách từ.
* Học flashcard.
* Chọn mức độ nhớ sau khi học.
* Lưu kết quả học ở mức đủ để ôn lại.
* Có ít nhất một hoặc hai dạng luyện tập đơn giản.
* Dashboard tiến độ cơ bản.
* Nhắc học ở mức demo hoặc local notification đơn giản.

Các chức năng mở rộng như Google login, import/export, biểu đồ nâng cao, AI/LLM chỉ làm nếu các chức năng chính đã ổn.

## 3. Quy trình làm việc mỗi ngày

Mỗi ngày nhóm nên làm theo cùng một quy trình ngắn:

1. Họp nhanh 10-15 phút để báo hôm qua làm gì, hôm nay làm gì, đang kẹt gì.
2. Mỗi thành viên làm phần module của mình.
3. Cuối ngày commit code và ghi lại phần đã xong.
4. Nếu có thay đổi dữ liệu dùng chung, phải báo nhóm trước khi sửa.
5. Test nhanh app sau khi tích hợp.

Không để đến cuối tuần mới ghép code vì rất dễ vỡ luồng demo.

## 4. Lịch làm việc 7 ngày

### Ngày 1 - Chốt phạm vi và dựng nền

Việc cần làm:

* Chốt chức năng nào bắt buộc, chức năng nào để mở rộng.
* Chốt phân công 4 thành viên.
* Chốt flow demo chính.
* Chốt prototype hoặc wireframe ở mức đủ làm UI.
* Thống nhất nguyên tắc database chung.
* Dựng app chạy được màn hình đầu tiên.
* Mỗi thành viên tạo phần việc của mình trên repository.

Kết quả cuối ngày:

* App mở được.
* Có màn hình khởi đầu hoặc màn hình đăng nhập.
* Có tài liệu phân công và kế hoạch đã chốt.
* Không còn tranh luận phạm vi lớn.

### Ngày 2 - Làm khung app và màn hình chính

Việc cần làm:

* Thành viên 1 làm luồng tài khoản và điều hướng chính.
* Thành viên 2 làm màn hình danh sách bộ từ và form tạo bộ từ.
* Thành viên 3 làm giao diện flashcard tĩnh và flow học thử.
* Thành viên 4 làm giao diện dashboard và màn hình chọn luyện tập.
* Cả nhóm thống nhất dữ liệu tối thiểu cần lưu, nhưng chưa mở rộng quá nhiều.

Kết quả cuối ngày:

* Có thể đi qua các màn hình chính.
* Các màn hình chưa cần hoàn hảo nhưng không bị trắng hoặc crash.
* UI có phong cách tương đối thống nhất.

### Ngày 3 - Kết nối dữ liệu cơ bản

Việc cần làm:

* Thành viên 1 kết nối đăng nhập/hồ sơ ở mức cơ bản.
* Thành viên 2 lưu và đọc bộ từ, từ vựng.
* Thành viên 3 đọc danh sách từ để hiển thị flashcard.
* Thành viên 4 chuẩn bị lưu kết quả luyện tập và dữ liệu tiến độ.
* Nhóm test quyền dữ liệu theo người dùng.

Kết quả cuối ngày:

* Người dùng có thể đăng nhập.
* Người dùng có thể tạo bộ từ và thêm từ.
* Flashcard lấy được dữ liệu thật hoặc dữ liệu mẫu thống nhất.
* Không còn mỗi module dùng một kiểu dữ liệu khác nhau.

### Ngày 4 - Hoàn thiện luồng học

Việc cần làm:

* Thành viên 1 hoàn thiện hồ sơ và xử lý lỗi đăng nhập cơ bản.
* Thành viên 2 hoàn thiện sửa/xóa bộ từ và từ vựng nếu kịp.
* Thành viên 3 hoàn thiện lật flashcard, đánh giá mức độ nhớ và lưu kết quả học.
* Thành viên 4 làm quiz đơn giản và bắt đầu tính tiến độ cơ bản.
* Cả nhóm test luồng: đăng nhập -> tạo bộ từ -> thêm từ -> học flashcard.

Kết quả cuối ngày:

* Luồng học chính chạy được.
* Kết quả học được lưu ở mức phục vụ demo.
* Có thể trình bày được cơ chế học lặp lại ngắt quãng ở mức cơ bản.

### Ngày 5 - Luyện tập, tiến độ và nhắc học

Việc cần làm:

* Thành viên 1 hỗ trợ tích hợp điều hướng và sửa lỗi chung.
* Thành viên 2 bổ sung tìm kiếm hoặc lọc cơ bản nếu chức năng chính đã ổn.
* Thành viên 3 hoàn thiện danh sách từ cần học hoặc cần ôn ở mức đơn giản.
* Thành viên 4 hoàn thiện quiz, dashboard và nhắc học ở mức demo.
* Nhóm thống nhất dữ liệu mẫu để demo.

Kết quả cuối ngày:

* Có quiz hoạt động.
* Dashboard hiển thị được số liệu cơ bản.
* Có cơ chế nhắc học hoặc màn hình cài đặt nhắc học.
* App đã có đủ phần chính để demo.

### Ngày 6 - Tích hợp và sửa lỗi

Việc cần làm:

* Ghép tất cả module vào một luồng demo.
* Test trên thiết bị hoặc emulator.
* Sửa lỗi crash, lỗi điều hướng, lỗi dữ liệu.
* Cắt bỏ chức năng chưa ổn định.
* Chụp màn hình các màn hình chính cho báo cáo.
* Mỗi thành viên ghi lại phần mình đã làm.

Kết quả cuối ngày:

* Có bản demo ổn định.
* Không còn lỗi nghiêm trọng trong luồng demo.
* Tài liệu có đủ nội dung để làm báo cáo.

### Ngày 7 - Chuẩn bị nộp và thuyết trình

Việc cần làm:

* Không thêm chức năng mới nếu không thật sự cần.
* Chỉ sửa lỗi nhỏ và chỉnh UI dễ thấy.
* Chuẩn bị slide.
* Chia phần thuyết trình cho 4 thành viên.
* Tập demo ít nhất 2 lần.
* Chuẩn bị dữ liệu mẫu sẵn trong app.
* Kiểm tra lại repository, tài liệu và file nộp.

Kết quả cuối ngày:

* App demo được trọn luồng.
* Slide và báo cáo sẵn sàng.
* Mỗi thành viên biết rõ phần mình trình bày.

## 5. Phân công theo ngày

| Ngày | Thành viên 1 | Thành viên 2 | Thành viên 3 | Thành viên 4 |
| --- | --- | --- | --- | --- |
| Ngày 1 | Chốt luồng tài khoản, app flow | Chốt luồng từ vựng | Chốt luồng học | Chốt luồng luyện tập/tiến độ |
| Ngày 2 | Màn hình tài khoản, điều hướng | Màn hình bộ từ | Màn hình flashcard | Màn hình quiz/dashboard |
| Ngày 3 | Kết nối auth/hồ sơ | Kết nối dữ liệu từ vựng | Đọc dữ liệu cho flashcard | Chuẩn bị dữ liệu tiến độ |
| Ngày 4 | Hoàn thiện hồ sơ, lỗi cơ bản | Hoàn thiện quản lý từ | Lưu kết quả học | Làm quiz cơ bản |
| Ngày 5 | Hỗ trợ tích hợp | Tìm kiếm/lọc nếu kịp | Danh sách ôn tập đơn giản | Dashboard, nhắc học |
| Ngày 6 | Sửa lỗi tích hợp | Sửa lỗi dữ liệu | Sửa lỗi học tập | Sửa lỗi thống kê |
| Ngày 7 | Slide + demo auth | Slide + demo từ vựng | Slide + demo học | Slide + demo tiến độ |

## 6. Chức năng nên cắt nếu thiếu thời gian

Nếu app chưa ổn định, cắt các phần sau trước:

* Google login.
* Import/export CSV hoặc Excel.
* AI/LLM.
* Biểu đồ phức tạp.
* Push notification nâng cao.
* Chia sẻ bộ từ.
* Offline mode.

Không nên cắt:

* Đăng nhập.
* Quản lý từ vựng.
* Flashcard.
* Lưu kết quả học.
* Dashboard cơ bản.

## 7. Luồng demo chính

Luồng demo nên ngắn và chắc:

1. Mở app.
2. Đăng nhập hoặc đăng ký.
3. Xem hồ sơ hoặc mục tiêu học.
4. Tạo một bộ từ.
5. Thêm vài từ vựng.
6. Vào học flashcard.
7. Chọn mức độ nhớ.
8. Làm một bài luyện tập ngắn.
9. Xem dashboard tiến độ.
10. Xem phần nhắc học.

## 8. Quy tắc tránh vỡ tiến độ

* Không thêm chức năng mới sau ngày 5 nếu chưa xong luồng chính.
* Không đổi dữ liệu dùng chung nếu chưa báo nhóm.
* Không để một thành viên giữ code quá lâu mà không commit.
* Không đợi UI đẹp mới test logic.
* Không để ngày cuối mới chạy thử trên thiết bị.
* Không cố làm chức năng mở rộng nếu demo chính chưa ổn.

## 9. Tiêu chí hoàn thành trong 1 tuần

Hoàn thành nếu:

* App chạy được trên thiết bị hoặc emulator.
* Có đủ luồng demo chính.
* Mỗi thành viên có phần code và phần trình bày.
* Tài liệu phân công, kế hoạch, kiến trúc và kiểm thử đã cập nhật.
* Không có lỗi crash trong luồng demo.
* Có dữ liệu mẫu để thầy xem được chức năng.
