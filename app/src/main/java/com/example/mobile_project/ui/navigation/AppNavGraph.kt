package com.example.mobile_project.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobile_project.ui.components.MinLishBottomBar
import com.example.mobile_project.feature.auth.viewmodel.AuthViewModel
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

    fun vocabularyDetail(setId: String) = "$VocabularyDetail/$setId"
    fun editVocabularySet(setId: String = "new") = "$EditVocabularySet/$setId"
    fun editWord(setId: String, wordId: String = "new") = "$EditWord/$setId/$wordId"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentRootRoute = currentRoute.toBottomRootRoute()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                authViewModel.refreshSession()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(authState.user, authState.isCheckingSession, currentRoute) {
        if (authState.isCheckingSession || currentRoute == null || currentRoute == AppRoutes.Splash) return@LaunchedEffect

        if (authState.user == null && currentRoute !in authRoutes) {
            navController.navigate(AppRoutes.Login) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }

        if (authState.user != null && currentRoute in authRoutes) {
            navController.navigate(AppRoutes.Home) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Splash,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(AppRoutes.Splash) {
                SplashScreen(errorMessage = authState.errorMessage)
                LaunchedEffect(authState.isCheckingSession, authState.user) {
                    if (authState.isCheckingSession) return@LaunchedEffect

                    val destination = if (authState.user == null) AppRoutes.Login else AppRoutes.Home
                    navController.navigate(destination) {
                        popUpTo(AppRoutes.Splash) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            composable(AppRoutes.Login) {
                LoginScreen(
                    authState = authState,
                    onLogin = authViewModel::login,
                    onGoogleLogin = {
                        (context as? ComponentActivity)?.let(authViewModel::loginWithGoogle)
                    },
                    onRegister = { navController.navigate(AppRoutes.Register) },
                    onForgotPassword = { navController.navigate(AppRoutes.ForgotPassword) }
                )
            }
            composable(AppRoutes.Register) {
                RegisterScreen(
                    authState = authState,
                    onRegister = authViewModel::register,
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
                    onAddSet = { navController.navigate(AppRoutes.editVocabularySet()) },
                    onQuiz = { navController.navigate(AppRoutes.Practice) },
                    onProgress = { navController.navigate(AppRoutes.Progress) }
                )
            }
            composable(AppRoutes.Vocabulary) {
                VocabularySetListScreen(
                    onSetClick = { setId -> navController.navigate(AppRoutes.vocabularyDetail(setId)) },
                    onAddClick = { navController.navigate(AppRoutes.editVocabularySet()) }
                )
            }
            composable(
                route = "${AppRoutes.VocabularyDetail}/{setId}",
                arguments = listOf(navArgument("setId") { type = NavType.StringType })
            ) { backStackEntry ->
                val setId = backStackEntry.arguments?.getString("setId").orEmpty()
                VocabularySetDetailScreen(
                    setId = setId,
                    onAddWord = { navController.navigate(AppRoutes.editWord(setId)) },
                    onEditWord = { wordId -> navController.navigate(AppRoutes.editWord(setId, wordId)) },
                    onEditSet = { navController.navigate(AppRoutes.editVocabularySet(setId)) },
                    onStartLearning = { navController.navigate(AppRoutes.Flashcard) },
                    onQuiz = { navController.navigate(AppRoutes.Quiz) }
                )
            }
            composable(
                route = "${AppRoutes.EditVocabularySet}/{setId}",
                arguments = listOf(navArgument("setId") { type = NavType.StringType })
            ) { backStackEntry ->
                EditVocabularySetScreen(
                    setId = backStackEntry.arguments?.getString("setId"),
                    onSave = { savedSetId ->
                        navController.navigate(AppRoutes.vocabularyDetail(savedSetId)) {
                            popUpTo(AppRoutes.Vocabulary)
                        }
                    }
                )
            }
            composable(
                route = "${AppRoutes.EditWord}/{setId}/{wordId}",
                arguments = listOf(
                    navArgument("setId") { type = NavType.StringType },
                    navArgument("wordId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                EditWordScreen(
                    setId = backStackEntry.arguments?.getString("setId").orEmpty(),
                    wordId = backStackEntry.arguments?.getString("wordId"),
                    onSave = { navController.popBackStack() }
                )
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
                PracticeTypeScreen(
                    onFlashcard = { navController.navigate(AppRoutes.Flashcard) },
                    onQuiz = { navController.navigate(AppRoutes.Quiz) }
                )
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
                    authUser = authState.user,
                    onEditProfile = { navController.navigate(AppRoutes.EditProfile) },
                    onNotifications = { navController.navigate(AppRoutes.Notifications) },
                    onLogout = { navController.navigate(AppRoutes.LogoutDialog) }
                )
            }
            composable(AppRoutes.LogoutDialog) {
                LogoutDialogScreen(
                    isLoading = authState.isLoading,
                    errorMessage = authState.errorMessage,
                    onConfirm = {
                        authViewModel.logout {
                            navController.navigate(AppRoutes.Login) {
                                popUpTo(AppRoutes.Home) { inclusive = true }
                                launchSingleTop = true
                            }
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

        if (currentRootRoute != null) {
            MinLishBottomBar(
                currentRoute = currentRootRoute,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

private val authRoutes = setOf(
    AppRoutes.Login,
    AppRoutes.Register,
    AppRoutes.ForgotPassword
)

private fun String?.toBottomRootRoute(): String? = when (this) {
    AppRoutes.Home,
    AppRoutes.Learning -> AppRoutes.Home

    AppRoutes.Vocabulary,
    AppRoutes.VocabularyDetail,
    AppRoutes.EditVocabularySet,
    AppRoutes.EditWord,
    "${AppRoutes.VocabularyDetail}/{setId}",
    "${AppRoutes.EditVocabularySet}/{setId}",
    "${AppRoutes.EditWord}/{setId}/{wordId}" -> AppRoutes.Vocabulary

    AppRoutes.Practice,
    AppRoutes.Flashcard,
    AppRoutes.SessionResult,
    AppRoutes.Quiz,
    AppRoutes.QuizResult -> AppRoutes.Practice

    AppRoutes.Profile,
    AppRoutes.Progress,
    AppRoutes.EditProfile,
    AppRoutes.Notifications,
    AppRoutes.LogoutDialog -> AppRoutes.Profile

    else -> null
}
