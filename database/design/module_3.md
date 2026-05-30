# Database Schema - Module 3: Học tập & Theo dõi tiến độ

Tài liệu này chi tiết hóa schema cho các Collection phục vụ Module 3.

---

## 1. Collection: `words` (Từ vựng)

Bảng lưu trữ thông tin về từ vựng.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `id` | integer | | Yes | No | | ID từ vựng |
| `vocab` | string | 255 | Yes | No | | Từ vựng tiếng Anh |
| `ipa` | string | 255 | No | No | | Pronunciation IPA |
| `meaning` | string | 500 | Yes | No | | Nghĩa tiếng Anh sang tiếng Việt |
| `example` | string | 1000 | No | No | | Ví dụ sử dụng |
| `imageUrl` | string | 1000 | No | No | | Link dẫn tới ảnh minh họa |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_vocab` | key | `vocab` | ASC |

---

## 2. Collection: `user_word_progress` (Tiến độ học từ)

Bảng lưu trữ tiến độ học tập và thuật toán lặp lại ngắt quãng (Spaced Repetition - SM2).

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người dùng |
| `wordId` | string | 36 | Yes | No | | Liên kết với `words.$id` |
| `boxLevel` | integer | | No | No | 0 | Flashcard hiện tại / SM2: Số lần lặp lại thành công |
| `easinessFactor` | double | | No | No | 2.5 | E-factor (SM2 algorithm) |
| `repetitions` | integer | | No | No | 0 | Số lần ôn tập thành công |
| `intervalDays` | integer | | No | No | 1 | Số ngày chờ trước lần ôn tập tiếp theo |
| `nextReviewDate` | datetime | | Yes | No | | Timestamp cho lần ôn tập tiếp theo |
| `lastReviewedAt` | datetime | | No | No | | Lần ôn tập cuối cùng |
| `status` | string | 50 | No | No | NOT_STARTED | Trạng thái: NOT_STARTED, IN_PROGRESS, MASTERED |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId` | key | `userId` | ASC |
| `idx_wordId` | key | `wordId` | ASC |
| `idx_nextReviewDate` | key | `nextReviewDate` | ASC |

---

## 3. Collection: `study_plans` (Kế hoạch học tập)

Bảng thiết lập mục tiêu/kế hoạch học tập.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Chủ sở hữu kế hoạch |
| `planName` | string | 255 | Yes | No | | Tên kế hoạch (Mục tiêu/Target) |
| `targetWordsPerDay` | integer | | Yes | No | | Số từ vựng mục tiêu học mỗi ngày |
| `startDate` | datetime | | Yes | No | | Ngày bắt đầu kế hoạch |
| `endDate` | datetime | | Yes | No | | Ngày kết thúc kế hoạch |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId` | key | `userId` | ASC |

---

## 4. Collection: `daily_logs` (Nhật ký hoạt động hằng ngày)

Bảng ghi log hoạt động học tập hằng ngày.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Người dùng |
| `date` | string | 10 | Yes | No | | Ngày (định dạng YYYY-MM-DD) |
| `wordsLearned` | integer | | No | No | 0 | Số từ mới học trong ngày |
| `wordsReviewed` | integer | | No | No | 0 | Số từ cũ được ôn tập trong ngày |
| `studyMinutes` | integer | | No | No | 0 | Thời gian học (phút) |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId_date` | key | `userId`, `date` | ASC |