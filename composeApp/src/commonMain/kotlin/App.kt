package com.maiki.rok_helper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.navigation.compose.rememberNavController
import com.maiki.rok_helper.navigation.NavGraph
import com.maiki.rok_helper.ui.theme.RoKHelperTheme

@Composable
fun App() {
    val navController = rememberNavController()
    RoKHelperTheme {
        NavGraph(navController = navController)
    }
}
