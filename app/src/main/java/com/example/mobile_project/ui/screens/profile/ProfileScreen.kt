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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
    
    // Format createdAt (e.g. 2023-10-27T10:00:00.000Z -> 27-10-2023)
    val joinDate = profile?.createdAt?.takeIf { it.isNotBlank() }?.let { dateString ->
        try {
            val datePart = dateString.substringBefore("T")
            val parts = datePart.split("-")
            if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else datePart
        } catch (e: Exception) {
            null
        }
    }
    
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
        
        if (joinDate != null) {
            Spacer(Modifier.height(4.dp))
            Text("Tham gia: $joinDate", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
        
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
                if (!profile?.preferredLearningStyle.isNullOrBlank()) {
                    ProfileRow(R.drawable.ic_book, "Sở thích học", profile?.preferredLearningStyle.orEmpty())
                }
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
                Text("Ứng dụng", style = MaterialTheme.typography.titleLarge)
                ProfileToggleRow(
                    iconRes = R.drawable.ic_bell, // Assume using bell or similar for sound
                    label = "Âm thanh",
                    checked = profile?.soundEnabled ?: true
                )
                ProfileToggleRow(
                    iconRes = R.drawable.ic_home, // Assume using home or similar for theme
                    label = "Giao diện tối",
                    checked = profile?.darkModeEnabled ?: false
                )
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
        Spacer(Modifier.height(100.dp)) // Giảm khoảng trống cứng
    }
}

@Composable
private fun ProfileToggleRow(@DrawableRes iconRes: Int, label: String, checked: Boolean) {
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
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        // Lưu ý: Switch ở đây chỉ là hiển thị trạng thái hiện tại (Read-only)
        // Việc thay đổi cần thực hiện qua trang "Chỉnh sửa hồ sơ" hoặc cần cập nhật ViewModel
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.surface,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
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
