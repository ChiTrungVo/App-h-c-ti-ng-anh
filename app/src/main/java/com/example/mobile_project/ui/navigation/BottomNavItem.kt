package com.example.mobile_project.ui.navigation

import androidx.annotation.DrawableRes
import com.example.mobile_project.R

sealed class BottomNavItem(
    val route: String,
    val label: String,
    @param:DrawableRes val iconRes: Int
) {
    data object Home : BottomNavItem("home", "Trang chủ", R.drawable.ic_home)
    data object Vocabulary : BottomNavItem("vocabulary", "Từ vựng", R.drawable.ic_vocabulary)
    data object Learning : BottomNavItem("learning", "Học", R.drawable.ic_learning)
    data object Progress : BottomNavItem("progress", "Tiến độ", R.drawable.ic_progress)

    companion object {
        val items = listOf(Home, Vocabulary, Learning, Progress)
    }
}
