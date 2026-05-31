package com.example.mobile_project.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R

@Composable
fun WhaleMascot(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    @DrawableRes asset: Int = R.drawable.mimi_whale
) {
    Image(
        painter = painterResource(asset),
        contentDescription = "Linh vật cá voi MinLish",
        modifier = modifier.size(size)
    )
}
