# Project

# Mô tả chi tiết đồ án: MinLish App - Ứng dụng hỗ trợ học từ vựng tiếng Anh

## 1. Giới thiệu

### 1.1. Tên đề tài

**MinLish App - Ứng dụng hỗ trợ học từ vựng tiếng Anh trên thiết bị di động**

### 1.2. Mục tiêu

MinLish là ứng dụng hỗ trợ người học ghi nhớ và ôn tập từ vựng tiếng Anh hiệu quả thông qua các phương pháp học hiện đại:

* Flashcard.
* Spaced Repetition System (SRS - lặp lại ngắt quãng).
* Học từ vựng theo ngữ cảnh thông qua ví dụ, collocation và từ liên quan.

Ứng dụng hướng đến việc giúp người học không chỉ ghi nhớ nghĩa của từ, mà còn biết cách sử dụng từ trong câu và trong các ngữ cảnh thực tế.

### 1.3. Phạm vi hệ thống

Trong phạm vi đồ án, ứng dụng tập trung vào các chức năng chính:

* Đăng ký, đăng nhập và quản lý hồ sơ người dùng.
* Tạo và quản lý các bộ từ vựng.
* Thêm, sửa, xóa từ vựng trong từng bộ từ.
* Học từ bằng flashcard.
* Ôn tập từ vựng theo cơ chế lặp lại ngắt quãng.
* Luyện tập từ vựng bằng các dạng bài cơ bản.
* Theo dõi tiến độ học tập.
* Nhắc nhở học tập hằng ngày.

Ngoài các chức năng chính, hệ thống có thể mở rộng thêm các tính năng như đăng nhập Google, import/export dữ liệu, biểu đồ thống kê, gợi ý kế hoạch học và thông báo thông minh.

### 1.4. Đối tượng người dùng

Ứng dụng phù hợp với:

* Học sinh, sinh viên.
* Người học IELTS, TOEIC hoặc tiếng Anh giao tiếp.
* Người đi làm cần mở rộng vốn từ vựng tiếng Anh.
* Người tự học muốn có công cụ theo dõi và nhắc ôn tập.

## 2. Công nghệ sử dụng

* Nền tảng phát triển: Android.
* Ngôn ngữ lập trình: Kotlin.
* Giao diện người dùng: Jetpack Compose.
* Backend-as-a-Service: Appwrite.
* Xác thực người dùng: Appwrite Auth.
* Cơ sở dữ liệu: Appwrite Databases.
* Lưu trữ tài nguyên: Appwrite Storage.
* Xử lý logic backend: Appwrite Functions, nếu cần.
* Đồng bộ dữ liệu thời gian thực: Appwrite Realtime, nếu cần.
* Công cụ phát triển: Android Studio.
* Công cụ quản lý mã nguồn: Git và GitHub.

## 3. Mô tả tổng quan hệ thống

Hệ thống được xây dựng theo mô hình client-server. Ứng dụng Android đóng vai trò là client, cung cấp giao diện học tập và xử lý các thao tác của người dùng. Appwrite đóng vai trò là backend, phụ trách xác thực tài khoản, lưu trữ dữ liệu từ vựng, tiến độ học tập và các cấu hình học tập của người dùng.

Dữ liệu chính của hệ thống bao gồm:

* Thông tin người dùng.
* Bộ từ vựng.
* Từ vựng trong từng bộ.
* Kết quả học và ôn tập.
* Lịch ôn tập tiếp theo.
* Thống kê tiến độ học tập.

## 4. Các module chính

Hệ thống gồm các module chính sau:

1. User Management.
2. Vocabulary Management.
3. Learning Engine.
4. Practice Module.
5. Analytics & Progress.
6. Notification System.

## 5. Chức năng hệ thống

### 5.1. User Management

#### 5.1.1. Đăng ký và đăng nhập

Người dùng có thể tạo tài khoản và đăng nhập vào hệ thống bằng:

* Email và mật khẩu.
* Google login, nếu triển khai mở rộng.

Mật khẩu người dùng được quản lý thông qua Appwrite Auth. Hệ thống chỉ cho phép người dùng truy cập vào dữ liệu thuộc tài khoản của mình.

