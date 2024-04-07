package konotop.http

/** Make a PATCH request.  */
@MustBeDocumented
@HttpVerb
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PATCH(
    val value: String = ""
)