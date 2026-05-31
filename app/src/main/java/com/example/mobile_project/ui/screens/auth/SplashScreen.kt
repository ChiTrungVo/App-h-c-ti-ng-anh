package com.example.mobile_project.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R

@Composable
fun SplashScreen(
    errorMessage: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Bubble(Modifier.align(Alignment.TopStart).size(126.dp), 0.28f)
        Bubble(Modifier.align(Alignment.TopEnd).padding(top = 74.dp).size(74.dp), 0.22f)
        Bubble(Modifier.align(Alignment.BottomStart).padding(bottom = 92.dp).size(86.dp), 0.2f)
        Image(
            painter = painterResource(R.drawable.ic_wave),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomCenter).size(width = 220.dp, height = 72.dp)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(54.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                shadowElevation = 18.dp,
                tonalElevation = 6.dp
            ) {
                Image(
                    painter = painterResource(R.drawable.minlish_app_icon),
                    contentDescription = "Mimi loading",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(128.dp)
                )
            }
            Spacer(Modifier.height(18.dp))
            Text("MinLish", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
            Text("Học từ vựng mỗi ngày", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text("Đang kiểm tra đăng nhập...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            errorMessage?.let { message ->
                Spacer(Modifier.height(18.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun Bubble(modifier: Modifier, alpha: Float) {
    Box(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha),
            shape = CircleShape
        )
    )
}
