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
    data object Practice : BottomNavItem("practice", "Ôn luyện", R.drawable.ic_learning)
    data object Profile : BottomNavItem("profile", "Cá nhân", R.drawable.ic_profile)

    companion object {
        val items = listOf(Home, Vocabulary, Practice, Profile)
    }
}
