package com.example.mobile_project.ui.screens.profile

import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.feature.auth.data.MinLishAuthUser
import com.example.mobile_project.feature.profile.viewmodel.ProfileUiState
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.theme.MinLishError
import com.example.mobile_project.ui.theme.MinLishErrorContainer
import com.example.mobile_project.ui.theme.MinLishPrimary
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSecondary
import com.example.mobile_project.ui.theme.MinLishSurface
import com.example.mobile_project.ui.theme.MinLishSurfaceContainer
import com.example.mobile_project.ui.theme.MinLishTertiary
import com.example.mobile_project.ui.theme.MinLishTertiaryContainer
import com.example.mobile_project.ui.theme.MinLishTextPrimary
import com.example.mobile_project.ui.theme.MinLishTextSecondary

@Composable
fun ProfileScreen(
    authUser: MinLishAuthUser?,
    profileState: ProfileUiState,
    onEditProfile: () -> Unit,
    onNotifications: () -> Unit,
    onAccountSecurity: () -> Unit,
    onLogout: () -> Unit
) {
    val profile = profileState.profile
    val displayName = profile?.displayName ?: authUser?.displayName ?: "Người học MinLish"
    val email = profile?.email ?: authUser?.email ?: "Chưa đăng nhập"
    val joinDate = profile?.createdAt?.takeIf { it.isNotBlank() }?.let { dateString ->
        runCatching {
            val datePart = dateString.substringBefore("T")
            val parts = datePart.split("-")
            if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else datePart
        }.getOrNull()
    }

    OceanBubblyBackground(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Cá nhân",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineLarge,
                color = MinLishTextPrimary
            )
            Spacer(Modifier.height(14.dp))

            ProfileHeaderCard(
                avatarBytes = profileState.avatarBytes,
                displayName = displayName,
                email = email,
                joinDate = joinDate,
                onEditProfile = onEditProfile
            )

            profileState.errorMessage?.let { message ->
                Spacer(Modifier.height(12.dp))
                ErrorMessage(message)
            }

            Spacer(Modifier.height(18.dp))
            ProfileSectionCard(title = "Hồ sơ học tập") {
                ProfileInfoRow(
                    iconRes = R.drawable.ic_profile,
                    label = "Ngôn ngữ mẹ đẻ",
                    value = profile?.nativeLanguage ?: "vi",
                    color = MinLishPrimary
                )
                ProfileInfoRow(
                    iconRes = R.drawable.ic_learning,
                    label = "Ngôn ngữ học",
                    value = profile?.targetLanguage ?: "en",
                    color = MinLishSecondary
                )
                ProfileInfoRow(
                    iconRes = R.drawable.ic_progress,
                    label = "Trình độ",
                    value = profile?.proficiencyLevel ?: "beginner",
                    color = MinLishTertiary
                )
                ProfileInfoRow(
                    iconRes = R.drawable.ic_clock,
                    label = "Mục tiêu hằng ngày",
                    value = "${profile?.dailyTargetMinutes ?: 15} phút",
                    color = MinLishPrimary
                )
                ProfileInfoRow(
                    iconRes = R.drawable.ic_check_circle,
                    label = "Cam kết",
                    value = profile?.studyGoal ?: "Duy trì học từ vựng mỗi ngày",
                    color = MinLishSecondary
                )
                profile?.preferredLearningStyle?.takeIf { it.isNotBlank() }?.let {
                    ProfileInfoRow(R.drawable.ic_book, "Sở thích học", it, MinLishPrimary)
                }
                profile?.bio?.takeIf { it.isNotBlank() }?.let {
                    ProfileInfoRow(R.drawable.ic_edit, "Giới thiệu", it, MinLishTertiary)
                }
            }

            Spacer(Modifier.height(16.dp))
            ProfileSectionCard(title = "Tùy chọn") {
                ProfileToggleRow(
                    iconRes = R.drawable.ic_bell,
                    label = "Âm thanh",
                    checked = profile?.soundEnabled ?: true
                )
                ProfileToggleRow(
                    iconRes = R.drawable.ic_home,
                    label = "Giao diện tối",
                    checked = profile?.darkModeEnabled ?: false
                )
                ProfileActionRow(
                    iconRes = R.drawable.ic_bell,
                    label = "Cài đặt nhắc học",
                    value = "Xem lịch và thời gian nhắc học",
                    color = MinLishSecondary,
                    onClick = onNotifications
                )
            }

            Spacer(Modifier.height(16.dp))
            ProfileSectionCard(title = "Tài khoản") {
                ProfileActionRow(
                    iconRes = R.drawable.ic_lock,
                    label = "Tài khoản & bảo mật",
                    value = "Đổi email, mật khẩu hoặc xóa tài khoản",
                    color = MinLishPrimary,
                    onClick = onAccountSecurity
                )
                ProfileActionRow(
                    iconRes = R.drawable.ic_logout,
                    label = "Đăng xuất",
                    value = "Rời khỏi MinLish trên thiết bị này",
                    color = MinLishError,
                    onClick = onLogout
                )
            }

            Spacer(Modifier.height(124.dp))
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    avatarBytes: ByteArray?,
    displayName: String,
    email: String,
    joinDate: String?,
    onEditProfile: () -> Unit
) {
    ProfileCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MinLishSurface,
        shadowColor = MinLishPrimary.copy(alpha = 0.14f)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AvatarView(avatarBytes = avatarBytes, displayName = displayName)
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .border(3.dp, MinLishSurface, CircleShape)
                        .clickable(onClick = onEditProfile),
                    shape = CircleShape,
                    color = MinLishTertiaryContainer,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Chỉnh sửa hồ sơ",
                            modifier = Modifier.size(18.dp),
                            colorFilter = ColorFilter.tint(MinLishTertiary)
                        )
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MinLishTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinLishTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                joinDate?.let {
                    Spacer(Modifier.height(10.dp))
                    Surface(
                        shape = CircleShape,
                        color = MinLishPrimaryContainer.copy(alpha = 0.24f)
                    ) {
                        Text(
                            text = "Tham gia: $it",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MinLishPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarView(avatarBytes: ByteArray?, displayName: String) {
    val bitmap = remember(avatarBytes) {
        avatarBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }

    Surface(
        modifier = Modifier
            .size(96.dp)
            .border(4.dp, MinLishSurface, CircleShape),
        shape = CircleShape,
        color = MinLishPrimaryContainer.copy(alpha = 0.7f),
        shadowElevation = 5.dp
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Ảnh đại diện của $displayName",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.minlish_app_icon),
                    contentDescription = "Ảnh đại diện mặc định",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    ProfileCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MinLishSurface,
        shadowColor = MinLishPrimary.copy(alpha = 0.12f)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MinLishTextPrimary
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun ProfileInfoRow(
    @DrawableRes iconRes: Int,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BubbleIcon(iconRes = iconRes, color = color)
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MinLishTextSecondary)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MinLishTextPrimary
            )
        }
    }
}

@Composable
private fun ProfileToggleRow(
    @DrawableRes iconRes: Int,
    label: String,
    checked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BubbleIcon(iconRes = iconRes, color = MinLishPrimary)
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MinLishTextPrimary
        )
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MinLishSurface,
                checkedTrackColor = MinLishPrimary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun ProfileActionRow(
    @DrawableRes iconRes: Int,
    label: String,
    value: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = color.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BubbleIcon(iconRes = iconRes, color = color)
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyLarge, color = MinLishTextPrimary)
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelLarge,
                    color = MinLishTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Image(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                colorFilter = ColorFilter.tint(color.copy(alpha = 0.72f))
            )
        }
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MinLishErrorContainer.copy(alpha = 0.72f),
        border = BorderStroke(1.5.dp, MinLishError.copy(alpha = 0.45f))
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MinLishError
        )
    }
}

@Composable
private fun BubbleIcon(
    @DrawableRes iconRes: Int,
    color: Color,
    size: Dp = 40.dp
) {
    Surface(
        shape = CircleShape,
        color = MinLishSurfaceContainer.copy(alpha = 0.8f),
        modifier = Modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(size * 0.48f),
                colorFilter = ColorFilter.tint(color)
            )
        }
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    containerColor: Color,
    shadowColor: Color,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    Box(modifier = modifier.padding(bottom = 5.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 5.dp)
                .background(shadowColor, shape)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            color = containerColor,
            border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.46f))
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        authUser = null,
        profileState = ProfileUiState(
            profile = null,
            avatarBytes = null,
            errorMessage = null
        ),
        onEditProfile = {},
        onNotifications = {},
        onAccountSecurity = {},
        onLogout = {}
    )
}
