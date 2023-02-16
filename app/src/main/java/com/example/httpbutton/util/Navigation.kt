package com.example.httpbutton.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.httpbutton.screens.*

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.PictureScreen.route) {
        composable(
            route = Screen.FactsScreen.route, arguments = listOf(navArgument(factExport) {
                type = NavType.StringType
            })
        ) {
            val fileToExport = it.arguments?.getString(factExport)
            FactsScreen(navController = navController, factExport = fileToExport)
        }
        composable(
            route = Screen.EditScreen.route, arguments = listOf(navArgument(factToImport) {
                type = NavType.StringType
            })
        ) {
            val fact = it.arguments?.getString(factToImport)
            EditScreen(fact = fact, navController = navController)

        }
        composable(
            route = Screen.PictureScreen.route,
        ) {
            PictureScreen(navController = navController)
        }
    }
}
