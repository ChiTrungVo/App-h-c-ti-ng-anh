package com.example.mobile_project.ui.screens.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.feature.auth.data.MinLishAuthUser
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@Composable
fun ProfileScreen(
    authUser: MinLishAuthUser?,
    onEditProfile: () -> Unit,
    onNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val displayName = authUser?.displayName ?: "Người học MinLish"
    val email = authUser?.email ?: "Chưa đăng nhập"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(28.dp))
        MascotBadge(size = 116.dp)
        Spacer(Modifier.height(14.dp))
        Text(displayName, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Text(email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                ProfileRow(R.drawable.ic_profile, "Ngôn ngữ mẹ đẻ", "Tiếng Việt")
                ProfileRow(R.drawable.ic_learning, "Ngôn ngữ học", "Tiếng Anh")
                ProfileRow(R.drawable.ic_progress, "Trình độ", "Beginner")
            }
        }
        Spacer(Modifier.height(12.dp))
        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Mục tiêu học tập", style = MaterialTheme.typography.titleLarge)
                ProfileRow(R.drawable.ic_clock, "Mục tiêu hằng ngày", "15 phút")
                ProfileRow(R.drawable.ic_check_circle, "Cam kết", "Duy trì học từ vựng mỗi ngày")
            }
        }
        Spacer(Modifier.height(12.dp))
        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Cài đặt", style = MaterialTheme.typography.titleLarge)
                ProfileRow(R.drawable.ic_bell, "Nhắc học", "20:30, T2 T4 T6 CN")
            }
        }
        Spacer(Modifier.height(20.dp))
        PrimaryButton("Chỉnh sửa hồ sơ", onClick = onEditProfile)
        Spacer(Modifier.height(12.dp))
        SecondaryButton("Cài đặt nhắc học", onClick = onNotifications)
        Spacer(Modifier.height(12.dp))
        SecondaryButton("Đăng xuất", onClick = onLogout)
        Spacer(Modifier.height(132.dp))
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
