package com.example.mobile_project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobile_project.ui.navigation.BottomNavItem
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@Composable
fun MinLishBottomBar(
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .height(78.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(39.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shadowElevation = 12.dp,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .border(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.72f), RoundedCornerShape(39.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
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
}

@Composable
private fun RowScope.MinLishNavigationItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "bottomNavContentColor"
    )
    val underlineWidth by animateDpAsState(
        targetValue = if (selected) 30.dp else 0.dp,
        label = "bottomNavUnderlineWidth"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        label = "bottomNavIconScale"
    )
    val iconLift by animateFloatAsState(
        targetValue = if (selected) -3f else 0f,
        label = "bottomNavIconLift"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(64.dp)
            .semantics {
                contentDescription = item.label
                role = Role.Tab
                this.selected = selected
            },
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OceanNavIcon(
                    item = item,
                    tint = contentColor,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                            translationY = iconLift
                        }
                )
                Text(
                    text = item.label,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .width(underlineWidth)
                        .height(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(99.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun OceanNavIcon(
    item: BottomNavItem,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = item.iconRes),
        contentDescription = item.label,
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint)
    )
}

@Preview
@Composable
fun MinLishBottomBarPreview() {
    MinLishBottomBar(
        currentRoute = "home",
        onItemClick = {}
    )
}
