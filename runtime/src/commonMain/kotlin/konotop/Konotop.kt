package konotop

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass


class Konotop private constructor(
    private val client: HttpClient
): Closeable by client {

    inline fun <reified T: Any> create(): T =
        create(T::class)

    fun <T: Any> create(service: KClass<T>): T {
        val factory = service.lookupFactory()
            ?: throw RuntimeException("Unknown service: ${service.qualifiedName}")

        return factory(client)
    }

    class Builder {
        private var client: HttpClient? = null
        private var baseUrl: Url? = null

        fun baseUrl(baseUrl: String) = apply {
            baseUrl(Url(baseUrl))
        }

        fun baseUrl(baseUrl: Url) = apply {
            val pathSegments: List<String> = baseUrl.pathSegments
            if ("" != pathSegments[pathSegments.size - 1]) {
                throw IllegalArgumentException("baseUrl must end in /: $baseUrl")
            }
            this.baseUrl = baseUrl
        }

        fun client(client: HttpClient): Builder {
            this.client = client
            return this
        }

        fun build(): Konotop {
            val client = client ?: defaultClient()
            return Konotop(client.applyConfig())
        }

        private fun HttpClient.applyConfig() = config {
            val baseUrl = baseUrl
            if (baseUrl != null) {
                applyBaseUrl(baseUrl)
            }
        }

        private fun HttpClientConfig<*>.applyBaseUrl(baseUrl: Url) {
            defaultRequest {
                url(baseUrl.toString())
            }
        }
    }

    companion object {
        fun defaultClient() = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}
