package com.github.stephenwanjala.brecipes.data.network.remote

import com.github.stephenwanjala.brecipes.data.network.NetworkError
import com.github.stephenwanjala.brecipes.data.network.Result
import com.github.stephenwanjala.brecipes.data.network.constructUrl
import com.github.stephenwanjala.brecipes.data.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class RecipesApi @Inject constructor(private val client: HttpClient) {
    suspend fun getRecipes(
        page: Int,
        limit: Int = 20,
        cuisine: String? = null
    ): Result<RecipeResponse, NetworkError> {

            val url = constructUrl("api/recipes")
            val result = safeCall<RecipeResponse> {
                client.get(urlString = url) {
                    parameter("page", page)
                    parameter("limit", limit)
                    parameter("cuisine", cuisine)
                }
            }
          return  when (result) {
                is Result.Success -> Result.Success(result.data)
                is Result.Error -> Result.Error(result.error)
            }
    }
}