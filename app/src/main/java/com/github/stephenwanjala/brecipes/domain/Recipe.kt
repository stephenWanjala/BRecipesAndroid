package com.github.stephenwanjala.brecipes.domain

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
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
) : Parcelable {
    @IgnoredOnParcel
    val isFavorite:Boolean = false
}
