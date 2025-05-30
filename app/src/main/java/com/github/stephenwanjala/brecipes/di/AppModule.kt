package com.github.stephenwanjala.brecipes.di

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.github.stephenwanjala.brecipes.data.local.RecipeDatabase
import com.github.stephenwanjala.brecipes.data.local.RecipeEntity
import com.github.stephenwanjala.brecipes.data.network.HttpClientFactory
import com.github.stephenwanjala.brecipes.data.network.remote.RecipesApi
import com.github.stephenwanjala.brecipes.data.network.remote.RecipesRemoteMediator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClientFactory.create(
            CIO.create()
        )
    }

    @Provides
    @Singleton
    fun provideRecipesDatabase(app: Application): RecipeDatabase {
        return Room.databaseBuilder(context = app, RecipeDatabase::class.java, "recipes.db")
            .fallbackToDestructiveMigration(false)
            .build()


    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun provideRecipePager(
        recipesApi: RecipesApi,
        recipeDatabase: RecipeDatabase
    ): Pager<Int, RecipeEntity> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = RecipesRemoteMediator(
                recipesApi = recipesApi,
                recipesDb = recipeDatabase
            ),
            pagingSourceFactory = {
                recipeDatabase.recipeDao.pagingSource()
            }
        )
    }
}