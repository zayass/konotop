package konotop.compiler.ksp

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.asClassName
import io.ktor.http.*

object Annotations {
    private const val ANNOTATION_PACKAGE_NAME = "konotop.http"

    val HttpVerb = ClassName(ANNOTATION_PACKAGE_NAME, "HttpVerb")

    val Body = konotop.http.Body::class.asClassName()
    val Path = konotop.http.Path::class.asClassName()
    val Query = konotop.http.Query::class.asClassName()
}

object KtorMembers {
    private const val KTOR_HTTP_PACKAGE = "io.ktor.http"
    private const val KTOR_CLIENT_CALL_PACKAGE = "io.ktor.client.call"
    private const val KTOR_CLIENT_REQUEST_PACKAGE = "io.ktor.client.request"

    val setBody = MemberName(KTOR_CLIENT_REQUEST_PACKAGE, "setBody")
    val getBody = MemberName(KTOR_CLIENT_CALL_PACKAGE, "body")
    val setContentType = MemberName(KTOR_HTTP_PACKAGE, "contentType")

    val applicationJson = ContentType.Application::class.asClassName()
        .member("Json")

    fun httpVerb(verb: HttpMethod) = MemberName(KTOR_CLIENT_REQUEST_PACKAGE, verb.name.lowercase())
}
