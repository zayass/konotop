package konotop.integration

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: Int? = null,
    val todo: String,
    val completed: Boolean,
    val userId: Int,
    val isDeleted: Boolean = false,
    val deletedOn: String? = null,
)