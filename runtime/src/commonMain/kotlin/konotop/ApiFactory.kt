package konotop

import io.ktor.client.*

interface ApiFactory<T> : (HttpClient) -> T {
    fun create(httpClient: HttpClient): T

    override operator fun invoke(httpClient: HttpClient) = create(httpClient)
}
