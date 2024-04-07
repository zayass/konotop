package konotop.http

/** Make a OPTIONS request.  */
@MustBeDocumented
@HttpVerb
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OPTIONS(
    val value: String = ""
)