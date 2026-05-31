# Database Schema Tổng Hợp - 4 Thành Viên

---

## 1. Nguyên tắc thống nhất

* Dùng một database Appwrite chung cho toàn bộ ứng dụng.
* Appwrite Auth quản lý đăng ký, đăng nhập, mật khẩu, phiên đăng nhập, xác thực email và OAuth; database của app không lưu mật khẩu, access token hoặc refresh token.
* Mọi dữ liệu riêng tư phải có `userId` trỏ tới Appwrite Auth user `$id` và dùng document-level permission theo chủ sở hữu.
* Dữ liệu từ vựng chỉ có một nguồn chính là `vocabularies`.
* Khóa tham chiếu bộ từ luôn là `setId`, trỏ đến `vocabulary_sets.$id`.
* Khóa tham chiếu từ luôn là `wordId`, trỏ đến `vocabularies.$id`.
* Tiến độ học từng từ chỉ lưu ở `user_word_progress`.
* Dashboard theo ngày đọc từ `daily_learning_stats`.
* Field thời điểm dùng `datetime` ISO 8601; field ngày tổng hợp dùng string `YYYY-MM-DD` để query theo ngày.

---

## 2. Mô tả chi tiết chỉnh sửa của 3 thành viên trước

| Thành viên | Vấn đề ở bản trước | Chỉnh sửa đã gộp vào schema tổng hợp |
|---|---|---|
| Thành viên 2 - Bộ từ vựng & từ vựng | Schema đã có `vocabulary_sets` và `vocabularies` nhưng thiếu một số field phục vụ đồng bộ, xóa mềm và thống kê số từ. | Giữ `vocabulary_sets` và `vocabularies` làm nguồn dữ liệu từ vựng duy nhất; bổ sung `wordCount`, `updatedAt`, `deletedAt`; đổi `collocation` thành `collocations`; thêm index theo `userId`, `setId`, `word`, `updatedAt`; ghi rõ Module 3/4 chỉ tham chiếu qua `setId` và `wordId`. |
| Thành viên 3 - Học tập & tiến độ | Có collection `words` trùng với `vocabularies`; dùng `nextReviewDate` khác tên với Module 4; `daily_logs` tách khỏi thống kê quiz. | Xóa collection `words`; chuẩn hóa `user_word_progress` là nơi duy nhất lưu tiến độ từ; dùng `nextReviewAt`; thêm `setId`, `lastQuality`, index `userId + wordId`, `userId + nextReviewAt`; thay `daily_logs` bằng `daily_learning_stats` dùng chung với Module 4. |
| Thành viên 4 - Quiz & thống kê | Tạo lại `user_word_progress`, dùng `deckId`, có `user_deck_progress`, tài liệu bị lẫn bảng và `notification_settings` trùng trách nhiệm với hồ sơ người dùng. | Không tạo progress riêng; đổi `deckId` thành `setId`; tách rõ `quiz_attempts` và `quiz_answers`; Module 4 cập nhật `daily_learning_stats`; chuyển `notification_settings` về nhóm dữ liệu người dùng của Thành viên 1 để chỉ còn một nơi lưu cấu hình nhắc học. |

---

## 3. Bảng tổng hợp collection và trách nhiệm

| Collection | Thành viên/module sở hữu chính | Module đọc/cập nhật | Vai trò |
|---|---|---|---|
| `user_profiles` | Thành viên 1 | Module 2, 3, 4 đọc thông tin cơ bản nếu cần | Hồ sơ và cấu hình học tập cơ bản của người dùng |
| `notification_settings` | Thành viên 1 | Thành viên 4 dùng để nhắc học | Cài đặt giờ/ngày nhắc học và token thông báo |
| `vocabulary_sets` | Thành viên 2 | Thành viên 3, 4 đọc | Bộ từ vựng do người dùng tạo |
| `vocabularies` | Thành viên 2 | Thành viên 3, 4 đọc | Nguồn dữ liệu từ vựng duy nhất |
| `user_word_progress` | Thành viên 3 | Thành viên 4 cập nhật sau quiz | Trạng thái học và lịch ôn từng từ |
| `study_plans` | Thành viên 3 | Thành viên 4 đọc nếu cần dashboard | Kế hoạch học của người dùng |
| `daily_learning_stats` | Thành viên 3 | Thành viên 4 cộng dồn số liệu quiz | Thống kê học tập hằng ngày |
| `quiz_attempts` | Thành viên 4 | Thành viên 3 đọc nếu cần lịch sử học | Mỗi lượt làm quiz |
| `quiz_answers` | Thành viên 4 | Thành viên 3 dùng để cập nhật tiến độ | Chi tiết từng câu trả lời |

