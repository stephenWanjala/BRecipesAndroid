package com.github.stephenwanjala.brecipes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.github.stephenwanjala.brecipes.data.local.RecipeEntity
import com.github.stephenwanjala.brecipes.data.mappers.toRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(pager: Pager<Int, RecipeEntity>) : ViewModel(){
    val recipePagingFlow = pager.flow.map { pagingData->
        pagingData.map { it.toRecipe() }
    }.cachedIn(viewModelScope)
}