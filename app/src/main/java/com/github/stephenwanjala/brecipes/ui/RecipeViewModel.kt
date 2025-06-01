package com.github.stephenwanjala.brecipes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.github.stephenwanjala.brecipes.data.local.RecipeDatabase
import com.github.stephenwanjala.brecipes.data.local.RecipeEntity
import com.github.stephenwanjala.brecipes.data.mappers.toRecipe
import com.github.stephenwanjala.brecipes.domain.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    pager: Pager<Int, RecipeEntity>,
    private val recipesDb: RecipeDatabase
) : ViewModel() {
    val recipePagingFlow = pager.flow.map { pagingData ->
        pagingData.map { it.toRecipe() }
    }.cachedIn(viewModelScope)
    private val _state = MutableStateFlow(RecipesState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecipesState())

    fun onAction(action: RecipeAction) {
        when (action) {
            is RecipeAction.OnSelectRecipe -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    val recipe = recipesDb.recipeDao.getRecipeById(action.recipeId)
                    _state.update {
                        it.copy(
                            selectedRecipe = recipe?.toRecipe(),
                            isLoading = false
                        )
                    }
                }
            }

            RecipeAction.OnNavigateUp -> _state.update { it.copy(selectedRecipe = null, selectedTabIndex = 0) }
        }
    }

    fun setSelectedTabIndex(index: Int) {
        _state.update { it.copy(selectedTabIndex = index) }
    }
}


data class RecipesState(
    val isLoading: Boolean = false,
    val selectedRecipe: Recipe? = null,
    val selectedTabIndex: Int = 0
)

sealed interface RecipeAction {
    data class OnSelectRecipe(val recipeId: Int) : RecipeAction
    data object OnNavigateUp : RecipeAction
}