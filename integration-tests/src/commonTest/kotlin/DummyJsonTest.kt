
import konotop.Konotop
import konotop.integration.Todo
import konotop.integration.TodoApi
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DummyJsonTest {
    private val konotop = Konotop
        .Builder()
        .baseUrl("https://dummyjson.com/")
        .build()

    private val api = konotop.create<TodoApi>()

    @Test
    fun testGetAsyncWithQueryArgument() = runTest {
        val responseWithLimit = api.getTodos(10)
        val responseWithLimitAndOffset = api.getTodos(10, 30)

        assertEquals(10, responseWithLimit.limit)
        assertEquals(0, responseWithLimit.offset)

        assertEquals(10, responseWithLimitAndOffset.limit)
        assertEquals(30, responseWithLimitAndOffset.offset)
    }

    @Test
    fun testGetAsyncWithPathArgument() = runTest {
        val response = api.getTodo(1)

        assertEquals(1, response.id)
    }

    @Test
    fun testPostAsyncWithBody() = runTest {
        val todo = randomTodo()

        val response = api.createTodo(todo)

        assertNotNull(response.id)
        assertEquals(response.todo, todo.todo)
        assertEquals(response.completed, todo.completed)
        assertEquals(response.userId, todo.userId)
    }

    @Test
    fun testPutAsyncWithBody() = runTest {
        val todo = randomTodo()

        val response = api.updateTodo(1, todo)

        assertEquals(1, response.id)
        assertEquals(response.todo, todo.todo)
        assertEquals(response.completed, todo.completed)
        assertEquals(response.userId, todo.userId)
    }

    @Test
    fun testDeleteAsyncWithPathArgument() = runTest {
        val response = api.deleteTodo(1)

        assertEquals(1, response.id)
        assertEquals(true, response.isDeleted)
        assertTrue(response.isDeleted)
        assertNotNull(response.deletedOn)
    }

    companion object {
        private fun randomTodo() = Todo(
            todo = randomString(),
            completed = Random.nextBoolean(),
            userId = Random.nextInt(1, 100),
        )

        @OptIn(ExperimentalStdlibApi::class)
        private fun randomString() = Random.nextBytes(16).toHexString()
    }
}