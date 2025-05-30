package com.github.stephenwanjala.brecipes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.stephenwanjala.brecipes.ui.RecipeListScreen
import kotlinx.serialization.Serializable


@Composable
fun BRecipesNav(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.RecipeListDestination) {
        composable<Screen.RecipeListDestination> {
            RecipeListScreen(onRecipeClick = { recipeId ->

            })
        }

    }
}


sealed interface Screen {
    @Serializable
    data object RecipeListDestination : Screen

    @Serializable
    object RecipeDetailDestination : Screen

}