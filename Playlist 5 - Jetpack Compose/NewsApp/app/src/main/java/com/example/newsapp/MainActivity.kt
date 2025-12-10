package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.newsapp.navigation.Screen
import com.example.newsapp.ui.detail.NewsArticlePage
import com.example.newsapp.ui.home.HomePage
import com.example.newsapp.ui.theme.NewsAppTheme
import com.example.newsapp.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsAppTheme {
                val navController = rememberNavController()
                val viewModel: NewsViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home
                ) {
                    composable<Screen.Home> {
                        HomePage(viewModel = viewModel, navController = navController)
                    }

                    composable<Screen.ArticleDetail> { backStackEntry ->
                        val args = backStackEntry.toRoute<Screen.ArticleDetail>()
                        NewsArticlePage(
                            url = args.url,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}