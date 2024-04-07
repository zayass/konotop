package konotop.http

/** Make a PUT request.  */
@MustBeDocumented
@HttpVerb
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PUT(
    val value: String = ""
)