---

## 4. Module 1 - Xác thực & Quản lý người dùng

### 4.1. Collection: `user_profiles` (Hồ sơ người dùng)

Lưu dữ liệu hồ sơ mở rộng. Không lưu mật khẩu, token đăng nhập, refresh token, token reset password hoặc token OAuth vì các phần này thuộc Appwrite Auth.

#### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Appwrite Auth user `$id` |
| `displayName` | string | 255 | Yes | No | | Tên hiển thị trong app |
| `email` | string | 255 | Yes | No | | Email lấy từ Auth để hiển thị/tìm kiếm nội bộ |
| `avatarUrl` | string | 1000 | No | No | | URL ảnh đại diện |
| `phone` | string | 20 | No | No | | Số điện thoại nếu người dùng cung cấp |
| `bio` | string | 500 | No | No | | Giới thiệu ngắn |
| `nativeLanguage` | string | 50 | No | No | `vi` | Ngôn ngữ mẹ đẻ |
| `targetLanguage` | string | 50 | No | No | `en` | Ngôn ngữ đang học |
| `proficiencyLevel` | string | 50 | No | No | `beginner` | `beginner`, `intermediate`, `advanced` |
| `studyGoal` | string | 500 | No | No | | Mục tiêu học tập |
| `dailyTargetMinutes` | integer | | No | No | 0 | Số phút học mục tiêu mỗi ngày |
| `preferredLearningStyle` | string | 100 | No | No | | Flashcard, quiz, mixed... |
| `soundEnabled` | boolean | | No | No | true | Bật âm thanh |
| `darkModeEnabled` | boolean | | No | No | false | Bật chế độ tối |
| `status` | string | 50 | No | No | `active` | `active`, `inactive`, `suspended`, `deleted` |
| `lastLoginAt` | datetime | ISO 8601 | No | No | | Lần đăng nhập gần nhất nếu app cần hiển thị |
| `createdAt` | datetime | ISO 8601 | No | No | | Thời điểm tạo hồ sơ |
| `updatedAt` | datetime | ISO 8601 | No | No | | Thời điểm cập nhật gần nhất |

#### Indexes

| Index Key | Type | Attributes | Order | Mục đích |
|---|---|---|---|---|
| `idx_profiles_userId` | unique | `userId` | ASC | Mỗi tài khoản có một hồ sơ |
| `idx_profiles_email` | key | `email` | ASC | Tìm hồ sơ theo email nếu cần |
| `idx_profiles_status` | key | `status` | ASC | Lọc trạng thái tài khoản |

#### Permissions

* Chủ sở hữu: `read`, `update`.
* Admin: `read`, `update`, `delete` nếu nhóm có dashboard quản trị.
* Không cho người dùng sửa `userId`, `email`, `status`, `createdAt` nếu các field này do backend/function quản lý.

### 4.2. Collection: `notification_settings` (Cài đặt nhắc học)

Lưu cấu hình nhắc học ở nhóm dữ liệu người dùng để Module 4 chỉ đọc và sử dụng, tránh tạo thêm một schema nhắc học riêng ở Module 4.

#### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Appwrite Auth user `$id` |
| `reminderTime` | string | HH:mm | No | No | `08:00` | Giờ nhắc học theo timezone của app/thiết bị |
| `reminderDays` | string | 10 | No | Yes | | Danh sách ngày nhắc, ví dụ `MON`, `WED`, `FRI` |
| `timezone` | string | 64 | No | No | `Asia/Ho_Chi_Minh` | Timezone dùng để tính lịch nhắc |
| `isEnabled` | boolean | | No | No | true | Bật/tắt nhắc học |
| `fcmToken` | string | 500 | No | No | | Token FCM của thiết bị hiện tại nếu dùng push notification |
| `createdAt` | datetime | ISO 8601 | No | No | | Thời điểm tạo |
| `updatedAt` | datetime | ISO 8601 | No | No | | Thời điểm cập nhật gần nhất |

