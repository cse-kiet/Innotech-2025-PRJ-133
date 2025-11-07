package com.example.selfguardian.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.selfguardian.screens.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {

        composable("dashboard") { DashboardScreen(navController) }
        composable("chat") { ChatScreen(navController) }

        composable("alerts") {
            val sampleItems = listOf(
                Items("Yogurt", "Food", LocalDate.now()),
                Items("Milk", "Food", LocalDate.now().plusDays(3)),
                Items("Aspirin", "Medicine", LocalDate.now().plusDays(10)),
                Items("Sunscreen", "Other", LocalDate.now().plusDays(17)),
                Items("Yogurt", "Food", LocalDate.now()),
                Items("Milk", "Food", LocalDate.now().plusDays(3)),
                Items("Aspirin", "Medicine", LocalDate.now().plusDays(10)),
                Items("Sunscreen", "Other", LocalDate.now().plusDays(17))
            )
            AlertScreen(items = sampleItems)
        }

        composable("addItem") { AddItemScreen() }
        composable("analytics") { AnalyticsScreen() }
        composable("help"){ HelpScreen() }
        composable("settings"){ SettingsScreen() }
        composable("profile") { ProfileScreen() }

        composable(
            route = "itemDetail/{name}/{category}/{date}/{daysLeft}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("daysLeft") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val daysLeft = backStackEntry.arguments?.getInt("daysLeft") ?: 0

            val item = ExpiringItems(
                name = name,
                category = category,
                date = date,
                daysLeft = daysLeft,
                icon = com.example.selfguardian.R.drawable.ic_launcher_foreground
            )

            ItemDetailScreen(item = item, onBack = { navController.popBackStack() })
        }

    }
}
