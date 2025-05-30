package com.github.stephenwanjala.brecipes.data.network.remote

import com.github.stephenwanjala.brecipes.BuildConfig
import com.github.stephenwanjala.brecipes.data.network.NetworkError
import com.github.stephenwanjala.brecipes.data.network.Result
import com.github.stephenwanjala.brecipes.data.network.constructUrl
import com.github.stephenwanjala.brecipes.data.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.request
import io.ktor.http.headers
import io.ktor.http.parameters
import io.ktor.http.parametersOf
import javax.inject.Inject

class RecipesApi @Inject constructor(private val client: HttpClient) {
    suspend fun getRecipes(
        page: Int=1,
        limit: Int = 20,
        cuisine: String? = null
    ): Result<RecipeResponse, NetworkError> {

        val url = constructUrl("api/recipes")
        val result = safeCall<RecipeResponse> {
            client.apply {
                headers {
                    append("x-api-key",BuildConfig.API_KEY)
                }

            }.get(urlString = url){
                parameter("page",page)
                parameter("limit",limit)
                cuisine?.let { parameter("cuisine",it) }
            }
        }
        return when (result) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.error)
        }
    }
}