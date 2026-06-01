package com.example.mobile_project.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.QuizOptionCard
import com.example.mobile_project.ui.components.QuizOptionState
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun QuizScreen(onResult: () -> Unit) {
    val question = SampleData.quizQuestions.first()
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Text("Quiz từ vựng", style = MaterialTheme.typography.headlineLarge)
        Text("Câu 4/7", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(progress = { 0.57f }, modifier = Modifier.fillMaxWidth().height(10.dp), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primaryContainer)
        Spacer(Modifier.height(28.dp))
        Text(question.questionText, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(18.dp))
        question.options.forEach { option ->
            val state = when (option) {
                question.correctAnswer -> QuizOptionState.Correct
                "current" -> QuizOptionState.Incorrect
                else -> QuizOptionState.Default
            }
            QuizOptionCard(text = option, state = state, onClick = {})
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text("Chính xác. Journey nghĩa là chuyến đi.", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(18.dp))
        SecondaryButton("Kiểm tra", onClick = {})
        Spacer(Modifier.height(10.dp))
        PrimaryButton("Câu tiếp theo", onClick = onResult)
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizScreenPreview() {
    Mobile_projectTheme {
        QuizScreen(onResult = {})
    }
}
