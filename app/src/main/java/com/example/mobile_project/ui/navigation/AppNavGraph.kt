package com.example.mobile_project.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobile_project.ui.components.MinLishBottomBar
import com.example.mobile_project.ui.screens.auth.ForgotPasswordScreen
import com.example.mobile_project.ui.screens.auth.LoginScreen
import com.example.mobile_project.ui.screens.auth.RegisterScreen
import com.example.mobile_project.ui.screens.auth.SplashScreen
import com.example.mobile_project.ui.screens.home.HomeScreen
import com.example.mobile_project.ui.screens.learning.DailyLearningPlanScreen
import com.example.mobile_project.ui.screens.learning.FlashcardSessionScreen
import com.example.mobile_project.ui.screens.learning.SessionResultScreen
import com.example.mobile_project.ui.screens.practice.PracticeTypeScreen
import com.example.mobile_project.ui.screens.practice.QuizResultScreen
import com.example.mobile_project.ui.screens.practice.QuizScreen
import com.example.mobile_project.ui.screens.profile.EditProfileScreen
import com.example.mobile_project.ui.screens.profile.LogoutDialogScreen
import com.example.mobile_project.ui.screens.profile.NotificationSettingsScreen
import com.example.mobile_project.ui.screens.profile.ProfileScreen
import com.example.mobile_project.ui.screens.progress.ProgressDashboardScreen
import com.example.mobile_project.ui.screens.vocabulary.EditVocabularySetScreen
import com.example.mobile_project.ui.screens.vocabulary.EditWordScreen
import com.example.mobile_project.ui.screens.vocabulary.VocabularySetDetailScreen
import com.example.mobile_project.ui.screens.vocabulary.VocabularySetListScreen

object AppRoutes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val ForgotPassword = "forgot_password"
    const val Home = "home"
    const val Vocabulary = "vocabulary"
    const val VocabularyDetail = "vocabulary_detail"
    const val EditVocabularySet = "edit_vocabulary_set"
    const val EditWord = "edit_word"
    const val Learning = "learning"
    const val Flashcard = "flashcard"
    const val SessionResult = "session_result"
    const val Practice = "practice"
    const val Quiz = "quiz"
    const val QuizResult = "quiz_result"
    const val Progress = "progress"
    const val Profile = "profile"
    const val EditProfile = "edit_profile"
    const val Notifications = "notifications"
    const val LogoutDialog = "logout_dialog"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val bottomRoutes = BottomNavItem.items.map { it.route }.toSet()

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomRoutes) {
                MinLishBottomBar(
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Splash,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoutes.Splash) {
                SplashScreen()
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(900)
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(AppRoutes.Splash) { inclusive = true }
                    }
                }
            }
            composable(AppRoutes.Login) {
                LoginScreen(
                    onLogin = { navController.navigate(AppRoutes.Home) },
                    onRegister = { navController.navigate(AppRoutes.Register) },
                    onForgotPassword = { navController.navigate(AppRoutes.ForgotPassword) }
                )
            }
            composable(AppRoutes.Register) {
                RegisterScreen(
                    onRegister = { navController.navigate(AppRoutes.Home) },
                    onLogin = { navController.navigate(AppRoutes.Login) }
                )
            }
            composable(AppRoutes.ForgotPassword) {
                ForgotPasswordScreen(
                    onSubmit = { navController.navigate(AppRoutes.Login) },
                    onBackToLogin = { navController.navigate(AppRoutes.Login) }
                )
            }
            composable(AppRoutes.Home) {
                HomeScreen(
                    onStartLearning = { navController.navigate(AppRoutes.Learning) },
                    onProfileClick = { navController.navigate(AppRoutes.Profile) },
                    onAddSet = { navController.navigate(AppRoutes.EditVocabularySet) },
                    onQuiz = { navController.navigate(AppRoutes.Quiz) },
                    onProgress = { navController.navigate(AppRoutes.Progress) }
                )
            }
            composable(AppRoutes.Vocabulary) {
                VocabularySetListScreen(
                    onSetClick = { navController.navigate(AppRoutes.VocabularyDetail) },
                    onAddClick = { navController.navigate(AppRoutes.EditVocabularySet) }
                )
            }
            composable(AppRoutes.VocabularyDetail) {
                VocabularySetDetailScreen(
                    onAddWord = { navController.navigate(AppRoutes.EditWord) },
                    onEditSet = { navController.navigate(AppRoutes.EditVocabularySet) },
                    onStartLearning = { navController.navigate(AppRoutes.Flashcard) },
                    onQuiz = { navController.navigate(AppRoutes.Quiz) }
                )
            }
            composable(AppRoutes.EditVocabularySet) {
                EditVocabularySetScreen(onSave = { navController.popBackStack() })
            }
            composable(AppRoutes.EditWord) {
                EditWordScreen(onSave = { navController.popBackStack() })
            }
            composable(AppRoutes.Learning) {
                DailyLearningPlanScreen(
                    onFlashcard = { navController.navigate(AppRoutes.Flashcard) },
                    onQuiz = { navController.navigate(AppRoutes.Quiz) }
                )
            }
            composable(AppRoutes.Flashcard) {
                FlashcardSessionScreen(onFinish = { navController.navigate(AppRoutes.SessionResult) })
            }
            composable(AppRoutes.SessionResult) {
                SessionResultScreen(
                    onContinue = { navController.navigate(AppRoutes.Home) },
                    onReview = { navController.navigate(AppRoutes.Flashcard) }
                )
            }
            composable(AppRoutes.Practice) {
                PracticeTypeScreen(onQuiz = { navController.navigate(AppRoutes.Quiz) })
            }
            composable(AppRoutes.Quiz) {
                QuizScreen(onResult = { navController.navigate(AppRoutes.QuizResult) })
            }
            composable(AppRoutes.QuizResult) {
                QuizResultScreen(onReviewWrong = { navController.navigate(AppRoutes.Quiz) })
            }
            composable(AppRoutes.Progress) {
                ProgressDashboardScreen(
                    onProfileClick = { navController.navigate(AppRoutes.Profile) },
                    onNotificationsClick = { navController.navigate(AppRoutes.Notifications) }
                )
            }
            composable(AppRoutes.Profile) {
                ProfileScreen(
                    onEditProfile = { navController.navigate(AppRoutes.EditProfile) },
                    onNotifications = { navController.navigate(AppRoutes.Notifications) },
                    onLogout = { navController.navigate(AppRoutes.LogoutDialog) }
                )
            }
            composable(AppRoutes.LogoutDialog) {
                LogoutDialogScreen(
                    onConfirm = {
                        navController.navigate(AppRoutes.Login) {
                            popUpTo(AppRoutes.Home) { inclusive = true }
                        }
                    },
                    onDismiss = { navController.popBackStack() }
                )
            }
            composable(AppRoutes.EditProfile) {
                EditProfileScreen(onSave = { navController.popBackStack() })
            }
            composable(AppRoutes.Notifications) {
                NotificationSettingsScreen(onSave = { navController.popBackStack() })
            }
        }
    }
}
