package konotop.http


/** Make a GET request.  */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class GET(
    val value: String = ""
)