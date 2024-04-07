package konotop.http

/** Make a HEAD request.  */
@MustBeDocumented
@HttpVerb
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class HEAD(
    val value: String = ""
)