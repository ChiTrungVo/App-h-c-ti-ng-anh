# Database Schema - Module 1: Xác thực & Quản lý người dùng

Tài liệu này chi tiết hóa schema cho các Collection phục vụ Module 1.

---

## 1. Collection: `users` (Người dùng)

Lưu trữ thông tin cơ bản về người dùng hệ thống.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `email` | string | 255 | Yes | No | | Email duy nhất của người dùng |
| `password` | string | 255 | Yes | No | | Mật khẩu (hash) |
| `name` | string | 255 | Yes | No | | Tên đầy đủ |
| `avatar` | string | 1000 | No | No | | URL ảnh đại diện |
| `phone` | string | 20 | No | No | | Số điện thoại |
| `bio` | string | 500 | No | No | | Tiểu sử cá nhân |
| `nativeLanguage` | string | 50 | No | No | vi | Ngôn ngữ mẹ đẻ |
| `targetLanguage` | string | 50 | No | No | en | Ngôn ngữ học tập |
| `proficiencyLevel` | string | 50 | No | No | beginner | Trình độ: beginner, intermediate, advanced, expert |
| `isEmailVerified` | boolean | | No | No | false | Email được xác thực |
| `isPhoneVerified` | boolean | | No | No | false | Số điện thoại được xác thực |
| `status` | string | 50 | No | No | active | Trạng thái: active, inactive, suspended, deleted |
| `lastLoginAt` | datetime | | No | No | | Lần đăng nhập cuối cùng |
| `createdAt` | datetime | | No | No | | Ngày tạo |
| `updatedAt` | datetime | | No | No | | Ngày cập nhật |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_email` | unique | `email` | ASC |
| `idx_status` | key | `status` | ASC |
| `idx_createdAt` | key | `createdAt` | DESC |

### Permissions

*   **Owner**: `read`, `update`
*   **Admin**: `read`, `update`, `delete`

---

## 2. Collection: `user_sessions` (Phiên đăng nhập)

Lưu trữ thông tin về phiên đăng nhập của người dùng.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Liên kết với người dùng |
| `token` | string | 500 | Yes | No | | JWT token hoặc session token |
| `refreshToken` | string | 500 | No | No | | Refresh token (nếu có) |
| `deviceInfo` | string | 500 | No | No | | Thông tin thiết bị (OS, app version, ...) |
| `ipAddress` | string | 50 | No | No | | Địa chỉ IP |
| `userAgent` | string | 500 | No | No | | User agent string |
| `expiresAt` | datetime | | Yes | No | | Thời gian hết hạn |
| `isActive` | boolean | | No | No | true | Phiên còn hoạt động |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId` | key | `userId` | ASC |
| `idx_token` | unique | `token` | ASC |
| `idx_expiresAt` | key | `expiresAt` | ASC |

---

## 3. Collection: `user_profiles` (Hồ sơ người dùng)

Lưu trữ thông tin chi tiết và cài đặt cá nhân của người dùng.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Liên kết với người dùng |
| `birthDate` | string | 10 | No | No | | Ngày sinh (YYYY-MM-DD) |
| `gender` | string | 10 | No | No | | Giới tính: male, female, other |
| `country` | string | 50 | No | No | | Quốc gia |
| `city` | string | 100 | No | No | | Thành phố |
| `studyGoal` | string | 500 | No | No | | Mục tiêu học tập |
| `studyHoursPerDay` | integer | | No | No | 0 | Thời gian học dự kiến (phút/ngày) |
| `preferredLearningStyle` | string | 100 | No | No | | Phong cách học ưa thích |
| `dailyReminderEnabled` | boolean | | No | No | true | Bật nhắc nhở hằng ngày |
| `soundEnabled` | boolean | | No | No | true | Bật âm thanh |
| `darkModeEnabled` | boolean | | No | No | false | Bật chế độ tối |
| `createdAt` | datetime | | No | No | | Ngày tạo |
| `updatedAt` | datetime | | No | No | | Ngày cập nhật |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId` | unique | `userId` | ASC |
| `idx_country` | key | `country` | ASC |

---

## 4. Collection: `password_resets` (Đặt lại mật khẩu)

Lưu trữ token và yêu cầu đặt lại mật khẩu.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Liên kết với người dùng |
| `resetToken` | string | 500 | Yes | No | | Token duy nhất để xác nhận |
| `email` | string | 255 | Yes | No | | Email người dùng (để xác minh) |
| `isUsed` | boolean | | No | No | false | Token đã được sử dụng |
| `expiresAt` | datetime | | Yes | No | | Thời gian hết hạn token |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_resetToken` | unique | `resetToken` | ASC |
| `idx_userId` | key | `userId` | ASC |
| `idx_expiresAt` | key | `expiresAt` | ASC |

---

## 5. Collection: `email_verifications` (Xác thực email)

Lưu trữ token và yêu cầu xác thực email.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Liên kết với người dùng |
| `verificationToken` | string | 500 | Yes | No | | Token duy nhất để xác nhận |
| `email` | string | 255 | Yes | No | | Email cần xác thực |
| `isVerified` | boolean | | No | No | false | Email đã được xác thực |
| `expiresAt` | datetime | | Yes | No | | Thời gian hết hạn token |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_verificationToken` | unique | `verificationToken` | ASC |
| `idx_userId` | key | `userId` | ASC |
| `idx_email` | key | `email` | ASC |

---

## 6. Collection: `social_logins` (Đăng nhập xã hội)

Lưu trữ thông tin liên kết với tài khoản xã hội (OAuth).

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Liên kết với người dùng |
| `provider` | string | 50 | Yes | No | | Nhà cung cấp: google, facebook, apple |
| `providerUserId` | string | 255 | Yes | No | | ID từ nhà cung cấp |
| `email` | string | 255 | No | No | | Email từ nhà cung cấp |
| `name` | string | 255 | No | No | | Tên từ nhà cung cấp |
| `avatar` | string | 1000 | No | No | | Avatar từ nhà cung cấp |
| `accessToken` | string | 500 | No | No | | Access token từ nhà cung cấp |
| `refreshToken` | string | 500 | No | No | | Refresh token từ nhà cung cấp |
| `tokenExpiresAt` | datetime | | No | No | | Thời gian hết hạn access token |
| `createdAt` | datetime | | No | No | | Ngày tạo |
| `updatedAt` | datetime | | No | No | | Ngày cập nhật |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_userId_provider` | unique | `userId`, `provider` | ASC |
| `idx_providerUserId` | unique | `provider`, `providerUserId` | ASC |
