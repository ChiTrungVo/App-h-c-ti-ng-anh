# Phân công chi tiết 4 thành viên - MinLish App

## 1. Mục tiêu của tài liệu

Tài liệu này dùng để phân chia công việc cho nhóm 4 thành viên trong quá trình phát triển ứng dụng MinLish. Nội dung tập trung vào việc mỗi thành viên cần làm, phạm vi chịu trách nhiệm, sản phẩm bàn giao và ràng buộc phối hợp.

Tài liệu này không dựng sẵn code, không đề xuất tên class, không đề xuất tên file code cụ thể và không chốt sẵn schema database chi tiết. Các quyết định kỹ thuật như tên class, tên màn hình, tên collection, tên field và kiểu dữ liệu sẽ được nhóm thống nhất trong quá trình thiết kế chi tiết trước khi triển khai.

## 2. Kết quả rà soát phân công

Phân công theo module dọc là hợp lý cho nhóm 4 người vì mỗi thành viên đều có phần:

* Thiết kế chức năng.
* Thiết kế giao diện.
* Lập trình mobile.
* Xử lý logic nghiệp vụ.
* Làm việc với dữ liệu.
* Kiểm thử.
* Viết tài liệu cho phần mình phụ trách.

Cách chia này phù hợp với môn lập trình di động hơn so với việc tách một người làm UI, một người làm backend, một người làm tài liệu. Mỗi thành viên đều phải tham gia code ứng dụng Android.

## 3. Nguyên tắc phân chia công việc

* Một database chung cho toàn bộ ứng dụng.
* Mỗi thành viên đề xuất nhu cầu dữ liệu cho module của mình.
* Nhóm thống nhất schema chung trước khi tạo thật trên Appwrite.
* Không tự ý thêm hoặc sửa cấu trúc dữ liệu nếu chưa thông báo nhóm.
* Không để một thành viên chỉ làm tài liệu, slide hoặc thiết kế.
* Stitch hoặc Figma chỉ dùng để hỗ trợ prototype và thống nhất giao diện.
* Giao diện cuối cùng phải được lập trình bằng Kotlin và Jetpack Compose.
* Chức năng mở rộng chỉ làm sau khi chức năng chính đã chạy ổn định.

## 4. Phân chia tổng quan

| Thành viên | Phạm vi chính | Mức độ khó | Ghi chú cân bằng |
| --- | --- | --- | --- |
| Thành viên 1 | Tài khoản, hồ sơ, cấu trúc điều hướng | Khá | Nắm nền tảng app và luồng người dùng |
| Thành viên 2 | Quản lý bộ từ và từ vựng | Khá | Nhiều màn hình nhập liệu và thao tác dữ liệu |
| Thành viên 3 | Học flashcard, SRS, kế hoạch học | Khá | Có thuật toán và trải nghiệm học chính |
| Thành viên 4 | Luyện tập, thống kê, nhắc học | Khá | Có logic quiz, tiến độ và tính năng mobile |

Khối lượng được xem là tương đối cân bằng vì mỗi thành viên đều có phần UI, logic, dữ liệu, kiểm thử và tài liệu. Nếu trong quá trình làm thấy một thành viên quá tải, nhóm ưu tiên hỗ trợ phần UI hoặc test trước, không chuyển toàn bộ module.

## 5. Thành viên 1 - Tài khoản, hồ sơ và cấu trúc ứng dụng

### 5.1. Mục tiêu

Đảm bảo người dùng có thể vào ứng dụng, quản lý tài khoản cá nhân và di chuyển giữa các khu vực chính của app.

### 5.2. Nội dung cần làm

#### Phân tích

* Xác định luồng người dùng mới vào app.
* Xác định luồng người dùng đã đăng nhập.
* Xác định thông tin hồ sơ cần có để phục vụ việc học.
* Thống nhất với các thành viên khác cách nhận biết người dùng hiện tại.

