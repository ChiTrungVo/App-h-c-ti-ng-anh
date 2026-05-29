# UI/UX và screen flow MinLish

## 1. Nguyên tắc UI/UX

* Giao diện đơn giản, tập trung vào việc học hằng ngày.
* Mỗi màn hình chỉ nên có một hành động chính rõ ràng.
* Ưu tiên thao tác nhanh: học từ, ôn từ, xem tiến độ.
* Dùng Material 3 và Jetpack Compose.
* Stitch/Figma có thể dùng để tạo prototype tham khảo.
* Giao diện cuối cùng phải được lập trình bằng Jetpack Compose.

## 2. Màu sắc và phong cách

Đề xuất phong cách:

* Sạch, sáng, dễ đọc.
* Màu chính dùng cho hành động học tập.
* Màu cảnh báo dùng cho lỗi hoặc từ cần ôn.
* Màu thành công dùng cho tiến độ hoàn thành.

Không nên dùng giao diện quá rối hoặc quá nhiều màu vì ứng dụng phục vụ học tập thường xuyên.

## 3. Screen flow tổng quan

```text
Splash
 ├── Login
 │    └── Register
 └── Home
      ├── Vocabulary
      │    ├── Vocabulary Set List
      │    ├── Vocabulary Set Detail
      │    ├── Create/Edit Set
      │    └── Create/Edit Word
      ├── Learning
      │    ├── Daily Plan
      │    ├── Flashcard Session
      │    └── Session Result
      ├── Practice
      │    ├── Practice Type
      │    ├── Quiz Screen
      │    └── Quiz Result
      ├── Progress
      │    └── Dashboard
      └── Profile
           ├── Edit Profile
           └── Notification Settings
```

## 4. Danh sách màn hình theo thành viên

| Thành viên | Màn hình |
| --- | --- |
| Thành viên 1 | Splash, Login, Register, Profile, Edit Profile, Main Navigation |
| Thành viên 2 | Vocabulary Set List, Set Detail, Create/Edit Set, Create/Edit Word, Search/Filter |
| Thành viên 3 | Daily Plan, Flashcard Session, Session Result |
| Thành viên 4 | Practice Type, Quiz Screen, Quiz Result, Progress Dashboard, Notification Settings |

## 5. Component dùng chung

Nên thống nhất các nhóm component dùng chung:

* Thanh tiêu đề.
* Nút hành động chính và phụ.
* Ô nhập liệu.
* Trạng thái tải dữ liệu.
* Trạng thái dữ liệu rỗng.
* Thông báo lỗi.
* Thẻ hiển thị từ vựng.
* Thẻ hiển thị thống kê.

Tên component cụ thể sẽ được nhóm thống nhất khi bắt đầu code.

## 6. Trạng thái bắt buộc của màn hình

Mỗi màn hình lấy dữ liệu cần có:

* Loading state.
* Success state.
* Empty state nếu không có dữ liệu.
* Error state nếu lỗi mạng hoặc lỗi quyền truy cập.

## 7. Gợi ý prototype bằng Stitch/Figma

Có thể dùng Stitch/Figma cho:

* Login/Register.
* Home dashboard.
* Vocabulary list.
* Flashcard.
* Progress dashboard.

Khi chuyển sang code:

* Không copy máy móc nếu layout không phù hợp mobile.
* Ưu tiên component Material 3.
* Kiểm tra hiển thị trên màn hình nhỏ.
* Giữ style thống nhất giữa các thành viên.
