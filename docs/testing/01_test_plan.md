# Kế hoạch kiểm thử MinLish

## 1. Mục tiêu kiểm thử

Đảm bảo ứng dụng hoạt động ổn định trong các luồng chính:

* Đăng ký và đăng nhập.
* Tạo bộ từ và thêm từ.
* Học flashcard và lưu kết quả SRS.
* Luyện tập quiz.
* Xem dashboard tiến độ.
* Nhận nhắc học.

## 2. Unit test

### SRS Scheduler

| Test case | Kết quả mong đợi |
| --- | --- |
| Chọn Again | Thời điểm ôn tiếp theo gần hơn, khoảng cách ôn giảm hoặc về mức ngắn nhất |
| Chọn Hard | interval tăng nhẹ |
| Chọn Good | interval tăng bình thường |
| Chọn Easy | Khoảng cách ôn tăng nhiều hơn, độ dễ nhớ tăng |

### Progress calculation

| Test case | Kết quả mong đợi |
| --- | --- |
| 8 đúng, 2 sai | accuracy = 80% |
| Học liên tục 3 ngày | streak = 3 |
| Nghỉ 1 ngày | streak reset hoặc tính theo quy tắc thống nhất |

### Validation

| Test case | Kết quả mong đợi |
| --- | --- |
| Email sai format | Hiển thị lỗi |
| Password quá ngắn | Hiển thị lỗi |
| Word rỗng | Không cho lưu từ |
| Meaning rỗng | Không cho lưu từ |

## 3. Manual test theo module

### User Management

* Đăng ký tài khoản mới.
* Đăng nhập bằng tài khoản đã tạo.
* Đăng xuất.
* Cập nhật tên, mục tiêu học và level.
* Kiểm tra lỗi khi nhập sai mật khẩu.

### Vocabulary Management

* Tạo bộ từ mới.
* Sửa tên và mô tả bộ từ.
* Xóa bộ từ.
* Thêm từ mới.
* Sửa từ.
* Xóa từ.
* Tìm kiếm theo word.
* Lọc theo tag.

### Learning

* Mở flashcard từ một bộ từ.
* Lật thẻ.
* Chọn Again, Hard, Good, Easy.
* Kiểm tra kết quả học được lưu.
* Kiểm tra từ đến hạn xuất hiện trong danh sách ôn.

### Practice

* Làm quiz chọn nghĩa đúng.
* Làm quiz nhập lại từ.
* Kiểm tra câu đúng/sai.
* Kiểm tra kết quả quiz được lưu.

### Progress

* Kiểm tra số từ đã học.
* Kiểm tra số từ cần ôn hôm nay.
* Kiểm tra accuracy.
* Kiểm tra streak.

### Notification

* Cài giờ nhắc học.
* Kiểm tra notification xuất hiện.
* Tắt hoặc đổi giờ nhắc học.

## 4. Test tích hợp

### Luồng 1: Người dùng mới

```text
Register -> Create Profile -> Create Vocabulary Set -> Add Words -> Learn Flashcard -> View Progress
```

### Luồng 2: Người dùng quay lại ôn tập

```text
Login -> Daily Plan -> Review Due Words -> Practice Quiz -> View Dashboard
```

### Luồng 3: Quản lý từ

```text
Login -> Vocabulary -> Search Word -> Edit Word -> Learn Updated Word
```

## 5. Tiêu chí trước khi demo

* App build thành công.
* Không crash trong 3 luồng demo chính.
* Có dữ liệu mẫu để demo.
* Các màn hình chính có loading/empty/error state.
* Mỗi thành viên đã test module của mình.
* Nhóm đã test tích hợp trên ít nhất một thiết bị hoặc emulator.
