package konotop.integration

import konotop.http.*

interface TodoApi {
    @GET("/todos")
    suspend fun getTodos(
        @Query("limit") limit: Int,
        @Query("skip") offset: Int? = null
    ): Todos

    @GET("/todos/{id}")
    suspend fun getTodo(@Path("id") id: Int): Todo

    @DELETE("/todos/{id}")
    suspend fun deleteTodo(@Path("id") id: Int): Todo

    @POST("/todos/add")
    suspend fun createTodo(@Body todo: Todo): Todo

    @PUT("/todos/{id}")
    suspend fun updateTodo(@Path("id") id: Int, @Body todo: Todo): Todo
}
