package com.github.stephenwanjala.brecipes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecipeEntity::class, PaginationEntity::class], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase(){
    abstract val recipeDao: RecipesDao
    abstract val paginationDao: PaginationDao

}