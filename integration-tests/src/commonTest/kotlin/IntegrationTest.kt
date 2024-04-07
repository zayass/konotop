import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import konotop.Konotop
import konotop.integration.RecipesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTest {
    private val httpClient = HttpClient {
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

    private val konotop = Konotop
        .Builder()
        .client(httpClient)
        .build()

    @Test
    fun testGetAsyncWithPathArgument() = runTest {
        val api = konotop.create<RecipesApi>()
        val response = api.getRecipe(1)

        assertEquals(1, response.id)
    }
}