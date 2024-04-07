package konotop.integration

import konotop.http.GET
import konotop.http.Path

interface RecipesApi {
    @GET("/recipes/{id}")
    suspend fun getRecipe(@Path("id") id: Int): Recipe
}
