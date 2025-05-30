package com.github.stephenwanjala.brecipes.data.network.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.github.stephenwanjala.brecipes.data.local.PaginationEntity
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

    private val paginationId = "recipes_pagination"

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RecipeEntity>
    ): MediatorResult = withContext(Dispatchers.IO) {
        try {
            val paginationDao = recipesDb.paginationDao
            val currentPagination = paginationDao.getPagination(paginationId)

            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> {
                    return@withContext MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    if (currentPagination == null) {
                        return@withContext MediatorResult.Success(endOfPaginationReached = true)
                    }
                    if (currentPagination.page >= currentPagination.totalPages) {
                        return@withContext MediatorResult.Success(endOfPaginationReached = true)
                    }
                    currentPagination.page + 1
                }
            }

            val response = recipesApi.getRecipes(page = page, limit = state.config.pageSize)

            when (response) {
                is Result.Error -> {
                    return@withContext MediatorResult.Error(Throwable(response.error.toString()))
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

                    val pagination = response.data.pagination

                    recipesDb.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            recipesDb.recipeDao.clearAll()
                            paginationDao.clearAll()
                        }
                        recipesDb.recipeDao.upsertAll(recipeEntities)
                        paginationDao.upsertPagination(
                            PaginationEntity(
                                id = paginationId,
                                page = pagination.page,
                                limit = pagination.limit,
                                total = pagination.total,
                                totalPages = pagination.totalPages
                            )
                        )
                    }

                    val endOfPaginationReached = pagination.page >= pagination.totalPages

                    return@withContext MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
            }
        } catch (e: Exception) {
            return@withContext MediatorResult.Error(e)
        }
    }
}
