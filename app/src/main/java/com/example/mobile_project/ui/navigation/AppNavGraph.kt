package com.example.mobile_project.ui.navigation

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.mobile_project.core.util.ConnectivityStatus
import com.example.mobile_project.core.util.NetworkConnectivityObserver
import com.example.mobile_project.ui.components.MinLishBottomBar
import com.example.mobile_project.feature.auth.viewmodel.AuthViewModel
import com.example.mobile_project.feature.profile.viewmodel.NotificationSettingsViewModel
import com.example.mobile_project.feature.profile.viewmodel.ProfileViewModel
import com.example.mobile_project.ui.screens.auth.ForgotPasswordScreen
import com.example.mobile_project.ui.screens.auth.LoginScreen
import com.example.mobile_project.ui.screens.auth.RegisterScreen
import com.example.mobile_project.ui.screens.auth.ResetPasswordScreen
import com.example.mobile_project.ui.screens.auth.SplashScreen
import com.example.mobile_project.ui.screens.auth.VerifyEmailScreen
import com.example.mobile_project.ui.screens.home.HomeScreen
import com.example.mobile_project.ui.screens.learning.DailyLearningPlanScreen
import com.example.mobile_project.ui.screens.learning.FlashcardSessionScreen
import com.example.mobile_project.ui.screens.learning.SessionResultScreen
import com.example.mobile_project.ui.screens.practice.PracticeTypeScreen
import com.example.mobile_project.ui.screens.practice.QuizResultScreen
import com.example.mobile_project.ui.screens.practice.QuizScreen
import com.example.mobile_project.ui.screens.profile.AccountSecurityScreen
import com.example.mobile_project.ui.screens.profile.EditProfileScreen
import com.example.mobile_project.ui.screens.profile.LogoutDialogScreen
import com.example.mobile_project.ui.screens.profile.NotificationSettingsScreen
import com.example.mobile_project.ui.screens.profile.ProfileScreen
import com.example.mobile_project.ui.screens.progress.ProgressDashboardScreen
import com.example.mobile_project.ui.screens.vocabulary.EditVocabularySetScreen
import com.example.mobile_project.ui.screens.vocabulary.EditWordScreen
import com.example.mobile_project.ui.screens.vocabulary.VocabularySetDetailScreen
import com.example.mobile_project.ui.screens.vocabulary.VocabularySetListScreen
import com.example.mobile_project.ui.screens.common.NoInternetScreen
import com.example.mobile_project.feature.learning.viewmodel.LearningViewModel
import com.example.mobile_project.feature.practice.viewmodel.PracticeViewModel
import com.example.mobile_project.feature.progress.viewmodel.ProgressViewModel
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.mobile_project.feature.home.viewmodel.HomeViewModel

object AppRoutes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val ForgotPassword = "forgot_password"
    const val ResetPassword = "reset_password"
    const val VerifyEmail = "verify_email"
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
    const val AccountSecurity = "account_security"
    const val LogoutDialog = "logout_dialog"

    fun vocabularyDetail(setId: String) = "$VocabularyDetail/$setId"
    fun editVocabularySet(setId: String = "new") = "$EditVocabularySet/$setId"
    fun editWord(setId: String, wordId: String = "new") = "$EditWord/$setId/$wordId"
    fun flashcard(setId: String) = "$Flashcard/$setId"
    fun sessionResult(setId: String) = "$SessionResult/$setId"
    fun resetPassword(userId: String, secret: String) =
        "$ResetPassword?userId=${Uri.encode(userId)}&secret=${Uri.encode(secret)}"
    fun quiz(setId: String) = "$Quiz/$setId"
}

