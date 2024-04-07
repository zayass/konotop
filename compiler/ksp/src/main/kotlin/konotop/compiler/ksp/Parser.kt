package konotop.compiler.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter

internal fun KSClassDeclaration.toModel(): Service {
    val methods = getServiceMethods().map { it.toModel() }

    val serviceModel = Service(
        origin = this,
        methods = methods.toList()
    )
    return serviceModel
}

internal fun KSFunctionDeclaration.toModel(): Method {
    val annotation = getAnnotation(GET)
    val path = annotation?.arguments?.firstOrNull()?.value as? String
        ?: error("$annotation should have value")

    val arguments = parameters.map { it.toModel() }

    return Method(
        origin = this,
        path = path,
        arguments = arguments
    )
}

internal fun KSValueParameter.toModel(): Arg {
    val pathAnnotation = getAnnotation(Path)

    return if (pathAnnotation != null) {
        val name = pathAnnotation.arguments.firstOrNull()?.value as? String
            ?: error("$pathAnnotation should have value")

        Arg.PathArgument(origin = this, name = name)
    } else {
        Arg.Unknown(origin = this)
    }
}