#### Thiết kế UI/UX

* Thiết kế màn hình đăng nhập.
* Thiết kế màn hình đăng ký.
* Thiết kế màn hình hồ sơ cá nhân.
* Thiết kế màn hình chỉnh sửa hồ sơ.
* Thiết kế bố cục điều hướng chính của app.
* Đảm bảo các màn hình có trạng thái tải, lỗi và dữ liệu rỗng nếu cần.

#### Lập trình mobile

* Triển khai luồng đăng ký, đăng nhập và đăng xuất.
* Triển khai hiển thị hồ sơ người dùng.
* Triển khai cập nhật thông tin hồ sơ.
* Triển khai điều hướng giữa các khu vực chính.
* Xử lý validate dữ liệu người dùng nhập vào.

#### Backend và dữ liệu

* Làm việc với hệ thống xác thực của Appwrite.
* Đề xuất các thông tin hồ sơ cần lưu.
* Thống nhất quyền truy cập dữ liệu hồ sơ với cả nhóm.
* Không tự ý tạo schema chi tiết nếu chưa được nhóm chốt.

#### Kiểm thử

* Kiểm tra đăng ký tài khoản mới.
* Kiểm tra đăng nhập đúng và sai thông tin.
* Kiểm tra đăng xuất.
* Kiểm tra cập nhật hồ sơ.
* Kiểm tra điều hướng sau khi đăng nhập.

#### Tài liệu

* Ghi lại luồng tài khoản.
* Ghi lại các màn hình đã phụ trách.
* Ghi lại các lỗi thường gặp khi đăng nhập và cách xử lý.

### 5.3. Sản phẩm bàn giao

* Người dùng có thể đăng ký, đăng nhập, đăng xuất.
* Người dùng có thể xem và sửa hồ sơ.
* App có luồng điều hướng chính ổn định.
* Có tài liệu mô tả phần tài khoản và hồ sơ.

### 5.4. Chức năng mở rộng nếu còn thời gian

* Đăng nhập bằng Google.
* Màn hình thiết lập mục tiêu học ban đầu.
* Cho phép người dùng chọn giờ nhắc học trong hồ sơ, phần xử lý nhắc học phối hợp với thành viên 4.

## 6. Thành viên 2 - Quản lý bộ từ vựng và từ vựng

### 6.1. Mục tiêu

Đảm bảo người dùng có thể tạo, xem, sửa, xóa và tổ chức các bộ từ vựng phục vụ cho việc học.

### 6.2. Nội dung cần làm

#### Phân tích

* Xác định thông tin cần có của một bộ từ.
* Xác định thông tin cần có của một từ vựng.
* Xác định nhu cầu tìm kiếm, lọc và phân loại từ.
* Thống nhất dữ liệu từ vựng cần cung cấp cho module học và luyện tập.

#### Thiết kế UI/UX

* Thiết kế màn hình danh sách bộ từ.
* Thiết kế màn hình chi tiết bộ từ.
* Thiết kế màn hình tạo và chỉnh sửa bộ từ.
* Thiết kế màn hình thêm và chỉnh sửa từ vựng.
* Thiết kế khu vực tìm kiếm và lọc.
* Đảm bảo form nhập liệu rõ ràng, dễ dùng trên thiết bị di động.

#### Lập trình mobile

* Triển khai hiển thị danh sách bộ từ.
* Triển khai thêm, sửa và xóa bộ từ.
* Triển khai hiển thị danh sách từ trong một bộ từ.
* Triển khai thêm, sửa và xóa từ vựng.
* Triển khai tìm kiếm và lọc cơ bản.
* Xử lý validate dữ liệu bắt buộc khi người dùng nhập từ.

#### Backend và dữ liệu

