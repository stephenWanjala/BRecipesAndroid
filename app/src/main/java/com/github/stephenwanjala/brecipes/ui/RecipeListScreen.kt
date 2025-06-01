package com.github.stephenwanjala.brecipes.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import com.github.stephenwanjala.brecipes.domain.Recipe
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RecipeListScreen(
    onRecipeClick: (Recipe) -> Unit,
    recipes: LazyPagingItems<Recipe>,
    selectedRecipe: Recipe?,
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isPhone =!adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(recipes.loadState) {
        if (recipes.loadState.refresh is LoadState.Error) {
            snackbarHostState.showSnackbar(message = "Error: " + (recipes.loadState.refresh as LoadState.Error).error.message)
        }
    }

    val showFab by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Recipes") },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                AnimatedVisibility(showFab && isPhone) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Scroll To The top"
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues), contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = lazyListState
                ) {
                    items(recipes) { recipe ->
                        if (recipe != null) {
                            RecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe) },
                                isSelected = selectedRecipe?.id == recipe.id
                            )
                        }
                    }
                    item {
                        if (recipes.loadState.append is LoadState.Loading) {
                            CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
                if (recipes.loadState.refresh is LoadState.Loading) {
                    CircularWavyProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}


fun <T : Any> LazyListScope.items(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount) { index ->
        itemContent(lazyPagingItems[index])
    }
}