#### 5.1.2. Hồ sơ người dùng

Người dùng có thể quản lý thông tin cá nhân gồm:

* Họ tên.
* Mục tiêu học tập: IELTS, TOEIC, giao tiếp, công việc.
* Trình độ tiếng Anh: A1 đến C2.
* Số từ mới muốn học mỗi ngày.
* Thời gian nhắc học hằng ngày.

### 5.2. Vocabulary Management

#### 5.2.1. Quản lý bộ từ vựng

Người dùng có thể tạo các bộ từ vựng theo từng chủ đề. Mỗi bộ từ gồm:

* Tên bộ từ.
* Mô tả.
* Tags, ví dụ: IELTS, Business, Travel, Daily English.
* Số lượng từ trong bộ.
* Ngày tạo và ngày cập nhật.

Người dùng có thể xem danh sách bộ từ, xem chi tiết, cập nhật thông tin hoặc xóa bộ từ không còn sử dụng.

#### 5.2.2. Quản lý từ vựng

Mỗi từ vựng bao gồm các thông tin:

* Word.
* Pronunciation.
* Meaning.
* Description bằng tiếng Anh.
* Example.
* Collocation.
* Related words.
* Note.
* Tag hoặc chủ đề liên quan.

Người dùng có thể thêm mới, chỉnh sửa hoặc xóa từ vựng trong từng bộ từ.

#### 5.2.3. Tìm kiếm và lọc từ vựng

Người dùng có thể:

* Tìm kiếm từ vựng theo từ khóa.
* Lọc từ vựng theo bộ từ.
* Lọc từ vựng theo tag.
* Xem các từ đang đến hạn ôn tập.

#### 5.2.4. Import và export dữ liệu

Chức năng mở rộng:

* Import danh sách từ vựng từ file CSV hoặc Excel.
* Export bộ từ vựng để sao lưu hoặc chia sẻ.

### 5.3. Learning Module

#### 5.3.1. Flashcard Learning

Người dùng học từ thông qua flashcard:

* Mặt trước hiển thị từ tiếng Anh.
* Mặt sau hiển thị nghĩa, ví dụ, collocation và ghi chú.
* Có hiệu ứng lật thẻ để tăng trải nghiệm học.
* Có thể chuyển sang từ tiếp theo hoặc quay lại từ trước đó.

#### 5.3.2. Spaced Repetition System

Ứng dụng áp dụng thuật toán SM-2 để tính thời điểm ôn tập tiếp theo.

Sau khi học hoặc ôn một từ, người dùng chọn mức độ ghi nhớ:

* Again.
* Hard.
* Good.
* Easy.

Dựa trên lựa chọn này, hệ thống tính toán:

* Thời gian ôn tập tiếp theo.
* Mức độ ghi nhớ của từ.
* Ease factor.
* Số lần đã ôn tập.
* Trạng thái học của từ.

#### 5.3.3. Daily Learning Plan

Hệ thống hỗ trợ lập kế hoạch học mỗi ngày:

* Số từ mới cần học trong ngày.
* Số từ đến hạn cần ôn.
* Tiến độ hoàn thành mục tiêu trong ngày.

Chức năng mở rộng có thể gợi ý số từ mới phù hợp dựa trên thói quen học và độ chính xác của người dùng.

### 5.4. Practice Module

Người dùng có thể luyện tập từ vựng bằng các dạng bài:

* Chọn nghĩa đúng của từ.
* Nhập lại từ dựa trên nghĩa.
* Nhận diện từ qua ví dụ.
* Ôn tập các từ đến hạn.
* Luyện tập riêng các từ khó hoặc từ hay sai.

Chức năng này giúp người học kiểm tra khả năng ghi nhớ thay vì chỉ xem lại flashcard.

### 5.5. Analytics & Progress

#### 5.5.1. Dashboard

Dashboard hiển thị tổng quan tiến độ học tập:

* Tổng số từ đã học.
* Số từ cần ôn hôm nay.
* Số từ mới đã học trong ngày.
* Streak học tập.
* Tỷ lệ trả lời đúng.
* Số bộ từ đang học.

