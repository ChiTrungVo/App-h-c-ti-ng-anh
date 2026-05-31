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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.QuizOptionCard
import com.example.mobile_project.ui.components.QuizOptionState
import com.example.mobile_project.ui.components.SecondaryButton

@Composable
fun QuizScreen(onResult: () -> Unit) {
    val question = SampleData.quizQuestions.first()
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var checked by remember { mutableStateOf(false) }
    val isCorrect = selectedOption == question.correctAnswer

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
                question.correctAnswer -> if (checked) QuizOptionState.Correct else if (selectedOption == option) QuizOptionState.Selected else QuizOptionState.Default
                selectedOption -> if (checked) QuizOptionState.Incorrect else QuizOptionState.Selected
                else -> QuizOptionState.Default
            }
            QuizOptionCard(
                text = option,
                state = state,
                onClick = {
                    selectedOption = option
                    checked = false
                }
            )
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(14.dp))
        if (checked) {
            Text(
                if (isCorrect) {
                    "Chính xác. Journey nghĩa là chuyến đi."
                } else {
                    "Chưa đúng. Đáp án đúng là ${question.correctAnswer}."
                },
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(Modifier.height(18.dp))
        SecondaryButton(
            "Kiểm tra",
            onClick = { checked = selectedOption != null },
            enabled = selectedOption != null
        )
        Spacer(Modifier.height(10.dp))
        PrimaryButton("Câu tiếp theo", onClick = onResult, enabled = checked)
    }
}
