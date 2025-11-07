package com.example.selfguardian.navigation



import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.selfguardian.screens.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }


        composable("onboarding") {
            OnboardingScreen(
                onGetStartedClick = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }


        composable("login") {
            val activity = LocalContext.current as Activity  // get Activity for Google Sign-In

            LoginScreen(
                context = activity,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate("signup") },
                onForgotPasswordClick = { navController.navigate("forgotPassword") }
            )
        }

        composable("signup") {
            val activity = LocalContext.current as Activity

            SignUpScreen(
                context = activity,
                onSignUpSuccess = {
                    // Navigate to main screen after successful sign-up
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onLoginClick = { navController.navigate("login") },
//                onGoogleSignUpClick = {
//                    // Call Google Sign-Up (reuse your GoogleSignInButton logic)
//                    // If you implemented GoogleSignIn inside SignUpScreen, it will handle navigation
//                }
            )
        }

        composable("forgotPassword") { ForgotPasswordScreen(
            onBackToLoginClick = {
                navController.popBackStack()
            }
        ) }

        composable("main") {
            NavBarScreen(
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
