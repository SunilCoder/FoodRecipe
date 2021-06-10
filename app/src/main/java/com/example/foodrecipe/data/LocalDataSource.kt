package com.example.foodrecipe.data

import androidx.lifecycle.LiveData
import com.example.foodrecipe.data.database.RecipesDao
import com.example.foodrecipe.data.database.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {
     fun readDatabase(): LiveData<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }
}