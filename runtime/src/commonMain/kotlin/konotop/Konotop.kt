package konotop

import io.ktor.client.*
import io.ktor.utils.io.core.*
import kotlin.reflect.KClass

class Konotop private constructor(
    private val client: HttpClient
): Closeable by client {

    inline fun <reified T: Any> create(): T =
        create(T::class)

    fun <T: Any> create(service: KClass<T>): T {
        val factory = service.lookupFactory()
            ?: throw RuntimeException("Unknown service: ${service.qualifiedName}")

        @Suppress("UNCHECKED_CAST")
        return factory(client) as T
    }

    class Builder {
        private var client: HttpClient? = null

        fun client(client: HttpClient): Builder {
            this.client = client
            return this
        }

        fun build(): Konotop {
            return Konotop(client!!)
        }
    }
}