#### Indexes

| Index Key | Type | Attributes | Order | Mục đích |
|---|---|---|---|---|
| `idx_notifications_userId` | unique | `userId` | ASC | Mỗi người dùng có một cấu hình nhắc học |
| `idx_notifications_enabled` | key | `isEnabled` | ASC | Lọc người dùng đang bật nhắc học nếu có backend job |

---

## 5. Module 2 - Quản lý bộ từ vựng & từ vựng

### 5.1. Collection: `vocabulary_sets` (Bộ từ vựng)

Lưu trữ thông tin về thư mục/bộ từ vựng do người dùng tạo.

#### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Chủ sở hữu bộ từ; tham chiếu Appwrite Auth user `$id` |
| `title` | string | 255 | Yes | No | | Tên bộ từ, ví dụ `IELTS Topic 1` |
| `description` | string | 500 | No | No | | Mô tả ngắn gọn |
| `tags` | string | 50 | No | Yes | | Danh sách tag như `IELTS`, `Business` |
| `wordCount` | integer | | No | No | 0 | Số từ trong bộ, cập nhật khi thêm/xóa từ |
| `isPublic` | boolean | | No | No | false | Cho phép người khác xem/copy bộ từ trong phạm vi mở rộng |
| `createdAt` | datetime | ISO 8601 | No | No | | Thời điểm tạo |
| `updatedAt` | datetime | ISO 8601 | No | No | | Thời điểm cập nhật gần nhất |
| `deletedAt` | datetime | ISO 8601 | No | No | | Dùng cho xóa mềm; rỗng nghĩa là còn hiệu lực |

#### Indexes

| Index Key | Type | Attributes | Order | Mục đích |
|---|---|---|---|---|
| `idx_sets_userId` | key | `userId` | ASC | Lấy danh sách bộ từ của người dùng |
| `idx_sets_user_title` | key | `userId`, `title` | ASC | Tìm/kiểm tra trùng tên bộ từ trong tài khoản |
| `idx_sets_public` | key | `isPublic` | ASC | Lọc bộ từ công khai nếu mở rộng chia sẻ |
| `idx_sets_updatedAt` | key | `updatedAt` | DESC | Sắp xếp bộ từ mới cập nhật |

### 5.2. Collection: `vocabularies` (Từ vựng)

Lưu trữ chi tiết các từ trong từng bộ từ. Đây là nguồn `wordId` chính cho Module 3 và Module 4.

#### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `setId` | string | 36 | Yes | No | | Tham chiếu `vocabulary_sets.$id` |
| `userId` | string | 36 | Yes | No | | Chủ sở hữu từ; lặp lại để query nhanh và kiểm tra quyền |
| `word` | string | 255 | Yes | No | | Từ hoặc cụm từ tiếng Anh |
| `pronunciation` | string | 255 | No | No | | Phiên âm/IPA |
| `meaning` | string | 500 | Yes | No | | Nghĩa tiếng Việt |
| `definition` | string | 1000 | No | No | | Định nghĩa tiếng Anh |
| `example` | string | 1000 | No | No | | Ví dụ sử dụng |
| `collocations` | string | 500 | No | Yes | | Các cụm từ thường đi kèm |
| `relatedWords` | string | 500 | No | Yes | | Từ đồng nghĩa/trái nghĩa/liên quan |
| `note` | string | 1000 | No | No | | Ghi chú cá nhân |
| `imageUrl` | string | 1000 | No | No | | Ảnh minh họa nếu có |
| `createdAt` | datetime | ISO 8601 | No | No | | Thời điểm tạo |
| `updatedAt` | datetime | ISO 8601 | No | No | | Thời điểm cập nhật gần nhất |
| `deletedAt` | datetime | ISO 8601 | No | No | | Dùng cho xóa mềm; rỗng nghĩa là còn hiệu lực |

