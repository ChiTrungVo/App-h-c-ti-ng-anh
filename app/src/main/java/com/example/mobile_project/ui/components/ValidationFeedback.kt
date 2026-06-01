package com.example.mobile_project.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

enum class FeedbackMessageType {
    Error,
    Success,
    Info
}

@Composable
fun ValidationMessageBox(
    message: String,
    modifier: Modifier = Modifier
) {
    FeedbackMessageBox(
        message = message,
        modifier = modifier,
        type = FeedbackMessageType.Error
    )
}

@Composable
fun FeedbackMessageBox(
    message: String,
    modifier: Modifier = Modifier,
    type: FeedbackMessageType = FeedbackMessageType.Info
) {
    val color = when (type) {
        FeedbackMessageType.Error -> MaterialTheme.colorScheme.error
        FeedbackMessageType.Success -> MaterialTheme.colorScheme.primary
        FeedbackMessageType.Info -> MaterialTheme.colorScheme.tertiary
    }
    val containerColor = when (type) {
        FeedbackMessageType.Error -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.72f)
        FeedbackMessageType.Success -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
        FeedbackMessageType.Info -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.42f)
    }
    val iconRes = when (type) {
        FeedbackMessageType.Error -> R.drawable.ic_error_outline
        FeedbackMessageType.Success -> R.drawable.ic_check_circle
        FeedbackMessageType.Info -> R.drawable.ic_bell
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(color)
            )
            Text(
                text = message,
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                textAlign = TextAlign.Start
            )
        }
    }
}
