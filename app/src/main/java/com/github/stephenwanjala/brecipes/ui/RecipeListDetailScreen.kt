package com.github.stephenwanjala.brecipes.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import com.github.stephenwanjala.brecipes.domain.Recipe
import com.github.stephenwanjala.brecipes.ui.recipe_details.RecipeDetailsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun RecipeListDetailScreen(
    viewModel: RecipeViewModel = hiltViewModel(),
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Recipe>()
    val recipeState = viewModel.state.collectAsStateWithLifecycle()
    val recipes = viewModel.recipePagingFlow.collectAsLazyPagingItems<Recipe>()
    val scope = rememberCoroutineScope()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val canShowAppbar =
        !adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                RecipeListScreen(
                    onRecipeClick = { recipe ->
                        viewModel.onAction(RecipeAction.OnSelectRecipe(recipe.id))
                        scope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, recipe)
                        }
                    },
                    recipes = recipes,
                    selectedRecipe = recipeState.value.selectedRecipe
                )

            }
        },
        detailPane = {
            AnimatedPane {
                navigator.currentDestination?.contentKey?.let { recipe ->
                    RecipeDetailsScreen(
                        onNavigateBack = {
                            scope.launch {
                                viewModel.onAction(RecipeAction.OnNavigateUp)
                                navigator.navigateTo(ListDetailPaneScaffoldRole.List)
                            }
                        },
                        selectedRecipe = recipe,
                        canShowAppBar = canShowAppbar,
                        onShareRecipe = {},
                    )

                }

            }
        }
    )

}