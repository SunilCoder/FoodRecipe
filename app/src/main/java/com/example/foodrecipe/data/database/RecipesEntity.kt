package com.example.foodrecipe.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodrecipe.model.FoodRecipe
import com.example.foodrecipe.util.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    foodRecipe: FoodRecipe
) {
    val foodRecipe = foodRecipe

    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}