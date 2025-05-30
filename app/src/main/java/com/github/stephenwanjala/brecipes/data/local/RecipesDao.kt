package com.github.stephenwanjala.brecipes.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RecipesDao {

    @Upsert
    suspend fun upsertAll(recipes: List<RecipeEntity>)

    @Query("SELECT * FROM recipeentity")
    suspend fun pagingSource(): PagingSource<String, RecipeEntity>

    @Query("DELETE FROM recipeentity")
    suspend fun clearAll()


}