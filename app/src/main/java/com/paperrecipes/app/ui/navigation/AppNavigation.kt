package com.paperrecipes.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paperrecipes.app.ui.auth.AuthScreen
import com.paperrecipes.app.ui.auth.AuthViewModel
import com.paperrecipes.app.ui.recipes.RecipeEditorScreen
import com.paperrecipes.app.ui.recipes.RecipeListScreen

object Routes {
    // Authentication view router
    const val AUTH: String = "auth"

    // Recipe list view
    const val LIST: String = "list"

    // Recipe editor view
    const val EDIT: String = "edit"

    // Recipe make view (MAKE stands for the moment that the user starts to cook a specific recipe)
    const val MAKE_RECIPE: String = "make"

    // Recipe preview
    const val PREVIEW: String = "preview"



}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    // val recipeViewModel: RecipeViewModel = viewModel()

    val startDestination = if (authState.isSignedIn) Routes.LIST else Routes.AUTH

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("auth") {
            AuthScreen(
                onSignedIn = {
                        navController.navigate(Routes.LIST) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                }
            )
        }

        composable("list") {
            RecipeListScreen(
                onLogOut = {
                    navController.navigate(Routes.AUTH)  {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onEdit = {
                    navController.navigate(Routes.EDIT)
                }
            )
        }

        composable(
            route = "edit?recipeId={recipeId}",
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
            ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            RecipeEditorScreen(
                recipeId = recipeId,
                onSaveRecipe = {
                    navController.navigate(Routes.LIST) {
                        popUpTo(Routes.LIST) { inclusive = true }
                    }
                },
                onBack = {
                    navController.navigate(Routes.LIST)
                }
            )
        }

    }
}