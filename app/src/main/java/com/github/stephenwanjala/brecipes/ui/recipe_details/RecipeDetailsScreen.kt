package com.github.stephenwanjala.brecipes.ui.recipe_details

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.github.stephenwanjala.brecipes.R
import com.github.stephenwanjala.brecipes.domain.Recipe
import com.github.stephenwanjala.brecipes.ui.RecipeUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RecipeDetailsScreen(
    onNavigateBack: () -> Unit,
    onShareRecipe: () -> Unit,
    canShowAppBar: Boolean,
    selectedRecipe: Recipe?,
) {
    BackHandler {
        onNavigateBack()
    }
    var isFavorite by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "favorite-scale"
    )
    val tabs = listOf("OverView", "Ingredients", "Instructions")
    val selectedIndex = remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()
    val favoriteColor by animateColorAsState(
        targetValue = if (isFavorite) Color(0xFFFF7F50) else MaterialTheme.colorScheme.primary,
        label = "favorite-color"
    )
    LaunchedEffect(pagerState.currentPage) {
        selectedIndex.intValue = pagerState.currentPage
    }
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topBarState,
        canScroll = { true }
    )
    var expanded by rememberSaveable { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {


        if (selectedRecipe != null) {
            Scaffold(
                topBar = {
                    AnimatedVisibility(canShowAppBar) {
                        if (selectedIndex.intValue == 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data("${RecipeUtils.IMAGES_BASE_URL}${selectedRecipe.image}")
                                            .crossfade(true)
                                            .error(R.drawable.logo)
                                            .placeholder(R.drawable.logo)
                                            .build()
                                    ),
                                    contentDescription = selectedRecipe.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = .5f),
                                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = .5f),
                                                    Color.Transparent
                                                ),
                                                startY = 0f,
                                                endY = 250f
                                            )
                                        )
                                )
                                LargeTopAppBar(
                                    title = { /* No title here, it's below the image for Overview */ },
                                    navigationIcon = {
                                        IconButton(
                                            onClick = onNavigateBack, modifier = Modifier
                                                .clip(
                                                    CircleShape
                                                )
                                                .background(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = CircleShape
                                                )
                                        ) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    actions = {
                                        IconButton(
                                            onClick = { isFavorite = !isFavorite },
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = CircleShape
                                                )
                                        ) {
                                            Icon(
                                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                                tint = favoriteColor,
                                                modifier = Modifier.scale(scale)
                                            )
                                        }
                                        IconButton(onClick = { onShareRecipe() }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.to_forward),
                                                contentDescription = "Share recipe",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.surface,
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = Color.Transparent,
                                        scrolledContainerColor = Color.Transparent
                                    ),
                                    modifier = Modifier.statusBarsPadding()
                                )
                            }
                        } else {
                            LargeTopAppBar(
                                title = { Text(selectedRecipe.title) },
                                navigationIcon = {
                                    IconButton(onClick = onNavigateBack) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = { isFavorite = !isFavorite },
                                        modifier = Modifier
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                            tint = favoriteColor,
                                            modifier = Modifier.scale(scale)
                                        )
                                    }
                                    IconButton(onClick = { onShareRecipe() }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.to_forward),
                                            contentDescription = "Share recipe",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                },
                                scrollBehavior = scrollBehavior
                            )
                        }
                    }
                },
            ) { paddingValues ->

                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .floatingToolbarVerticalNestedScroll(
                            expanded = expanded,
                            onExpand = { expanded = true },
                            onCollapse = { expanded = false },
                        )
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (selectedIndex.intValue == 0 && canShowAppBar) {
                            Text(
                                text = selectedRecipe.title,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 16.dp,
                                    bottom = 8.dp
                                )
                            )
                        }
                        if (selectedIndex.intValue == 0 && !canShowAppBar) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data("${RecipeUtils.IMAGES_BASE_URL}${selectedRecipe.image}")
                                            .crossfade(true)
                                            .error(R.drawable.logo)
                                            .placeholder(R.drawable.logo)
                                            .build()
                                    ),
                                    contentDescription = selectedRecipe.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.5f)
                                                ),
                                                startY = 0f,
                                                endY = 250f
                                            )
                                        )
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .align(Alignment.BottomStart),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = selectedRecipe.title,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(
                                            bottom = 4.dp
                                        )
                                    )
                                    Text(
                                        text = "Chef: ${selectedRecipe.chefName ?: "Unknown Chef"}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White.copy(alpha = 0.8f)
                                        ),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                        }
                        PrimaryTabRow(
                            selectedTabIndex = pagerState.currentPage,
                            divider = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .shadow(
                                            elevation = 4.dp,
                                            spotColor = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.2f
                                            ),
                                            ambientColor = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.1f
                                            )
                                        )
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    text = { Text(text = title) }
                                )
                            }
                        }


                        HorizontalPager(state = pagerState, modifier = Modifier.padding(16.dp)) {
                            when (it) {
                                0 -> OverviewTabContent(recipe = selectedRecipe)
                                1 -> IngredientsTabContent(
                                    recipe = selectedRecipe,
                                    scrollBehavior = scrollBehavior
                                )

                                2 -> RecipeStepsContent(
                                    recipe = selectedRecipe,
                                    scrollBehavior = scrollBehavior
                                )
                            }
                        }
                    }

                    if (!canShowAppBar) {
                        VerticalFloatingToolbar(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(x = -ScreenOffset),
                            expanded = expanded,
                            content = {
                                FilledIconButton(onClick = { onShareRecipe() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.to_forward),
                                        contentDescription = "Share recipe",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .height(64.dp)
                                    )
                                }
                                IconButton(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = CircleShape
                                        ),
                                    onClick = { isFavorite = !isFavorite },
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                        tint = favoriteColor,
                                        modifier = Modifier.scale(scale)
                                    )
                                }

                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewTabContent(recipe: Recipe) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = recipe.description ?: "No description available.",
            style = MaterialTheme.typography.bodyLarge
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            InfoChip(icon = Icons.Filled.Restaurant, text = recipe.serves ?: "N/A")
            InfoChip(icon = Icons.Filled.Timer, text = recipe.preparationTime ?: "N/A")
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IngredientsTabContent(recipe: Recipe, scrollBehavior: TopAppBarScrollBehavior) {
    val options = listOf("Ingredients", "Description")
    val unCheckedIcons = listOf(Icons.Outlined.Restaurant, Icons.Outlined.RestaurantMenu)
    val checkedIcons = listOf(Icons.Filled.Restaurant, Icons.Filled.RestaurantMenu)
    var selectedIndex by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            val modifiers = listOf(Modifier.weight(1f), Modifier.weight(1f))
            options.forEachIndexed { index, label ->
                ToggleButton(
                    checked = selectedIndex == index,
                    onCheckedChange = { selectedIndex = index },
                    modifier = modifiers[index].semantics { role = Role.RadioButton },
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        }
                ) {
                    Icon(
                        if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                        contentDescription = "Localized description"
                    )
                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                    Text(label)
                }
            }
        }

        when (selectedIndex) {
            0 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    items(recipe.ingredients, key = { it.hashCode() }) { ingredient ->
                        Text(text = "• $ingredient", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            1 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    itemsIndexed(
                        items = recipe.ingredientsDesc,
                        key = { index, item -> "${index + item.hashCode()} desc" }) { index, ingredient ->
                        Text(text = "• $ingredient", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeStepsContent(recipe: Recipe, scrollBehavior: TopAppBarScrollBehavior) {
    if (recipe.method.isEmpty()) {
        Text("No recipe steps available.", style = MaterialTheme.typography.bodyLarge)
        return
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        itemsIndexed(recipe.method) { index, step ->
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(text = step, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}