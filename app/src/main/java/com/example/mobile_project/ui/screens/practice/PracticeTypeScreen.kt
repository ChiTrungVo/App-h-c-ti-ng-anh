package com.example.mobile_project.ui.screens.practice

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.R
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.MimiMood
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.theme.MinLishPrimary
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSurface
import com.example.mobile_project.ui.theme.Mobile_projectTheme
import com.example.mobile_project.ui.theme.MinLishTextPrimary
import com.example.mobile_project.ui.theme.MinLishTextSecondary

@Composable
fun PracticeTypeScreen(
    onFlashcard: () -> Unit,
    onQuiz: () -> Unit
) {
    val practiceTypes = listOf(
        PracticeTypeItem(
            title = "Trắc nghiệm",
            iconRes = R.drawable.ic_quiz,
            containerColor = Color(0xFFFFECB3),
            borderColor = Color(0xFFFFE082),
            shadowColor = Color(0xFFFFC107),
            iconTint = Color(0xFFFFB300),
            onClick = onQuiz
        )
    )

    OceanBubblyBackground(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Luyện tập",
                style = MaterialTheme.typography.displayLarge,
                color = MinLishPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Surface(
                shape = CircleShape,
                color = MinLishSurface,
                border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.7f)),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "Chọn một bài tập để bắt đầu nào!",
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MinLishTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(28.dp))
            MascotGuidance()
            Spacer(Modifier.height(26.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PracticeTypeCard(
                    item = practiceTypes[0],
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .aspectRatio(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            Spacer(Modifier.height(104.dp))
        }
    }
}

@Composable
private fun MascotGuidance() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        MascotBadge(size = 88.dp, mood = MimiMood.Welcome)
        Spacer(Modifier.width(12.dp))
        Surface(
            modifier = Modifier.weight(1f, fill = false),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomEnd = 24.dp,
                bottomStart = 6.dp
            ),
            color = MinLishSurface,
            border = BorderStroke(2.dp, MinLishPrimaryContainer.copy(alpha = 0.8f)),
            shadowElevation = 2.dp
        ) {
            Text(
                text = "Cố lên bạn nhé! Chúng ta cùng ôn bài nào.",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MinLishTextPrimary
            )
        }
    }
}

@Composable
private fun PracticeTypeCard(
    item: PracticeTypeItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(bottom = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp)
                .background(item.shadowColor, RoundedCornerShape(28.dp))
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = item.onClick),
            shape = RoundedCornerShape(28.dp),
            color = item.containerColor,
            border = BorderStroke(2.dp, item.borderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MinLishSurface,
                    shadowElevation = 2.dp
                ) {
                    Image(
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(36.dp),
                        colorFilter = ColorFilter.tint(item.iconTint)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MinLishTextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PracticeTypeScreenPreview() {
    Mobile_projectTheme {
        PracticeTypeScreen(
            onFlashcard = {},
            onQuiz = {}
        )
    }
}
private data class PracticeTypeItem(
    val title: String,
    @param:DrawableRes val iconRes: Int,
    val containerColor: Color,
    val borderColor: Color,
    val shadowColor: Color,
    val iconTint: Color,
    val onClick: () -> Unit
)
