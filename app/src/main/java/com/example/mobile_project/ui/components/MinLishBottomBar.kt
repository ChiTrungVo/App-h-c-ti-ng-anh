package com.example.mobile_project.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.mobile_project.ui.navigation.BottomNavItem
import com.example.mobile_project.ui.theme.MinLishPrimary
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSurface
import com.example.mobile_project.ui.theme.MinLishTextSecondary

@Composable
fun MinLishBottomBar(
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .height(74.dp),
        shape = RoundedCornerShape(38.dp),
        color = MinLishSurface.copy(alpha = 0.96f),
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .border(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.72f), RoundedCornerShape(38.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.22f), RoundedCornerShape(38.dp))
                .padding(horizontal = 10.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.items.forEach { item ->
                MinLishNavigationItem(
                    item = item,
                    selected = currentRoute == item.route,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.MinLishNavigationItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .semantics {
                contentDescription = item.label
                role = Role.Tab
                this.selected = selected
            },
        shape = RoundedCornerShape(28.dp),
        color = if (selected) MinLishPrimaryContainer.copy(alpha = 0.82f) else MaterialTheme.colorScheme.surface.copy(alpha = 0f),
        shadowElevation = if (selected) 6.dp else 0.dp,
        tonalElevation = if (selected) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = if (selected) 1.dp else 0.dp,
                    color = if (selected) MinLishPrimary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            OceanNavIcon(
                item = item,
                selected = selected,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun OceanNavIcon(
    item: BottomNavItem,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (selected) MinLishPrimary else MinLishTextSecondary.copy(alpha = 0.74f)
    Canvas(modifier = modifier) {
        val stroke = Stroke(
            width = 2.8.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        val w = size.width
        val h = size.height
        when (item) {
            BottomNavItem.Home -> {
                val roof = Path().apply {
                    moveTo(w * 0.16f, h * 0.48f)
                    lineTo(w * 0.50f, h * 0.18f)
                    lineTo(w * 0.84f, h * 0.48f)
                }
                drawPath(roof, color, style = stroke)
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.25f, h * 0.45f),
                    size = Size(w * 0.50f, h * 0.36f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                    style = stroke
                )
            }
            BottomNavItem.Vocabulary -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.18f, h * 0.18f),
                    size = Size(w * 0.28f, h * 0.62f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                    style = stroke
                )
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.54f, h * 0.18f),
                    size = Size(w * 0.28f, h * 0.62f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                    style = stroke
                )
                drawLine(color, Offset(w * 0.50f, h * 0.22f), Offset(w * 0.50f, h * 0.82f), strokeWidth = 2.dp.toPx())
            }
            BottomNavItem.Learning -> {
                val wave = Path().apply {
                    moveTo(w * 0.12f, h * 0.58f)
                    cubicTo(w * 0.26f, h * 0.42f, w * 0.38f, h * 0.74f, w * 0.52f, h * 0.58f)
                    cubicTo(w * 0.66f, h * 0.42f, w * 0.76f, h * 0.74f, w * 0.90f, h * 0.58f)
                }
                drawPath(wave, color, style = stroke)
                drawCircle(color, radius = w * 0.10f, center = Offset(w * 0.38f, h * 0.28f), style = stroke)
                drawCircle(color, radius = w * 0.07f, center = Offset(w * 0.62f, h * 0.30f), style = stroke)
                drawLine(color, Offset(w * 0.18f, h * 0.76f), Offset(w * 0.82f, h * 0.76f), strokeWidth = 2.6.dp.toPx(), cap = StrokeCap.Round)
            }
            BottomNavItem.Progress -> {
                val barWidth = w * 0.13f
                listOf(0.68f, 0.48f, 0.28f).forEachIndexed { index, top ->
                    val left = w * (0.20f + index * 0.24f)
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(left, h * top),
                        size = Size(barWidth, h * (0.82f - top)),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                }
                drawLine(color, Offset(w * 0.14f, h * 0.84f), Offset(w * 0.86f, h * 0.84f), strokeWidth = 2.4.dp.toPx(), cap = StrokeCap.Round)
            }
        }
    }
}
