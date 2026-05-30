# Database Schema - Module 4: Quiz & Thống kê

Tài liệu này chi tiết hóa schema cho các Collection phục vụ Module 4.

---

## 1. Collection: `user_word_progress` (Tiến độ từ vựng)

Bảng lưu trữ tiến độ học tập chi tiết từng từ vựng.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người dùng |
| `wordId` | string | 36 | Yes | No | | Liên kết với từ vựng |
| `easinessFactor` | integer | | No | No | 250 | E-factor (SM2 algorithm) |
| `intervalDays` | integer | | No | No | 1 | Số ngày chờ trước lần ôn tập tiếp theo |
| `repetitions` | integer | | No | No | 0 | Số lần ôn tập thành công |
| `nextReviewAt` | datetime | | Yes | No | | Timestamp cho lần ôn tập tiếp theo |
| `lastReviewedAt` | datetime | | No | No | | Lần ôn tập cuối cùng |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId` | key | `userId` | ASC |
| `idx_wordId` | key | `wordId` | ASC |
| `idx_nextReviewAt` | key | `nextReviewAt` | ASC |

---

## 2. Collection: `user_deck_progress` (Tiến độ bộ từ vựng)

Bảng lưu trữ tiến độ học tập tổng quát cho từng bộ từ vựng.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người dùng |
| `deckId` | string | 36 | Yes | No | | Liên kết với bộ từ vựng |
| `totalWords` | integer | | No | No | 0 | Tổng số từ vựng trong bộ |
| `masteredWords` | integer | | No | No | 0 | Số từ đã thành thạo |
| `startedAt` | datetime | | No | No | | Ngày bắt đầu học bộ từ |
Bảng ghi lại mỗi lần người dùng thực hiện quiz.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người dùng |
| `deckId` | string | 36 | No | No | | Liên kết với bộ từ vựng (tùy chọn) |
| `quizType` | string | 50 | No | No | multiple_choice | Loại quiz: multiple_choice, fill_in_blank, listening, matching |
| `totalQuestions` | integer | | No | No | 0 | Tổng số câu hỏi |
| `correctAnswers` | integer | | No | No | 0 | Số câu trả lời đúng |
| `durationSeconds` | integer | | No | No | 0 | Thời gian làm quiz (giây) |
| `completedAt` | datetime | | No | No | | Thời gian hoàn thành |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId` | key | `userId` | ASC |
| `idx_completedAt` | key | `completedAt` | ASC |

---

## 4. Collection: `quiz_questions` (Câu hỏi quiz)
**quiz\_attempts**

Bảng lưu trữ chi tiết từng câu hỏi trong một lần quiz.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `attemptId` | string | 36 | Yes | No | | Liên kết với lần thử quiz |
| `wordId` | string | 36 | Yes | No | | Liên kết với từ vựng |
| `questionText` | string | 1000 | No | No | | Nội dung câu hỏi |
| `correctAnswer` | string | 500 | No | No | | Câu trả lời đúng |
| `userAnswer` | string | 500 | No | No | | Câu trả lời của người dùng |
| `isCorrect` | boolean | | No | No | false | Người dùng trả lời đúng hay không |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_attemptId` | key | `attemptId` | ASC |
| `idx_wordId` | key | `wordId` | ASC |

---

Bảng ghi lại thống kê hoạt động học tập hằng ngày.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người dùng |
| `date` | string | 10 | Yes | No | | Ngày (định dạng YYYY-MM-DD) |
| `wordsReviewed` | integer | | No | No | 0 | Số từ được ôn tập |
| `wordsMastered` | integer | | No | No | 0 | Số từ đã thành thạo |
| `quizCount` | integer | | No | No | 0 | Số lần làm quiz |
| `avgScore` | float | | No | No | 0 | Điểm trung bình quiz |
| `studyMinutes` | integer | | No | No | 0 | Thời gian học (phút) |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId_date` | key | `userId`, `date` | ASC |

---

## 6. Collection: `notification_settings` (Cài đặt thông báo) |
| user\_answer | String | 500 |  |  |
| is\_correct | Boolean |  |  | false |
Bảng cài đặt thông báo và nhắc nhở cho người dùng.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người dùng |
| `reminderTime` | string | 5 | No | No | 08:00 | Giờ nhắc nhở (HH:MM) |
| `reminderDays` | string | 100 | No | Yes | | Các ngày nhắc nhở (e.g., "MON,WED,FRI") |
| `isEnabled` | boolean | | No | No | true | Bật/tắt thông báo |
| `fcmToken` | string | 500 | No | No | | Firebase Cloud Messaging token |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId` | key | `userId` | ASC | 0 |
| quiz\_count | Integer |  |  | 0 |
| avg\_score | Float |  |  | 0 |
| study\_minutes | Integer |  |  | 0 |

**notification\_settings**

| Attribute | Type | Size | Required | Default |
| :---- | :---- | :---- | :---- | :---- |
| user\_id | String | 36 | yes |  |
| reminder\_time | String | 5 |  | 08:00 |
| reminder\_days | String | 100 |  |  |
| is\_enabled | Boolean |  |  | true |
| fcm\_token | String | 500 |  |  |

