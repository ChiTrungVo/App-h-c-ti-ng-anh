# Cấu trúc thư mục dự án MinLish

## 1. Mục tiêu

Cấu trúc thư mục được tổ chức theo module chức năng để nhóm 4 thành viên có thể làm song song, hạn chế đụng file và dễ kiểm soát trách nhiệm.

Tài liệu này chỉ định hướng tổ chức thư mục, không bắt buộc tên class hoặc tên file code chi tiết.

## 2. Cấu trúc tài liệu

```text
docs
├── README.md
├── 00_mo_ta.md
├── project
│   ├── 01_phan_cong_cong_viec.md
│   ├── 02_phan_cong_chi_tiet_4_thanh_vien.md
│   ├── 03_ke_hoach_1_tuan.md
│   ├── 04_quy_uoc_du_an.md
│   └── 05_cau_truc_thu_muc.md
├── architecture
│   ├── 01_kien_truc_he_thong.md
│   ├── 02_database_appwrite.md
│   └── 03_backend_appwrite.md
├── design
│   └── 01_ui_ux_va_screen_flow.md
└── testing
    └── 01_test_plan.md
```

## 3. Nhóm thư mục source code đề xuất

Source code nên được chia thành các khu vực:

* Khu vực dùng chung cho cấu hình app, điều hướng, component dùng lại và tiện ích.
* Khu vực module tài khoản và hồ sơ.
* Khu vực module quản lý từ vựng.
* Khu vực module học flashcard và SRS.
* Khu vực module luyện tập.
* Khu vực module thống kê tiến độ.
* Khu vực module nhắc học.
* Khu vực model hoặc kiểu dữ liệu dùng chung nếu nhóm thấy cần.

Tên thư mục cụ thể có thể điều chỉnh khi nhóm thống nhất quy ước code.

## 4. Ý nghĩa cách chia thư mục

| Khu vực | Vai trò |
| --- | --- |
| Dùng chung | Chứa phần được nhiều module sử dụng |
| Tài khoản/hồ sơ | Chứa luồng đăng nhập, đăng ký, hồ sơ |
| Từ vựng | Chứa quản lý bộ từ và từ vựng |
| Học tập | Chứa flashcard, SRS và kế hoạch học |
| Luyện tập | Chứa quiz và bài luyện tập |
| Tiến độ | Chứa dashboard và thống kê |
| Nhắc học | Chứa cài đặt và xử lý notification |

## 5. Quy tắc làm việc với cấu trúc thư mục

* Thành viên chỉ tạo file trong module mình phụ trách, trừ khi có thống nhất chung.
* Logic riêng của module đặt trong khu vực module đó.
* Logic dùng chung nhiều module mới đưa vào khu vực dùng chung.
* UI component dùng lại nhiều màn hình nên được tách khỏi màn hình cụ thể.
* Không đặt tất cả màn hình vào một thư mục chung vì dễ xung đột khi nhiều người code.
* Source code chia theo module, nhưng Appwrite chỉ dùng một database chung.
* Không tạo file code rỗng nếu chưa có nhu cầu thật.

## 6. Khi nào được thêm thư mục mới

Chỉ thêm thư mục mới khi:

* Có chức năng mới cần tách riêng.
* Thư mục hiện tại đã quá nhiều trách nhiệm.
* Nội dung được dùng chung bởi nhiều module.
* Nhóm thống nhất rằng việc tách thư mục giúp code dễ hiểu hơn.