* Đề xuất dữ liệu cần lưu cho bộ từ và từ vựng.
* Thống nhất cách liên kết dữ liệu từ vựng với người dùng hiện tại.
* Thống nhất cách module học và module luyện tập lấy dữ liệu từ vựng.
* Không tự ý chốt tên collection, tên field hoặc kiểu dữ liệu nếu chưa được nhóm thống nhất.

#### Kiểm thử

* Kiểm tra tạo bộ từ.
* Kiểm tra sửa bộ từ.
* Kiểm tra xóa bộ từ.
* Kiểm tra thêm từ mới.
* Kiểm tra sửa và xóa từ.
* Kiểm tra tìm kiếm và lọc.
* Kiểm tra trường hợp danh sách rỗng.

#### Tài liệu

* Ghi lại luồng quản lý từ vựng.
* Ghi lại các trường thông tin từ vựng ở mức mô tả nghiệp vụ.
* Ghi lại cách module khác sử dụng dữ liệu từ vựng.

### 6.3. Sản phẩm bàn giao

* Người dùng quản lý được bộ từ vựng.
* Người dùng quản lý được từ vựng trong từng bộ từ.
* Module học và luyện tập có thể sử dụng danh sách từ.
* Có tài liệu mô tả phần quản lý từ vựng.

### 6.4. Chức năng mở rộng nếu còn thời gian

* Import danh sách từ từ file.
* Export bộ từ.
* Đánh dấu từ khó hoặc từ yêu thích.

## 7. Thành viên 3 - Học flashcard, SRS và kế hoạch học

### 7.1. Mục tiêu

Đảm bảo người dùng có thể học từ bằng flashcard và hệ thống có thể gợi ý thời điểm ôn lại từ dựa trên mức độ ghi nhớ.

### 7.2. Nội dung cần làm

#### Phân tích

* Xác định luồng học một phiên flashcard.
* Xác định các mức đánh giá ghi nhớ của người học.
* Xác định cách chọn từ mới và từ cần ôn.
* Xác định cách cập nhật trạng thái học sau mỗi lượt học.
* Thống nhất với thành viên 4 dữ liệu nào được dùng để tính tiến độ.

#### Thiết kế UI/UX

* Thiết kế màn hình kế hoạch học trong ngày.
* Thiết kế màn hình flashcard.
* Thiết kế trạng thái mặt trước và mặt sau của thẻ.
* Thiết kế thao tác đánh giá mức độ nhớ.
* Thiết kế màn hình kết quả sau phiên học.
* Đảm bảo thao tác học nhanh, dễ bấm và phù hợp màn hình nhỏ.

#### Lập trình mobile

* Triển khai giao diện học flashcard.
* Triển khai hiệu ứng lật thẻ.
* Triển khai luồng chuyển qua từ tiếp theo.
* Triển khai xử lý lựa chọn mức độ ghi nhớ.
* Triển khai kế hoạch học trong ngày ở mức cơ bản.
* Xử lý trường hợp không có từ để học hoặc ôn.

#### Backend và dữ liệu

* Đề xuất dữ liệu cần lưu để biết một từ đã học tới đâu.
* Đề xuất dữ liệu cần lưu để biết khi nào cần ôn lại.
* Thống nhất cách lưu kết quả học để module thống kê sử dụng.
* Không tự ý chốt tên collection, tên field hoặc công thức lưu trữ nếu chưa được nhóm thống nhất.

#### Kiểm thử

* Kiểm tra flashcard hiển thị đúng nội dung.
* Kiểm tra thao tác lật thẻ.
* Kiểm tra các lựa chọn mức độ ghi nhớ.
* Kiểm tra từ đã học được cập nhật trạng thái.
* Kiểm tra danh sách từ cần ôn trong ngày.
* Kiểm tra trường hợp bộ từ không có dữ liệu.

#### Tài liệu

* Ghi lại luồng học flashcard.
* Ghi lại nguyên tắc SRS ở mức nghiệp vụ.
* Ghi lại cách tính lịch ôn ở mức mô tả, không cần chốt chi tiết kỹ thuật trong tài liệu phân công.

