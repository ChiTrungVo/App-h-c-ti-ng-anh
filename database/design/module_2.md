# Database Schema - Module 2: Quản lý bộ từ vựng & Từ vựng

Tài liệu này chi tiết hóa schema cho các Collection phục vụ Module 2.

## 1. Collection: `vocabulary_sets` (Bộ từ vựng)

Lưu trữ thông tin về các thư mục/bộ từ vựng của người dùng.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `userId` | string | 36 | Yes | No | | Chủ sở hữu bộ từ |
| `title` | string | 255 | Yes | No | | Tên bộ từ (VD: IELTS Topic 1) |
| `description` | string | 500 | No | No | | Mô tả ngắn gọn |
| `tags` | string | 50 | No | Yes | | Danh sách tag (IELTS, Business, ...) |
| `isPublic` | boolean | | No | No | false | Cho phép người khác xem (mở rộng) |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Permissions

*   **Owner**: `read`, `update`, `delete`
*   **Người khác**: Nếu `isPublic` = true thì `read` (cần xử lý qua Function hoặc logic App). Mặc định là Document Level Security cho Owner.

---

## 2. Collection: `vocabularies` (Từ vựng)

Lưu trữ chi tiết các từ vựng trong từng bộ từ.

### Attributes

| Attribute ID | Type | Size/Format | Required | Array | Default | Description |
|---|---|---|---|---|---|---|
| `setId` | string | 36 | Yes | No | | Liên kết với `$id` của `vocabulary_sets` |
| `userId` | string | 36 | Yes | No | | Chủ sở hữu từ (để query nhanh) |
| `word` | string | 255 | Yes | No | | Từ tiếng Anh |
| `pronunciation` | string | 255 | No | No | | Phiên âm |
| `meaning` | string | 500 | Yes | No | | Nghĩa tiếng Việt |
| `definition` | string | 1000 | No | No | | Định nghĩa tiếng Anh |
| `example` | string | 1000 | No | No | | Ví dụ sử dụng |
| `collocation` | string | 500 | No | Yes | | Các collocation đi kèm |
| `relatedWords` | string | 500 | No | Yes | | Từ đồng nghĩa/trái nghĩa |
| `note` | string | 1000 | No | No | | Ghi chú cá nhân |
| `imageUrl` | string | 1000 | No | No | | Ảnh minh họa (nếu có) |
| `createdAt` | datetime | | No | No | | Ngày tạo |

### Indexes

| Index Key | Type | Attributes | Order |
|---|---|---|---|
| `idx_setId` | key | `setId` | ASC |
| `idx_userId` | key | `userId` | ASC |
| `idx_word` | key | `word` | ASC |