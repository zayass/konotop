package konotop.integration

import io.ktor.client.statement.*
import konotop.http.*

interface RecipesApi {
    @GET("/recipes/{id}")
    suspend fun getRecipe(@Path("id") id: Int): Recipe

    @DELETE("/recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int): HttpResponse

    @POST("/recipes")
    suspend fun createRecipe(recipe: Recipe): HttpResponse

    @PUT("/recipes")
    suspend fun updateRecipe(recipe: Recipe): HttpResponse
}