### 7.3. Sản phẩm bàn giao

* Người dùng học được từ bằng flashcard.
* Người dùng đánh giá được mức độ ghi nhớ.
* Hệ thống lưu được kết quả học để phục vụ ôn tập.
* Có kế hoạch học trong ngày ở mức cơ bản.
* Có tài liệu mô tả phần học và SRS.

### 7.4. Chức năng mở rộng nếu còn thời gian

* Gợi ý số từ mới phù hợp với hiệu suất học.
* Chế độ học riêng cho từ khó.
* Tùy chỉnh số từ học mỗi ngày.

## 8. Thành viên 4 - Luyện tập, thống kê và nhắc học

### 8.1. Mục tiêu

Đảm bảo người dùng có thể luyện tập lại từ vựng, xem tiến độ học và nhận nhắc học hằng ngày.

### 8.2. Nội dung cần làm

#### Phân tích

* Xác định các dạng luyện tập phù hợp với từ vựng.
* Xác định dữ liệu cần để tính số từ đã học, độ chính xác và chuỗi ngày học.
* Xác định thông tin cần hiển thị trên dashboard.
* Xác định cách nhắc học phù hợp với ứng dụng mobile.

#### Thiết kế UI/UX

* Thiết kế màn hình chọn dạng luyện tập.
* Thiết kế màn hình câu hỏi luyện tập.
* Thiết kế màn hình kết quả luyện tập.
* Thiết kế dashboard tiến độ học.
* Thiết kế màn hình cài đặt nhắc học.
* Đảm bảo số liệu thống kê dễ đọc và không quá rối.

#### Lập trình mobile

* Triển khai ít nhất hai dạng luyện tập cơ bản.
* Triển khai kiểm tra câu trả lời đúng hoặc sai.
* Triển khai lưu kết quả luyện tập.
* Triển khai dashboard tiến độ cơ bản.
* Triển khai tính độ chính xác và chuỗi ngày học.
* Triển khai nhắc học hằng ngày ở mức cơ bản.

#### Backend và dữ liệu

* Đề xuất dữ liệu cần lưu cho kết quả luyện tập.
* Đề xuất dữ liệu cần lưu cho thống kê học tập.
* Phối hợp với thành viên 3 để thống kê dùng chung dữ liệu học.
* Phối hợp với thành viên 1 nếu cài đặt nhắc học nằm trong hồ sơ.
* Không tự ý chốt tên collection, tên field hoặc kiểu dữ liệu nếu chưa được nhóm thống nhất.

#### Kiểm thử

* Kiểm tra tạo câu hỏi luyện tập từ dữ liệu từ vựng.
* Kiểm tra trả lời đúng và sai.
* Kiểm tra lưu kết quả luyện tập.
* Kiểm tra dashboard sau khi học hoặc luyện tập.
* Kiểm tra tính chuỗi ngày học.
* Kiểm tra nhắc học hằng ngày.

#### Tài liệu

* Ghi lại các dạng luyện tập.
* Ghi lại cách tính thống kê ở mức nghiệp vụ.
* Ghi lại cơ chế nhắc học và giới hạn triển khai.

### 8.3. Sản phẩm bàn giao

* Người dùng luyện tập được từ vựng.
* Người dùng xem được dashboard tiến độ.
* Hệ thống tính được độ chính xác và chuỗi ngày học ở mức cơ bản.
* Có nhắc học hằng ngày.
* Có tài liệu mô tả phần luyện tập, thống kê và nhắc học.

### 8.4. Chức năng mở rộng nếu còn thời gian

* Biểu đồ hoạt động học theo ngày.
* Biểu đồ tỷ lệ ghi nhớ.
* Thông báo nâng cao cho từ đến hạn ôn.

## 9. Công việc chung của cả nhóm

### 9.1. Trước khi code

