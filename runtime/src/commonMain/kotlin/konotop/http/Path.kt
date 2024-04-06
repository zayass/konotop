package konotop.http


/**
 * Named replacement in a URL path segment.
 *
 *
 * Simple example:
 *
 * ```
 * @GET("/image/{id}")
 * suspend fun example(@Path("id") id: Int): HttpResponse
 * ```
 *
 * Calling with `foo.example(1)` yields `/image/1`.
 *
 *
 * Values are URL encoded by default. Disable with `encoded=true`.
 *
 * ```
 * @GET("/user/{name}")
 * suspend fun encoded(@Path("name") name: String): HttpResponse
 *
 * @GET("/user/{name}")
 * suspend fun notEncoded(@Path(value="name", encoded=true) name: String): HttpResponse
 * ```
 *
 * Calling `foo.encoded("John%Doe")` yields `/user/John%25Doe` whereas `foo.notEncoded("John%Doe")` yields `/user/John%Doe`.
 *
 *
 * Path parameters may not be `null`.
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path(
    val value: String,
    /**
     * Specifies whether the argument value to the annotated method parameter is already URL encoded.
     */
    val encoded: Boolean = false
)