@Composable
fun AppNavGraph(
    incomingDeepLink: Uri? = null,
    onIncomingDeepLinkConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    var connectivityRetryKey by remember { mutableStateOf(0) }
    val connectivityObserver = remember(context, connectivityRetryKey) {
        NetworkConnectivityObserver(context)
    }
    val connectivityStatus by connectivityObserver.status.collectAsState(initial = ConnectivityStatus.Available)

    if (connectivityStatus == ConnectivityStatus.Unavailable) {
        NoInternetScreen(onRetry = {
            connectivityRetryKey += 1
        })
        return
    }

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val notificationSettingsViewModel: NotificationSettingsViewModel = viewModel(
        factory = NotificationSettingsViewModel.factory(
            context.applicationContext as Application
        )
    )
    val authState by authViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()
    val notificationSettingsState by notificationSettingsViewModel.uiState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentRootRoute = currentRoute.toBottomRootRoute()
    val lifecycleOwner = LocalLifecycleOwner.current
    val learningViewModel: LearningViewModel = viewModel()
    val practiceViewModel: PracticeViewModel = viewModel()
    val progressViewModel: ProgressViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                authViewModel.refreshSession()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(authState.user?.id, authState.user?.isEmailVerified) {
        if (authState.user?.isEmailVerified == true) {
            profileViewModel.loadProfile(force = true)
            notificationSettingsViewModel.loadSettings(force = true)
        }
    }

    LaunchedEffect(incomingDeepLink) {
        incomingDeepLink?.let { uri ->
            when (uri.host) {
                "verify-email" -> {
                    authViewModel.completeEmailVerification(uri)
                    navController.navigate(AppRoutes.VerifyEmail) {
                        launchSingleTop = true
                    }
                }
                "reset-password" -> {
                    val userId = uri.getQueryParameter("userId").orEmpty()
                    val secret = uri.getQueryParameter("secret").orEmpty()
                    navController.navigate(AppRoutes.resetPassword(userId, secret)) {
                        launchSingleTop = true
                    }
                }
            }
            onIncomingDeepLinkConsumed()
        }
    }

    LaunchedEffect(authState.user, authState.isCheckingSession, currentRoute) {
        if (authState.isCheckingSession || currentRoute == null || currentRoute == AppRoutes.Splash) return@LaunchedEffect

        val user = authState.user
        if (user == null && currentRoute !in authRoutes) {
            navController.navigate(AppRoutes.Login) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }

        if (user != null && !user.isEmailVerified && currentRoute !in emailVerificationAllowedRoutes) {
            navController.navigate(AppRoutes.VerifyEmail) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                launchSingleTop = true
            }
        }

        if (user != null && user.isEmailVerified && currentRoute in authRoutes + AppRoutes.VerifyEmail) {
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

                    val user = authState.user
                    val destination = when {
                        user == null -> AppRoutes.Login
                        !user.isEmailVerified -> AppRoutes.VerifyEmail
                        else -> AppRoutes.Home
                    }
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
                    onForgotPassword = { navController.navigate(AppRoutes.ForgotPassword) },
                    onClearMessage = authViewModel::clearMessage
                )
            }
            composable(AppRoutes.Register) {
                RegisterScreen(
                    authState = authState,
                    onRegister = authViewModel::register,
                    onLogin = { navController.navigate(AppRoutes.Login) },
                    onClearMessage = authViewModel::clearMessage
                )
            }
            composable(AppRoutes.ForgotPassword) {
                ForgotPasswordScreen(
                    authState = authState,
                    onSubmit = authViewModel::sendPasswordRecovery,
                    onBackToLogin = { navController.navigate(AppRoutes.Login) },
                    onClearMessage = authViewModel::clearMessage
                )
            }
            composable(
                route = "${AppRoutes.ResetPassword}?userId={userId}&secret={secret}",
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("secret") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { entry ->
                val userId = entry.arguments?.getString("userId").orEmpty()
                val secret = entry.arguments?.getString("secret").orEmpty()
                ResetPasswordScreen(
                    authState = authState,
                    onSubmit = { password, confirmPassword ->
                        authViewModel.completePasswordRecovery(
                            userId = userId,
                            secret = secret,
                            password = password,
                            confirmPassword = confirmPassword
                        )
                    },
                    onBackToLogin = { navController.navigate(AppRoutes.Login) },
                    onClearMessage = authViewModel::clearMessage
                )
            }
            composable(AppRoutes.VerifyEmail) {
                VerifyEmailScreen(
                    authState = authState,
                    onResendEmail = authViewModel::resendVerificationEmail,
                    onRefresh = authViewModel::refreshSession,
                    onBackToLogin = {
                        navController.navigate(AppRoutes.Login) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onLogout = {
                        authViewModel.logout {
                            navController.navigate(AppRoutes.Login) {
                                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
            composable(AppRoutes.Home) {
                val homeState by homeViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) { homeViewModel.loadHomeData() }

                val profile = profileState.profile
                HomeScreen(
                    displayName = profile?.displayName ?: authState.user?.displayName ?: "Người học MinLish",
                    dailyTargetMinutes = profile?.dailyTargetMinutes ?: 15,
                    studiedMinutesToday = homeState.studiedMinutesToday, // ← thật
                    totalWordsLearned = homeState.totalWordsLearned,     // ← thật
                    streakDays = homeState.streakDays,                   // ← thật
                    quizAccuracy = homeState.quizAccuracy,               // ← thật
                    onStartLearning = { navController.navigate(AppRoutes.Learning) },
                    onProfileClick = { navController.navigate(AppRoutes.Profile) },
                    onAddSet = { navController.navigate(AppRoutes.editVocabularySet()) },
                    onQuiz = { navController.navigate(AppRoutes.Practice) },
                    onProgress = { navController.navigate(AppRoutes.Progress) },
                    onVocabulary = { navController.navigate(AppRoutes.Vocabulary) }
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
                    onStartLearning = { navController.navigate(AppRoutes.flashcard(setId)) },
                    onQuiz = { navController.navigate(AppRoutes.quiz(setId)) },
                    onDeleteSet = { navController.popBackStack(AppRoutes.Vocabulary, false) }
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
                    onFlashcard = { setId -> navController.navigate(AppRoutes.flashcard(setId)) },
                    onQuiz = { setId -> navController.navigate(AppRoutes.quiz(setId)) },
                    learningViewModel = learningViewModel
                )
            }
            composable(AppRoutes.Flashcard) {
                FlashcardSessionScreen(
                    onFinish = { finishedSetId ->
                        if (finishedSetId.isNotBlank()) {
                            navController.navigate(AppRoutes.sessionResult(finishedSetId))
                        } else {
                            navController.popBackStack()
                        }
                    },
                    learningViewModel = learningViewModel
                )
            }
            composable(
                route = "${AppRoutes.Flashcard}/{setId}",
                arguments = listOf(navArgument("setId") { type = NavType.StringType })
            ) { backStackEntry ->
                val setId = backStackEntry.arguments?.getString("setId").orEmpty()
                FlashcardSessionScreen(
                    setId = setId,
                    onFinish = { finishedSetId ->
                        navController.navigate(AppRoutes.sessionResult(finishedSetId.ifBlank { setId }))
                    },
                    learningViewModel = learningViewModel
                )
            }
            composable(
                route = "${AppRoutes.SessionResult}/{setId}",
                arguments = listOf(navArgument("setId") { type = NavType.StringType })
            ) {
                SessionResultScreen(
                    onContinue = { navController.navigate(AppRoutes.Home) },
                    onReview = { setId -> navController.navigate(AppRoutes.flashcard(setId)) },
                    learningViewModel = learningViewModel
                )
            }
            composable(AppRoutes.SessionResult) {
                SessionResultScreen(
                    onContinue = { navController.navigate(AppRoutes.Home) },
                    onReview = { setId -> navController.navigate(AppRoutes.flashcard(setId)) },
                    learningViewModel = learningViewModel
                )
            }
            composable(AppRoutes.Practice) {
                PracticeTypeScreen(
                    onFlashcard = { navController.navigate(AppRoutes.Flashcard) },
                    onQuiz = { navController.navigate(AppRoutes.Quiz) }
                )
            }
            composable(AppRoutes.Quiz) {
                QuizScreen(
                    setId = "",
                    onResult = { navController.navigate(AppRoutes.QuizResult) },
                    practiceViewModel = practiceViewModel
                )
            }
            composable(
                route = "${AppRoutes.Quiz}/{setId}",
                arguments = listOf(navArgument("setId") { type = NavType.StringType })
            ) { backStackEntry ->
                val setId = backStackEntry.arguments?.getString("setId").orEmpty()
                QuizScreen(
                    setId = setId,
                    onResult = { navController.navigate(AppRoutes.QuizResult) },
                    practiceViewModel = practiceViewModel
                )
            }
            composable(AppRoutes.QuizResult) {
                QuizResultScreen(
                    onReviewWrong = { navController.popBackStack() },
                    onBackToVocabulary = {
                        navController.navigate(AppRoutes.Vocabulary) {
                            popUpTo(AppRoutes.Practice) { inclusive = false }
                        }
                    },
                    practiceViewModel = practiceViewModel,
                    onRetry = {
                        // Lấy setId từ practiceViewModel để navigate đúng
                        val setId = practiceViewModel.uiState.value.setId
                        if (setId.isNotBlank()) {
                            navController.navigate(AppRoutes.quiz(setId)) {
                                popUpTo(AppRoutes.QuizResult) { inclusive = true }
                            }
                        } else {
                            navController.navigate(AppRoutes.Quiz) {
                                popUpTo(AppRoutes.QuizResult) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(AppRoutes.Progress) {
                LaunchedEffect(Unit) { progressViewModel.loadProgress() }
                ProgressDashboardScreen(
                    onProfileClick = { navController.navigate(AppRoutes.Profile) },
                    onNotificationsClick = { navController.navigate(AppRoutes.Notifications) },
                    progressViewModel = progressViewModel
                )
            }
            composable(AppRoutes.Profile) {
                LaunchedEffect(Unit) {
                    profileViewModel.loadProfile()
                }
                ProfileScreen(
                    authUser = authState.user,
                    profileState = profileState,
                    onEditProfile = { navController.navigate(AppRoutes.EditProfile) },
                    onNotifications = { navController.navigate(AppRoutes.Notifications) },
                    onAccountSecurity = { navController.navigate(AppRoutes.AccountSecurity) },
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
                LaunchedEffect(Unit) {
                    profileViewModel.loadProfile()
                }
                EditProfileScreen(
                    state = profileState,
                    onFormChange = profileViewModel::updateForm,
                    onUploadAvatar = { uri -> profileViewModel.uploadAvatar(context, uri) },
                    onSave = { profileViewModel.saveProfile { navController.popBackStack() } }
                )
            }
            composable(AppRoutes.Notifications) {
                LaunchedEffect(Unit) {
                    notificationSettingsViewModel.loadSettings()
                }
                NotificationSettingsScreen(
                    state = notificationSettingsState,
                    onFormChange = notificationSettingsViewModel::updateForm,
                    onSave = { notificationSettingsViewModel.saveSettings { navController.popBackStack() } }
                )
            }
            composable(AppRoutes.AccountSecurity) {
                LaunchedEffect(Unit) {
                    profileViewModel.loadProfile()
                }
                AccountSecurityScreen(
                    state = profileState,
                    onFormChange = profileViewModel::updateSecurityForm,
                    onUpdateEmail = { profileViewModel.updateEmail() },
                    onUpdatePassword = { profileViewModel.updatePassword() },
                    onSoftDelete = {
                        profileViewModel.softDeleteAccount {
                            authViewModel.refreshSession()
                            navController.navigate(AppRoutes.Login) {
                                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
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
    AppRoutes.ForgotPassword,
    AppRoutes.ResetPassword,
    "${AppRoutes.ResetPassword}?userId={userId}&secret={secret}",
    AppRoutes.VerifyEmail
)

private val emailVerificationAllowedRoutes = setOf(
    AppRoutes.VerifyEmail,
    AppRoutes.LogoutDialog
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
    "${AppRoutes.Flashcard}/{setId}",
    AppRoutes.SessionResult,
    "${AppRoutes.SessionResult}/{setId}",
    AppRoutes.Quiz,
    "${AppRoutes.Quiz}/{setId}",
    AppRoutes.QuizResult -> AppRoutes.Practice

    AppRoutes.Profile,
    AppRoutes.Progress,
    AppRoutes.EditProfile,
    AppRoutes.Notifications,
    AppRoutes.AccountSecurity,
    AppRoutes.LogoutDialog -> AppRoutes.Profile

    else -> null
}
