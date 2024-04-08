package konotop.integration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Todos(
    val todos: List<Todo>,
    val total: Int,
    @SerialName("skip")
    val offset: Int,
    val limit: Int
)