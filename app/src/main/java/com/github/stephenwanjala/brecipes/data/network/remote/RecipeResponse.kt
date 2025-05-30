package com.github.stephenwanjala.brecipes.data.network.remote

import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    val recipes: List<RecipeDto>,
    val pagination: PaginationDto
)

@Serializable
data class RecipeDto(
    val id: Int,
    val title: String,
    val description: String?,
    val cuisine: String,
    val image: String?,
    val sourceUrl: String?,
    val chefName: String?,
    val preparationTime: String?,
    val cookingTime: String?,
    val serves: String?,
    val ingredientsDesc: List<String>,
    val ingredients: List<String>,
    val method: List<String>,
)

@Serializable
data class PaginationDto(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)
