package konotop.integration

import konotop.AssociatedFactory
import konotop.http.GET
import konotop.http.Path


@AssociatedFactory(RecipesApiFactory::class)
interface RecipesApi {
    @GET("/recipes/{id}")
    suspend fun getRecipe(@Path("id") id: Int): Recipe
}
