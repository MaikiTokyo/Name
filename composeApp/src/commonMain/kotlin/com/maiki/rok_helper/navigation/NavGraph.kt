package com.maiki.rok_helper.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maiki.rok_helper.ui.screens.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Academy : Screen("tech")
    object Build : Screen("build")
    object Train : Screen("train")
    object Heal : Screen("heal")
    object Vip : Screen("vip")
    object Gear : Screen("gear")
    object Lyceum : Screen("lyceum")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigateToTool = { route ->
                navController.navigate(route)
            })
        }
        // В будущем сюда добавим остальные экраны
    }
}
