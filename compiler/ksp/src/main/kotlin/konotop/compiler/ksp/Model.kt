package konotop.compiler.ksp

import com.google.devtools.ksp.symbol.*

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

    val name
        get() = declaration.simpleName.getShortName()

    fun isSuspend() =
        declaration.modifiers.contains(Modifier.SUSPEND)

    fun bodyArgument() = arguments
        .filterIsInstance<Arg.BodyArgument>()
        .firstOrNull()

    fun pathArguments() = arguments
        .filterIsInstance<Arg.PathArgument>()

    fun queryArguments() = arguments
        .filterIsInstance<Arg.QueryArgument>()
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

abstract class Arg : HasDeclaration<KSValueParameter> {
    abstract override val declaration: KSValueParameter

    val kotlinArgumentName by lazy {
        declaration.name!!.asString()
    }

    val isNullable by lazy {
        declaration.type.resolve().isMarkedNullable
    }

    data class BodyArgument(
        override val declaration: KSValueParameter,
    ): Arg()

    data class PathArgument(
        override val declaration: KSValueParameter,
        val pathArgumentName: String,
    ): Arg()

    data class QueryArgument(
        override val declaration: KSValueParameter,
        val queryArgumentName: String,
    ): Arg()

    data class Unknown(
        override val declaration: KSValueParameter,
    ): Arg()
}