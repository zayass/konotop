package konotop.compiler.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter

internal fun KSClassDeclaration.toModel(): Service {
    val methods = getServiceMethods().map { it.toModel() }

    val serviceModel = Service(
        declaration = this,
        methods = methods.toList()
    )
    return serviceModel
}

internal fun KSFunctionDeclaration.toModel(): Method {
    val annotation = getAnnotationByMeta(HttpVerb)
    val path = annotation?.arguments?.firstOrNull()?.value as? String
        ?: error("$annotation should have value")

    val arguments = parameters.map { it.toModel() }

    val httpMethod = HttpMethod.valueOf(
        annotation.shortName.asString()
    )

    return Method(
        declaration = this,
        path = path,
        httpMethod = httpMethod,
        arguments = arguments
    )
}

internal fun KSValueParameter.toModel(): Arg {
    val bodyAnnotation = getAnnotation(Body)
    val pathAnnotation = getAnnotation(Path)
    val queryAnnotation = getAnnotation(Query)

    return when {
        bodyAnnotation != null -> {
            Arg.BodyArgument(declaration = this)
        }
        pathAnnotation != null -> {
            val name = pathAnnotation.arguments.firstOrNull()?.value as? String
                ?: error("$pathAnnotation should have value")

            Arg.PathArgument(declaration = this, pathArgumentName = name)
        }
        queryAnnotation != null -> {
            val name = queryAnnotation.arguments.firstOrNull()?.value as? String
                ?: error("$queryAnnotation should have value")

            Arg.QueryArgument(declaration = this, queryArgumentName = name)
        }
        else -> {
            Arg.Unknown(declaration = this)
        }
    }
}