package com.github.stephenwanjala.brecipes.data.mappers

import com.github.stephenwanjala.brecipes.data.local.RecipeEntity
import com.github.stephenwanjala.brecipes.data.network.remote.RecipeDto

fun RecipeDto.toRecipe(): RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        image = image,
        description = description,
        cuisine = cuisine,
        sourceUrl = sourceUrl,
        chefName = chefName,
        preparationTime = preparationTime,
        cookingTime = cookingTime,
        serves = serves,
        ingredientsDesc = ingredientsDesc,
        ingredients = ingredients,
        method = method,
    )
}


fun RecipeEntity.toRecipeDto(): RecipeDto {
    return RecipeDto(
        id = id,
        title = title,
        image = image,
        description = description,
        cuisine = cuisine,
        sourceUrl = sourceUrl,
        chefName = chefName,
        preparationTime = preparationTime,
        cookingTime = cookingTime,
        serves = serves,
        ingredientsDesc = ingredientsDesc,
        ingredients = ingredients,
        method = method,
    )

}