* Chốt phạm vi chức năng chính.
* Chốt chức năng mở rộng nào sẽ làm nếu còn thời gian.
* Thống nhất phong cách giao diện.
* Thống nhất luồng màn hình.
* Thống nhất dữ liệu cần lưu ở mức nghiệp vụ.
* Thống nhất cách phân quyền dữ liệu.
* Chỉ sau khi thống nhất mới tạo cấu trúc dữ liệu thật trên Appwrite.

### 9.2. Trong khi code

* Mỗi thành viên làm trên phần mình phụ trách.
* Khi cần dùng dữ liệu của module khác, phải trao đổi trước.
* Khi muốn thay đổi dữ liệu dùng chung, phải cập nhật tài liệu và báo nhóm.
* Không sửa phần của thành viên khác nếu chưa thống nhất.
* Mỗi chức năng phải có xử lý tải dữ liệu, lỗi và dữ liệu rỗng nếu phù hợp.

### 9.3. Sau khi code

* Tự kiểm thử module của mình.
* Tích hợp với các module liên quan.
* Kiểm thử luồng demo chính.
* Cập nhật tài liệu phần đã làm.
* Chuẩn bị nội dung trình bày cá nhân.

## 10. Ràng buộc về database

* Dùng một database chung cho toàn bộ ứng dụng.
* Không chia database riêng theo thành viên.
* Không tạo sẵn schema chi tiết trong tài liệu phân công này.
* Mỗi thành viên chỉ mô tả nhu cầu dữ liệu của module mình.
* Nhóm cần có một người chịu trách nhiệm tổng hợp và kiểm tra tính thống nhất dữ liệu.
* Tên collection, tên field, kiểu dữ liệu và index chỉ chốt sau buổi thống nhất schema.
* Sau khi schema đã chốt, mọi thay đổi phải được thông báo cho cả nhóm.

## 11. Ràng buộc về UI

* Có thể dùng Stitch hoặc Figma để hỗ trợ prototype.
* Không xem prototype là sản phẩm lập trình cuối cùng.
* Giao diện cuối cùng phải được code bằng Jetpack Compose.
* Các màn hình phải thống nhất màu sắc, spacing và kiểu nút.
* Không để mỗi module có một phong cách giao diện khác nhau.
* Phải kiểm tra giao diện trên màn hình nhỏ.

## 12. Ràng buộc về chất lượng

* Chức năng chính phải hoàn thành trước chức năng mở rộng.
* Không thêm chức năng mới khi luồng chính chưa ổn định.
* Không bỏ qua xử lý lỗi cơ bản.
* Không hard-code dữ liệu demo vào luồng chính nếu dữ liệu đó cần lấy từ backend.
* Mỗi thành viên phải có phần đóng góp code mobile rõ ràng.
* Mỗi thành viên phải có phần trình bày khi báo cáo.

## 13. Tiêu chí hoàn thành chung

Ứng dụng được xem là đạt phạm vi chính khi:

* Người dùng đăng ký và đăng nhập được.
* Người dùng tạo được bộ từ và thêm từ vựng.
* Người dùng học được bằng flashcard.
* Hệ thống lưu được kết quả học để phục vụ ôn tập.
* Người dùng luyện tập được bằng quiz.
* Người dùng xem được tiến độ học cơ bản.
* Người dùng nhận được nhắc học hằng ngày hoặc có cơ chế nhắc học ở mức demo.
* Các module chính tích hợp được với nhau trong một luồng demo.

## 14. Gợi ý trình bày khi báo cáo

Mỗi thành viên trình bày theo cấu trúc:

1. Module phụ trách.
2. Mục tiêu của module.
3. Các màn hình đã làm.
4. Logic chính đã xử lý.
5. Phần dữ liệu có liên quan ở mức nghiệp vụ.
6. Kết quả demo.
7. Khó khăn và hướng mở rộng.