#### Indexes

| Index Key | Type | Attributes | Order | Mục đích |
|---|---|---|---|---|
| `idx_vocab_setId` | key | `setId` | ASC | Lấy toàn bộ từ trong một bộ |
| `idx_vocab_userId` | key | `userId` | ASC | Lấy/tìm từ của một người dùng |
| `idx_vocab_set_word` | key | `setId`, `word` | ASC | Tìm/kiểm tra trùng từ trong một bộ |
| `idx_vocab_word` | key | `word` | ASC | Tìm kiếm theo từ khóa |
| `idx_vocab_updatedAt` | key | `updatedAt` | DESC | Đồng bộ dữ liệu mới cập nhật |

---

## 6. Module 3 - Học tập & Theo dõi tiến độ

### 6.1. Collection: `user_word_progress` (Tiến độ học từng từ)

Lưu trạng thái học và tham số thuật toán lặp lại ngắt quãng cho từng người dùng - từng từ vựng.

#### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người học; tham chiếu Appwrite Auth user `$id` |
| `setId` | string | 36 | Yes | No | | Bộ từ chứa từ đang học; tham chiếu `vocabulary_sets.$id` |
| `wordId` | string | 36 | Yes | No | | Từ đang học; tham chiếu `vocabularies.$id` |
| `status` | string | 50 | No | No | `NOT_STARTED` | `NOT_STARTED`, `LEARNING`, `REVIEWING`, `MASTERED` |
| `boxLevel` | integer | | No | No | 0 | Mức flashcard/Leitner hiện tại nếu dùng hộp ôn tập |
| `easinessFactor` | double | | No | No | 2.5 | Hệ số dễ của SM2 |
| `repetitions` | integer | | No | No | 0 | Số lần ôn đúng liên tiếp theo SM2 |
| `intervalDays` | integer | | No | No | 1 | Số ngày tới lần ôn tiếp theo |
| `nextReviewAt` | datetime | ISO 8601 | Yes | No | | Thời điểm từ đến hạn ôn |
| `lastReviewedAt` | datetime | ISO 8601 | No | No | | Lần ôn gần nhất |
| `lastQuality` | integer | 0-5 | No | No | | Điểm chất lượng câu trả lời gần nhất theo SM2 |
| `createdAt` | datetime | ISO 8601 | No | No | | Thời điểm tạo tiến độ |
| `updatedAt` | datetime | ISO 8601 | No | No | | Thời điểm cập nhật gần nhất |

#### Indexes

| Index Key | Type | Attributes | Order | Mục đích |
|---|---|---|---|---|
| `idx_progress_userId` | key | `userId` | ASC | Lấy tiến độ của người dùng |
| `idx_progress_wordId` | key | `wordId` | ASC | Tìm tiến độ theo từ |
| `idx_progress_user_word` | unique | `userId`, `wordId` | ASC | Mỗi người dùng chỉ có một tiến độ cho một từ |
| `idx_progress_user_due` | key | `userId`, `nextReviewAt` | ASC | Lấy danh sách từ đến hạn ôn |
| `idx_progress_user_set` | key | `userId`, `setId` | ASC | Lấy tiến độ theo bộ từ |

### 6.2. Collection: `study_plans` (Kế hoạch học tập)

Lưu mục tiêu học tập của người dùng. Một kế hoạch có thể áp dụng cho toàn bộ tài khoản hoặc một bộ từ cụ thể.

#### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Chủ sở hữu kế hoạch |
| `setId` | string | 36 | No | No | | Bộ từ áp dụng; rỗng nếu kế hoạch tổng quát |
| `planName` | string | 255 | Yes | No | | Tên kế hoạch/mục tiêu |
| `targetWordsPerDay` | integer | | Yes | No | | Số từ mới mục tiêu mỗi ngày |
| `targetReviewPerDay` | integer | | No | No | 0 | Số từ ôn mục tiêu mỗi ngày |
| `startDate` | string | YYYY-MM-DD | Yes | No | | Ngày bắt đầu kế hoạch |
| `endDate` | string | YYYY-MM-DD | Yes | No | | Ngày kết thúc kế hoạch |
| `isActive` | boolean | | No | No | true | Kế hoạch đang được áp dụng |
| `createdAt` | datetime | ISO 8601 | No | No | | Thời điểm tạo |
| `updatedAt` | datetime | ISO 8601 | No | No | | Thời điểm cập nhật gần nhất |

