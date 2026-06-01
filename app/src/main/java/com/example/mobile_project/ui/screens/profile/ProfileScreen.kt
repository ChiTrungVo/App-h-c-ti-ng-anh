package com.example.mobile_project.ui.screens.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import com.example.mobile_project.R
import com.example.mobile_project.feature.auth.data.MinLishAuthUser
import com.example.mobile_project.feature.profile.viewmodel.ProfileUiState
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

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
    val reminderText = "Xem trong cài đặt nhắc học"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(28.dp))
        AvatarBadge(avatarBytes = profileState.avatarBytes, displayName = displayName)
        Spacer(Modifier.height(14.dp))
        Text(displayName, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Text(email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        profileState.errorMessage?.let { message ->
            Spacer(Modifier.height(10.dp))
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(18.dp))
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.7f)),
            shadowElevation = 6.dp
        ) {
            Text(
                "Mimi sẽ cá nhân hóa bài học theo hồ sơ này.",
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(18.dp))
        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Thông tin cá nhân", style = MaterialTheme.typography.titleLarge)
                ProfileRow(R.drawable.ic_profile, "Ngôn ngữ mẹ đẻ", profile?.nativeLanguage ?: "vi")
                ProfileRow(R.drawable.ic_learning, "Ngôn ngữ học", profile?.targetLanguage ?: "en")
                ProfileRow(R.drawable.ic_progress, "Trình độ", profile?.proficiencyLevel ?: "beginner")
                if (!profile?.bio.isNullOrBlank()) {
                    ProfileRow(R.drawable.ic_edit, "Giới thiệu", profile?.bio.orEmpty())
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Mục tiêu học tập", style = MaterialTheme.typography.titleLarge)
                ProfileRow(R.drawable.ic_clock, "Mục tiêu hằng ngày", "${profile?.dailyTargetMinutes ?: 15} phút")
                ProfileRow(R.drawable.ic_check_circle, "Cam kết", profile?.studyGoal ?: "Duy trì học từ vựng mỗi ngày")
            }
        }
        Spacer(Modifier.height(12.dp))
        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Cài đặt", style = MaterialTheme.typography.titleLarge)
                ProfileRow(R.drawable.ic_bell, "Nhắc học", reminderText)
                ProfileRow(R.drawable.ic_lock, "Bảo mật", "Đổi email, mật khẩu hoặc xoá tài khoản")
            }
        }
        Spacer(Modifier.height(20.dp))
        PrimaryButton("Chỉnh sửa hồ sơ", onClick = onEditProfile)
        Spacer(Modifier.height(12.dp))
        SecondaryButton("Cài đặt nhắc học", onClick = onNotifications)
        Spacer(Modifier.height(12.dp))
        SecondaryButton("Tài khoản & bảo mật", onClick = onAccountSecurity)
        Spacer(Modifier.height(12.dp))
        SecondaryButton("Đăng xuất", onClick = onLogout)
        Spacer(Modifier.height(132.dp))
    }
}

@Composable
private fun AvatarBadge(avatarBytes: ByteArray?, displayName: String) {
    val bitmap = remember(avatarBytes) {
        avatarBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
        tonalElevation = 4.dp,
        modifier = Modifier.size(116.dp)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Ảnh đại diện của $displayName",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                MascotBadge(size = 108.dp)
            }
        }
    }
}

@Composable
private fun ProfileRow(@DrawableRes iconRes: Int, label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.32f),
            modifier = Modifier.size(38.dp)
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.padding(9.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
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
