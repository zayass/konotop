import konotop.Konotop
import konotop.integration.RecipesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTest {
    private val konotop = Konotop
        .Builder()
        .baseUrl("https://dummyjson.com/")
        .build()

    @Test
    fun testGetAsyncWithPathArgument() = runTest {
        val api = konotop.create<RecipesApi>()
        val response = api.getRecipe(1)

        assertEquals(1, response.id)
    }
}