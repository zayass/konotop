package konotop.http

/** Make a DELETE request.  */
@MustBeDocumented
@HttpVerb
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DELETE(
    val value: String = ""
)