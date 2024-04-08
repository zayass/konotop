package konotop.http

@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Query(
    /** The query parameter name. */
    val value: String,
    /**
     * Specifies whether the parameter [value] and value are already URL encoded.
     */
    val encoded: Boolean = false
)