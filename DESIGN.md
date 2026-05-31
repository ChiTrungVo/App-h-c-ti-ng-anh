# MinLish Aqua Premium Design System

## Product

MinLish is an Android vocabulary learning app for Vietnamese learners. The UI should feel like a polished student-project-ready learning app: modern, friendly, clean, cute but not childish.

The product preserves these future Appwrite collection and field names: `userId`, `setId`, `wordId`, `vocabulary_sets`, `vocabularies`, `user_word_progress`, `daily_learning_stats`, `quiz_attempts`, `quiz_answers`, `notification_settings`.

## Visual Direction

- Style: modern cartoon, clean Material 3, ocean branded.
- Inspiration: friendly motivational language-learning energy, clear progress, strong CTAs, and gamification. Do not copy Duolingo logo, mascot, layout, brand color, or illustration style.
- Mascot: a cute blue whale used selectively on splash, home, empty state, result, and progress screens.
- Background: calm light cyan.
- Cards: white rounded cards with soft elevation.
- Accent visuals: restrained waves and bubbles.
- Avoid messy gradients, clutter, emojis, text-symbol icons, oversized mascots, and childish sticker-style UI.
- Compose conversion target: local vector drawables rendered with `painterResource()`.

## Color Tokens

- `background`: `#F6FCFF`
- `surface`: `#FFFFFF`
- `surfaceContainer`: `#EAF8FE`
- `primary`: `#0288D1`
- `primaryLight`: `#4FC3F7`
- `primaryContainer`: `#BDEFFF`
- `secondary`: `#00A6B2`
- `secondaryContainer`: `#D6FAF6`
- `tertiary`: `#F7B731`
- `tertiaryContainer`: `#FFF2BF`
- `success`: `#6DD6A8`
- `successContainer`: `#DDF8EC`
- `error`: `#D64545`
- `errorContainer`: `#FFE4E4`
- `textPrimary`: `#123047`
- `textSecondary`: `#4E6372`
- `outline`: `#B7D6E3`
- `wave`: `#7ED7F4`

## Typography

Use a rounded readable sans-serif feel. In Compose, use `FontFamily.Default` with Quicksand-like weights because no bundled font file exists yet.

- Display: 36sp, Bold, 42sp line height.
- Headline: 28sp, Bold, 34sp line height.
- Title: 20sp, Bold, 26sp line height.
- Body: 16sp, Medium/Normal, 24sp line height.
- Body small: 14sp, Normal, 21sp line height.
- Label: 13sp, SemiBold, 18sp line height.

## Spacing And Shape

- Screen horizontal margin: 20dp.
- Section gap: 24dp.
- Card padding: 16dp.
- List item gap: 12dp.
- Minimum tappable height: 48dp.
- Primary button height: 52dp.
- Card radius: 24dp.
- Input radius: 18dp.
- Button radius: 18dp.
- Bottom bar radius: 28dp top corners.

## Components

- Primary buttons are filled aqua blue with white labels.
- Secondary buttons are white or light cyan outlined buttons.
- Stat cards use small local vector icons, value, and Vietnamese label.
- Vocabulary cards show title, description, tags, word count, progress, and state.
- Word cards show English word, IPA, Vietnamese meaning, short example.
- Flashcards use a large centered card with front/back states.
- Quiz options are rounded cards with selected, correct, and incorrect states.
- Empty states use small whale/notebook illustrations and one clear action.
- Bottom navigation contains exactly four tabs: `Trang chủ`, `Từ vựng`, `Học`, `Tiến độ`.
- Profile is accessed from the avatar/top bar or from the Progress screen. Quiz belongs inside the `Học` tab.
- Bottom navigation uses a glass solid capsule style with a white/pale cyan surface, subtle border, soft shadow, and cyan selected pill.

## Local Asset Names

- `ic_home`
- `ic_vocabulary`
- `ic_learning`
- `ic_practice`
- `ic_progress`
- `ic_profile`
- `ic_search`
- `ic_add`
- `ic_book`
- `ic_flashcard`
- `ic_quiz`
- `ic_clock`
- `ic_check_circle`
- `ic_close_circle`
- `ic_chart`
- `ic_bell`
- `ic_edit`
- `ic_logout`
- `ic_whale`
- `ic_whale_celebrate`
- `ic_empty_notebook`
- `ic_wave`

## Screen Inventory

1. Splash screen with MinLish logo, whale mascot, text `Học từ vựng mỗi ngày`, loading state, aqua background, subtle wave and bubble decoration.
2. Login screen with email, password, invalid email/password state, `Đăng nhập`, `Tạo tài khoản`, `Quên mật khẩu?`, small whale illustration near top.
3. Register screen with display name, email, password, confirm password, password mismatch state, `Đăng ký`, link back to login.
4. Home screen with greeting, daily goal card, `Bắt đầu học`, stats for learned words, streak, quiz accuracy, review due.
5. Forgot password screen with email, `Gửi yêu cầu`, `Quay lại đăng nhập`, loading, error, and success state.
6. Vocabulary set list shell with search, filters `Tất cả`, `IELTS`, `Giao tiếp`, `Công việc`, `Đã học gần đây`, cards, empty/loading/error placeholders, add action.
6. Vocabulary set detail with title, word count, progress preview, word list, `Thêm từ`, `Sửa bộ từ`, `Bắt đầu học`, `Làm quiz`.
7. Create/edit vocabulary set with title, description, topic tags, public/private toggle, `Lưu`.
8. Create/edit word with word, IPA, meaning, definition, example, collocations, note, image URL, `Lưu từ`.
9. Daily learning plan with today plan, new word target, due review count, study time estimate, `Học bằng flashcard`.
10. Flashcard session with large card, front and back content, quality buttons `Lại`, `Khó`, `Ổn`, `Dễ`, progress.
11. Session result with learned, reviewed, remembered, minutes, `Tiếp tục`, `Ôn lại`, whale celebration.
12. Practice type with cards `Trắc nghiệm`, `Điền từ`, `Nghe và chọn`, `Ghép cặp`.
13. Quiz with question, options, progress, `Kiểm tra`, `Câu tiếp theo`, correct/incorrect feedback.
14. Quiz result with score percentage, correct answers, total questions, duration, `Xem lại câu sai`.
15. Progress dashboard with daily stats, streak, words mastered, quiz accuracy, weekly chart placeholder, ocean progress visualization.
16. Profile with avatar, display name, email, target language, proficiency, daily target minutes, `Sửa hồ sơ`, `Đăng xuất`.
17. Edit profile with display name, phone, bio, native language, target language, proficiency, daily target, `Lưu thay đổi`, save success state.
18. Notification settings with reminder enabled, reminder time, reminder days, timezone, `Lưu cài đặt`, enabled/disabled state.

## Team Ownership

- Member 1: Splash, Login, Register, Forgot Password, Home shell, Profile, Edit Profile, Notification Settings, logout dialog, navigation.
- Member 2: Vocabulary set list/detail, create/edit set, create/edit word, search/filter.
- Member 3: Daily learning plan, Flashcard session, Session result.
- Member 4: Practice type, Quiz screen, Quiz result, Progress dashboard.
