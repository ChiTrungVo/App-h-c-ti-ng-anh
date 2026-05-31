# Hướng dẫn thiết lập Database trên Appwrite

## Yêu cầu

- Node.js v16 trở lên
- Tài khoản Appwrite (Cloud hoặc Self-hosted)
- API Key với quyền tạo database và collections

## Các bước thực hiện

### 1. Lấy thông tin từ Appwrite Console

Truy cập Appwrite Console và lấy các thông tin sau:

- **Endpoint**: 
  - Cloud: `https://cloud.appwrite.io/v1`
  - Self-hosted: URL của instance (ví dụ: `http://localhost/v1`)
  
- **Project ID**: Vào project của bạn, copy Project ID từ Settings

- **API Key**: 
  1. Vào project → Settings → API Keys
  2. Tạo API Key mới với các quyền:
     - `databases.read`
     - `databases.write`
     - `collections.read`
     - `collections.write`
     - `attributes.read`
     - `attributes.write`
     - `indexes.read`
     - `indexes.write`

### 2. Cấu hình môi trường

```bash
cd database/scripts
cp .env.example .env
```

Mở file `.env` và điền thông tin:

```env
APPWRITE_ENDPOINT=https://cloud.appwrite.io/v1
APPWRITE_PROJECT_ID=your_project_id_here
APPWRITE_API_KEY=your_api_key_here
DATABASE_ID=vocabulary_app_db
```

### 3. Cài đặt dependencies

```bash
npm install
```

### 4. Chạy script thiết lập

```bash
npm run setup
```

Script sẽ tạo:
- 1 database: `vocabulary_app_db`
- 9 collections với đầy đủ attributes và indexes theo schema

## Các collection được tạo

1. **user_profiles** - Hồ sơ người dùng
2. **notification_settings** - Cài đặt nhắc học
3. **vocabulary_sets** - Bộ từ vựng
4. **vocabularies** - Từ vựng
5. **user_word_progress** - Tiến độ học từng từ
6. **study_plans** - Kế hoạch học tập
7. **daily_learning_stats** - Thống kê học tập hằng ngày
8. **quiz_attempts** - Lượt làm quiz
9. **quiz_answers** - Chi tiết câu trả lời

## Xử lý lỗi

### Lỗi 401 Unauthorized
- Kiểm tra lại API Key có đúng không
- Kiểm tra API Key có đủ quyền không

### Lỗi 409 Conflict
- Database hoặc collection đã tồn tại
- Script sẽ bỏ qua và tiếp tục

### Lỗi Rate Limit
- Script đã có delay giữa các request
- Nếu vẫn gặp lỗi, tăng thời gian delay trong code

## Kiểm tra kết quả

Sau khi chạy xong, truy cập Appwrite Console:
1. Vào project của bạn
2. Chọn Databases
3. Chọn database `vocabulary_app_db`
4. Kiểm tra các collections, attributes và indexes đã được tạo

## Lưu ý bảo mật

- **KHÔNG** commit file `.env` lên git
- File `.env` đã được thêm vào `.gitignore`
- API Key chỉ dùng cho việc setup, nên xóa hoặc vô hiệu hóa sau khi hoàn thành
