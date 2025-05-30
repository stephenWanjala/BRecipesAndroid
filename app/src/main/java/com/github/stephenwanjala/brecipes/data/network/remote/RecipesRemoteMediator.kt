package com.github.stephenwanjala.brecipes.data.network.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.github.stephenwanjala.brecipes.data.local.RecipeDatabase
import com.github.stephenwanjala.brecipes.data.local.RecipeEntity
import com.github.stephenwanjala.brecipes.data.network.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class RecipesRemoteMediator(
    private val recipesApi: RecipesApi,
    private val recipesDb: RecipeDatabase,
) : RemoteMediator<Int, RecipeEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RecipeEntity>
    ): MediatorResult = withContext(Dispatchers.IO) {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return@withContext MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        return@withContext MediatorResult.Success(endOfPaginationReached = true)
                    }
                    (state.pages.size + 1)
                }
            }
            println("page: $page")
            println("pageSize: ${state.config.pageSize}")

            val response = recipesApi.getRecipes(page = page, limit = state.config.pageSize)
            when (response) {
                is Result.Error -> {
                    MediatorResult.Error(Throwable(response.error.toString()))
                }

                is Result.Success -> {
                    val recipeDtos = response.data.recipes
                    val recipeEntities = recipeDtos.map { dto ->
                        RecipeEntity(
                            id = dto.id,
                            title = dto.title,
                            description = dto.description,
                            cuisine = dto.cuisine,
                            image = dto.image,
                            sourceUrl = dto.sourceUrl,
                            chefName = dto.chefName,
                            preparationTime = dto.preparationTime,
                            cookingTime = dto.cookingTime,
                            serves = dto.serves,
                            ingredientsDesc = dto.ingredientsDesc,
                            ingredients = dto.ingredients,
                            method = dto.method
                        )
                    }

                    val database = recipesDb
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            database.recipeDao.clearAll()
                        }
                        database.recipeDao.upsertAll(recipeEntities)
                    }
                    MediatorResult.Success(
                        endOfPaginationReached = page >= response.data.pagination.page
                    )
                }
            }

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
