package com.example.mobile_project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@Composable
fun MinLishTopBar(
    title: String,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.minlish_app_icon),
                contentDescription = "Logo MinLish",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MinLishPrimaryContainer.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            )
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            tonalElevation = 2.dp,
            modifier = Modifier
                .size(46.dp)
                .border(1.dp, MinLishPrimaryContainer.copy(alpha = 0.72f), CircleShape)
                .clickable(onClick = onProfileClick)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = "Hồ sơ",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}


@Preview
@Composable
fun MinLishTopBarPreview() {
    MinLishTopBar(
        title = "MinLish",
        onProfileClick = { /* Xử lý khi nhấn vào ảnh hồ sơ */ }
    )
}