#### 5.5.2. Biểu đồ thống kê

Chức năng mở rộng:

* Biểu đồ hoạt động học theo ngày.
* Biểu đồ tỷ lệ ghi nhớ.
* Thống kê số từ mới và số từ đã ôn.
* Thống kê theo bộ từ hoặc tag.

#### 5.5.3. Ước lượng trình độ

Hệ thống có thể ước lượng trình độ người học dựa trên số lượng từ đã học, độ chính xác và mức độ ghi nhớ:

* Beginner.
* Intermediate.
* Advanced.

### 5.6. Notification System

Hệ thống hỗ trợ nhắc nhở học tập:

* Nhắc học hằng ngày theo giờ người dùng cài đặt.
* Nhắc các từ đến hạn ôn tập.
* Thông báo khi hoàn thành mục tiêu ngày.
* Push notification hoặc email notification nếu triển khai mở rộng.

### 5.7. AI hoặc LLM cơ bản

Chức năng mở rộng có thể tích hợp AI/LLM để tăng giá trị ứng dụng:

* Tạo thêm ví dụ cho một từ vựng.
* Gợi ý collocation phổ biến.
* Gợi ý related words.
* Giải thích cách dùng từ bằng tiếng Anh đơn giản.
* Tạo câu hỏi luyện tập từ danh sách từ vựng.

Phần AI/LLM được xem là chức năng mở rộng, không ảnh hưởng đến luồng chính của ứng dụng nếu chưa triển khai kịp.

## 6. Yêu cầu phi chức năng

### 6.1. Performance

* Thời gian tải màn hình chính dưới 2 giây trong điều kiện mạng ổn định.
* Dữ liệu học tập được truy xuất nhanh và ổn định.
* Ứng dụng có khả năng mở rộng khi số lượng người dùng tăng.

### 6.2. Security

* Xác thực người dùng thông qua Appwrite Auth.
* Chỉ cho phép người dùng truy cập dữ liệu thuộc tài khoản của mình.
* Không lưu mật khẩu dạng plain text trong ứng dụng.
* Kiểm tra và phân quyền truy cập với các collection quan trọng.

### 6.3. Usability

* Giao diện đơn giản, dễ sử dụng.
* Các thao tác học từ nhanh và trực quan.
* Phù hợp với người học sử dụng hằng ngày.
* Tối ưu trải nghiệm trên thiết bị di động.

### 6.4. Maintainability

* Mã nguồn được chia theo module rõ ràng.
* Tách riêng tầng giao diện, xử lý nghiệp vụ và truy cập dữ liệu.
* Dễ dàng bổ sung tính năng mới như import/export, AI hoặc thống kê nâng cao.

## 7. Chức năng mở rộng đề xuất

Nếu còn thời gian, ứng dụng có thể triển khai thêm:

* Google login.
* Import và export CSV/Excel.
* Biểu đồ thống kê tiến độ học tập.
* Push notification.
* Gợi ý số từ mới mỗi ngày.
* Chế độ học theo mục tiêu IELTS, TOEIC hoặc giao tiếp.
* Tìm kiếm và lọc từ vựng theo tag.
* Đánh dấu từ khó.
* Chia sẻ bộ từ vựng cho người dùng khác.
* Tạo ví dụ, collocation hoặc câu hỏi luyện tập bằng AI/LLM.

## 8. Kết luận

MinLish là ứng dụng hỗ trợ học từ vựng tiếng Anh theo hướng cá nhân hóa, giúp người học ghi nhớ từ lâu hơn thông qua flashcard, lặp lại ngắt quãng và học theo ngữ cảnh. Hệ thống không chỉ hỗ trợ việc lưu trữ từ vựng mà còn theo dõi tiến độ, nhắc nhở học tập và tối ưu lịch ôn tập cho từng người dùng.

Với các chức năng mở rộng như import/export, thống kê nâng cao, thông báo thông minh và tích hợp AI/LLM cơ bản, ứng dụng có khả năng phát triển thêm sau phạm vi đồ án và tạo điểm cộng về tính ứng dụng thực tế.
6