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
     fun pagingSource(): PagingSource<Int, RecipeEntity>

    @Query("DELETE FROM recipeentity")
    suspend fun clearAll()


}


@Dao
interface PaginationDao{
    @Upsert
    suspend fun upsertPagination(pagination: PaginationEntity)

    @Query("SELECT * FROM paginationentity WHERE id = :id")
    suspend fun getPagination(id: String): PaginationEntity?

    @Query("DELETE FROM paginationentity")
    suspend fun clearAll()

    @Query("DELETE FROM paginationentity WHERE id = :id")
    suspend fun deletePagination(id: String)

}