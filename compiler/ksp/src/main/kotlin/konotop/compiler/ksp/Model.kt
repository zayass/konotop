package konotop.compiler.ksp

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter

interface HasDeclaration<T : KSAnnotated> {
    val declaration: T
}

data class Service(
    override val declaration: KSClassDeclaration,
    val methods: List<Method>
) : HasDeclaration<KSClassDeclaration> {
    val packageName = declaration.packageName.asString()
    val factoryName = "${declaration.simpleName.asString()}Factory"
    val implementationName = "${declaration.simpleName.asString()}Impl"
}

data class Method(
    override val declaration: KSFunctionDeclaration,
    val path: String,
    val httpMethod: HttpMethod = HttpMethod.GET,
    val arguments: List<Arg> = emptyList()
) : HasDeclaration<KSFunctionDeclaration> {
    fun hasBody() = arguments.any { it is Arg.BodyArgument }

    fun bodyArgument() = arguments
        .filterIsInstance<Arg.BodyArgument>()
        .firstOrNull()
}

enum class HttpMethod {
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    PATCH,
    POST,
    PUT
}

sealed interface Arg : HasDeclaration<KSValueParameter> {
    override val declaration: KSValueParameter

    data class BodyArgument(
        override val declaration: KSValueParameter,
    ): Arg

    data class PathArgument(
        override val declaration: KSValueParameter,
        val name: String,
    ): Arg

    data class Unknown(
        override val declaration: KSValueParameter,
    ): Arg
}