import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.*
import konotop.Konotop
import konotop.integration.Recipe
import konotop.integration.RecipesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test


@Resource("/recipes")
class Recipes {
    @Resource("{id}")
    class ById(val parent: Recipes = Recipes(), val id: Long)
}

class FibiTest {

    @Test
    fun experiment() = runTest {
        val httpClient = HttpClient {
            install(Resources)
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            defaultRequest {
                url {
                    host = "dummyjson.com"
                    protocol = URLProtocol.HTTPS
                }
            }
        }

        val response = httpClient
            .get(Recipes.ById(id = 1))
            .body<Recipe>()

        println(response)
    }

    @Test
    fun experiment1() = runTest {
        val httpClient = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            defaultRequest {
                url {
                    host = "dummyjson.com"
                    protocol = URLProtocol.HTTPS
                }
            }
        }

        val konotop = Konotop
            .Builder()
            .client(httpClient)
            .build()

        val api = konotop.create<RecipesApi>()
        val response = api.getRecipe(1)
        println(response)
    }
}