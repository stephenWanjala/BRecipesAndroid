package com.github.stephenwanjala.brecipes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.json.Json

@Entity
@TypeConverters(ListStringTypeConverter::class)
data class RecipeEntity(
    @PrimaryKey
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


class ListStringTypeConverter {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.let {
            Json.decodeFromString<List<String>>(it)
        } ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return Json.encodeToString(list ?: emptyList())
    }
}


