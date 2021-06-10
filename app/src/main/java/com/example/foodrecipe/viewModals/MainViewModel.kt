package com.example.foodrecipe.viewModals

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodrecipe.data.Repository
import com.example.foodrecipe.data.database.RecipesEntity
import com.example.foodrecipe.model.FoodRecipe
import com.example.foodrecipe.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.Exception

/*
class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: MyApplication
) : AndroidViewModel(application) {
    var recpiesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recpiesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                //val response= repository.remote.getRecipes(queries)
                val response = repository.remote.getRecipes(queries)
                // val recipesResponse= repository.remote.getRecipes(queries)
                recpiesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                recpiesResponse.value = NetworkResult.Error(null, "Recipes not found")
            }
        } else {
            recpiesResponse.value = NetworkResult.Error(null, "No Internet Connection.")
        }
    }

    private fun handleFoodRecipesResponse(response: retrofit2.Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(null, "TimeOut")
            }
            response.code() == 402 -> {
                return NetworkResult.Error(null, "API key Limited")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error(null, "Recipes not found.")
            }
            response.isSuccessful() -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(null, response.message())
            }

        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}*/
class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {
    /*Room*/
    //val readRecipes:LiveData<List<RecipesEntity>> = repository.local.readDatabase().asLiveData()
    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readDatabase()
    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }


    /*Retrofit*/

    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)
                val foodRecipe = recipesResponse.value!!.data
                if (foodRecipe != null) {
                    offlineCacheRecipe(foodRecipe)

                }
            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error(null, "Recipes not found.")
            }
        } else {
            recipesResponse.value = NetworkResult.Error(null, "No Internet Connection.")
        }
    }

    private fun offlineCacheRecipe(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)

    }

    private fun handleFoodRecipesResponse(response: retrofit2.Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(null, "Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error(null, "API Key Limited.")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error(null, "Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(null, response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}

