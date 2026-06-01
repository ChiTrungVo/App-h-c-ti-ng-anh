package com.example.mobile_project.ui.screens.practice

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.R
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSurface
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun PracticeTypeScreen(
    onFlashcard: () -> Unit,
    onQuiz: () -> Unit
) {
    val types = listOf(
        Triple("Trắc nghiệm", "Chọn nghĩa đúng của từ.", R.drawable.ic_quiz),
        Triple("Điền từ", "Hoàn thiện câu ví dụ.", R.drawable.ic_edit),
        Triple("Nghe và chọn", "Nghe phát âm rồi chọn đáp án.", R.drawable.ic_clock),
        Triple("Ghép cặp", "Ghép từ với nghĩa tiếng Việt.", R.drawable.ic_check_circle)
    )
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Text("Luyện tập", style = MaterialTheme.typography.headlineLarge)
        Text("Chọn dạng bài phù hợp với mục tiêu hôm nay.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        Card(
            onClick = onFlashcard,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MinLishSurface),
            border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.72f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_flashcard),
                    contentDescription = null,
                    modifier = Modifier.size(46.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Column(Modifier.weight(1f)) {
                    Text("Học từ mới", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Ôn flashcard theo nhịp ghi nhớ và quay lại các từ cần luyện.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            PrimaryButton(
                text = "Vào flashcard",
                onClick = onFlashcard,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text("Các dạng luyện tập", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        types.forEachIndexed { index, item ->
            PracticeTypeCard(item.first, item.second, item.third, onClick = if (index == 0) onQuiz else ({}))
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(120.dp))
    }
}

@Composable
private fun PracticeTypeCard(title: String, description: String, icon: Int, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(38.dp), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
