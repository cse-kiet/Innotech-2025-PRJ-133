package com.example.selfguardian.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBarScreen(onLogoutClick: () -> Unit = {}) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    // 🎨 Your theme colors
    val gradientTop = Color(0xFFB3E5FC)
    val gradientBottom = Color(0xFFE1BEE7)
    val primaryPurple = Color(0xFF7E57C2)
    val textColor = Color(0xFF3E3E3E)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = navBackStackEntry?.destination?.route ?: "dashboard"

    val drawerItems = listOf(
        DrawerItem("Alerts", Icons.Default.Notifications, "alerts"),
        DrawerItem("Add Item", Icons.Default.AddCircle, "addItem"),
        DrawerItem("Analytics", Icons.Default.BarChart, "analytics"),
        DrawerItem("Profile", Icons.Default.Person, "profile"),
        DrawerItem("Help", Icons.Default.Help, "help"),
        DrawerItem("Settings", Icons.Default.Settings, "settings"),
        DrawerItem("Logout", Icons.Default.ExitToApp, "logout")
    )

    val bottomItems = listOf(
        DrawerItem("Home", Icons.Default.Home, "dashboard"),
        DrawerItem("Alerts", Icons.Default.Notifications, "alerts"),
        DrawerItem("Add", Icons.Default.AddCircle, "addItem"),
        DrawerItem("Analytics", Icons.Default.BarChart, "analytics"),
        DrawerItem("Profile", Icons.Default.Person, "profile")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White
            ) {
                // Gradient Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(gradientTop, gradientBottom)
                            )
                        )
                        .padding(vertical = 40.dp)
                ) {
                    Text(
                        text = "Self Guardian",
                        style = MaterialTheme.typography.titleLarge,
                        color = primaryPurple,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Divider(color = Color.Gray.copy(alpha = 0.3f))

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title, color = textColor) },
                        selected = currentScreen == item.route,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            if (item.route == "logout") {
                                onLogoutClick()
                            } else {
                                navController.navigate(item.route) {
                                    popUpTo("dashboard") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.title,
                                tint = primaryPurple
                            )
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            selectedContainerColor = gradientTop.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (currentScreen != "dashboard" && currentScreen != "chat") {
                    TopAppBar(
                        title = { Text(currentScreen.replaceFirstChar { it.uppercase() }) },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = primaryPurple,
                            titleContentColor = Color.White
                        )
                    )
                }
            },
            bottomBar = {
                if (currentScreen != "chat") {
                    NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                        bottomItems.forEach { item ->
                            NavigationBarItem(
                                selected = currentScreen == item.route,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo("dashboard") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title,
                                        tint = if (currentScreen == item.route) primaryPurple else Color.Gray
                                    )
                                },
                                label = {
                                    Text(
                                        item.title,
                                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                        color = if (currentScreen == item.route) primaryPurple else Color.Gray
                                    )
                                }
                            )
                        }
                    }
                }
            },
            containerColor = Color(0xFFF5F3FA)
        ) { innerPadding ->
            val adjustedPadding = if (currentScreen == "dashboard" || currentScreen == "chat") {
                PaddingValues(0.dp)
            } else innerPadding

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(adjustedPadding)
            ) {
                MainNavGraph(navController)
            }
        }
    }
}

data class DrawerItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
