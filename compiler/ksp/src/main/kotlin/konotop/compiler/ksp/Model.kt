package konotop.compiler.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter

data class Service(
    val origin: KSClassDeclaration,
    val methods: List<Method>
) {
    val packageName = origin.packageName.asString()
    val factoryName = "${origin.simpleName.asString()}Factory"
    val implementationName = "${origin.simpleName.asString()}Impl"
}

data class Method(
    val origin: KSFunctionDeclaration,
    val path: String,
    val httpMethod: HttpMethod = HttpMethod.GET,
    val arguments: List<Arg> = emptyList()
)

enum class HttpMethod {
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    PATCH,
    POST,
    PUT
}

sealed interface Arg {
    val origin: KSValueParameter

    data class PathArgument(
        override val origin: KSValueParameter,
        val name: String,
    ): Arg

    data class Unknown(
        override val origin: KSValueParameter,
    ): Arg
}