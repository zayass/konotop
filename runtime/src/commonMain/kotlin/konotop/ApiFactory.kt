package konotop

import io.ktor.client.*

interface ApiFactory<T> {
    fun create(httpClient: HttpClient): T
}