#### Indexes

| Index Key | Type | Attributes | Order | Mục đích |
|---|---|---|---|---|
| `idx_plans_userId` | key | `userId` | ASC | Lấy kế hoạch của người dùng |
| `idx_plans_user_active` | key | `userId`, `isActive` | ASC | Lấy kế hoạch đang bật |
| `idx_plans_user_set` | key | `userId`, `setId` | ASC | Lấy kế hoạch theo bộ từ |

### 6.3. Collection: `daily_learning_stats` (Thống kê học tập hằng ngày)

Collection dùng chung cho Module 3 và Module 4 để tổng hợp dashboard theo ngày. Module 3 cập nhật phần học/ôn từ; Module 4 cập nhật phần quiz.

#### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người dùng |
| `date` | string | YYYY-MM-DD | Yes | No | | Ngày thống kê theo timezone của app |
| `wordsLearned` | integer | | No | No | 0 | Số từ mới học trong ngày |
| `wordsReviewed` | integer | | No | No | 0 | Số từ được ôn trong ngày |
| `wordsMastered` | integer | | No | No | 0 | Số từ chuyển sang trạng thái thành thạo |
| `quizCount` | integer | | No | No | 0 | Số lượt quiz hoàn thành |
| `correctAnswers` | integer | | No | No | 0 | Tổng số câu trả lời đúng trong ngày |
| `totalQuestions` | integer | | No | No | 0 | Tổng số câu quiz trong ngày |
| `avgScore` | double | | No | No | 0 | Điểm/tỷ lệ đúng trung bình, tính từ quiz |
| `studyMinutes` | integer | | No | No | 0 | Tổng thời gian học và luyện tập |
| `createdAt` | datetime | ISO 8601 | No | No | | Thời điểm tạo |
| `updatedAt` | datetime | ISO 8601 | No | No | | Thời điểm cập nhật gần nhất |

#### Indexes

| Index Key | Type | Attributes | Order | Mục đích |
|---|---|---|---|---|
| `idx_stats_user_date` | unique | `userId`, `date` | ASC | Mỗi người dùng chỉ có một bản ghi thống kê mỗi ngày |
| `idx_stats_userId` | key | `userId` | ASC | Lấy chuỗi ngày học của người dùng |
| `idx_stats_date` | key | `date` | DESC | Lọc/sắp xếp thống kê theo ngày |

---

## 7. Module 4 - Quiz & Thống kê luyện tập

### 7.1. Collection: `quiz_attempts` (Lượt làm quiz)

Ghi lại mỗi lần người dùng hoàn thành hoặc bắt đầu một bài quiz.

#### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người làm quiz |
| `setId` | string | 36 | No | No | | Bộ từ được luyện; rỗng nếu quiz tổng hợp nhiều bộ |
| `quizType` | string | 50 | Yes | No | `multiple_choice` | `multiple_choice`, `fill_in_blank`, `listening`, `matching` |
| `status` | string | 50 | No | No | `COMPLETED` | `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |
| `totalQuestions` | integer | | No | No | 0 | Tổng số câu hỏi |
| `correctAnswers` | integer | | No | No | 0 | Số câu đúng |
| `scorePercent` | double | | No | No | 0 | Tỷ lệ đúng từ 0 đến 100 |
| `durationSeconds` | integer | | No | No | 0 | Thời gian làm quiz |
| `startedAt` | datetime | ISO 8601 | No | No | | Thời điểm bắt đầu |
| `completedAt` | datetime | ISO 8601 | No | No | | Thời điểm hoàn thành |
| `createdAt` | datetime | ISO 8601 | No | No | | Thời điểm tạo |
| `updatedAt` | datetime | ISO 8601 | No | No | | Thời điểm cập nhật gần nhất |

#### Indexes

| Index Key | Type | Attributes | Order | Mục đích |
|---|---|---|---|---|
| `idx_attempts_userId` | key | `userId` | ASC | Lấy lịch sử quiz của người dùng |
| `idx_attempts_user_completed` | key | `userId`, `completedAt` | DESC | Lấy quiz gần đây cho dashboard |
| `idx_attempts_user_set` | key | `userId`, `setId` | ASC | Lọc quiz theo bộ từ |
| `idx_attempts_status` | key | `status` | ASC | Dọn các lượt quiz bỏ dở nếu cần |

### 7.2. Collection: `quiz_answers` (Chi tiết câu trả lời)

Lưu từng câu hỏi/câu trả lời trong một lượt quiz để xem lại lỗi sai và phân tích thống kê.

#### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `attemptId` | string | 36 | Yes | No | | Tham chiếu `quiz_attempts.$id` |
| `userId` | string | 36 | Yes | No | | Lặp lại để kiểm tra quyền và query nhanh |
| `wordId` | string | 36 | Yes | No | | Tham chiếu `vocabularies.$id` |
| `questionType` | string | 50 | Yes | No | | Loại câu hỏi trong lượt quiz |
| `questionText` | string | 1000 | No | No | | Nội dung câu hỏi đã hiển thị |
| `correctAnswer` | string | 500 | No | No | | Đáp án đúng tại thời điểm làm quiz |
| `userAnswer` | string | 500 | No | No | | Đáp án người dùng chọn/nhập |
| `isCorrect` | boolean | | No | No | false | Kết quả đúng/sai |
| `quality` | integer | 0-5 | No | No | | Điểm chất lượng dùng để cập nhật SM2 nếu áp dụng |
| `answeredAt` | datetime | ISO 8601 | No | No | | Thời điểm trả lời |
| `createdAt` | datetime | ISO 8601 | No | No | | Thời điểm tạo |

#### Indexes

| Index Key | Type | Attributes | Order | Mục đích |
|---|---|---|---|---|
| `idx_answers_attemptId` | key | `attemptId` | ASC | Lấy chi tiết một lượt quiz |
| `idx_answers_user_word` | key | `userId`, `wordId` | ASC | Xem lịch sử trả lời của một từ |
| `idx_answers_isCorrect` | key | `isCorrect` | ASC | Lọc câu sai nếu cần luyện lại |

---

## 8. Luồng cập nhật dữ liệu giữa các module

1. Thành viên 1 tạo hồ sơ `user_profiles` sau khi Appwrite Auth tạo user thành công; nếu người dùng bật nhắc học thì tạo/cập nhật `notification_settings`.
2. Thành viên 2 tạo `vocabulary_sets`, sau đó tạo `vocabularies` với `setId` trỏ về bộ từ.
3. Thành viên 3 tạo hoặc cập nhật `user_word_progress` theo cặp `userId + wordId` khi người dùng bắt đầu học/ôn.
4. Khi người dùng học hoặc ôn flashcard, Thành viên 3 cập nhật `lastQuality`, `easinessFactor`, `intervalDays`, `repetitions`, `nextReviewAt` và cộng số liệu vào `daily_learning_stats`.
5. Thành viên 4 tạo `quiz_attempts` và các `quiz_answers` khi người dùng làm quiz.
6. Với mỗi câu trả lời quiz có `wordId`, Thành viên 4 cập nhật lại `user_word_progress` theo `quality` hoặc đúng/sai.
7. Khi quiz hoàn thành, Thành viên 4 cộng `quizCount`, `correctAnswers`, `totalQuestions`, `avgScore`, `studyMinutes` vào cùng bản ghi `daily_learning_stats` của ngày đó.
8. UI dashboard đọc `daily_learning_stats`; UI ôn tập đọc `user_word_progress` kết hợp `vocabularies`.

---

## 9. Thứ tự tạo collection trên Appwrite

1. `user_profiles`
2. `notification_settings`
3. `vocabulary_sets`
4. `vocabularies`
5. `user_word_progress`
6. `study_plans`
7. `daily_learning_stats`
8. `quiz_attempts`
9. `quiz_answers`

Nếu Appwrite project không hỗ trợ một số ràng buộc unique/composite như tài liệu, nhóm cần kiểm tra trùng bằng query trước khi tạo document.
