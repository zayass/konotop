package konotop.compiler.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile

fun KSClassDeclaration.isInterface() = classKind == ClassKind.INTERFACE

fun KSClassDeclaration.isServiceInterface() =
    isInterface() && getAllFunctions().any { it.isServiceMethod() }

fun KSFunctionDeclaration.isServiceMethod() = hasMetaAnnotation(Annotations.HttpVerb)

fun KSClassDeclaration.getServiceMethods() =
    getAllFunctions().filter { it.isServiceMethod() }

fun KSAnnotated.getAnnotation(annotationClassName: ClassName) = annotations.firstOrNull {
    it.match(annotationClassName)
}

fun KSAnnotated.getAnnotationByMeta(annotationClassName: ClassName) = annotations.firstOrNull { annotation ->
    val resolved = annotation.annotationType.resolve().declaration
    resolved.hasAnnotation(annotationClassName)
}

fun KSAnnotated.hasAnnotation(annotationClassName: ClassName) = annotations.any {
    it.match(annotationClassName)
}

fun KSAnnotated.hasMetaAnnotation(annotationClassName: ClassName) = annotations.any { annotation ->
    val resolved = annotation.annotationType.resolve().declaration
    resolved.hasAnnotation(annotationClassName)
}

private fun KSAnnotation.match(className: ClassName): Boolean {
    return shortName.getShortName() == className.simpleName &&
            annotationType.resolve().declaration.qualifiedName?.asString() == className.canonicalName
}

fun TypeSpec.Builder.addOriginatingKSClass(ksClassDeclaration: KSClassDeclaration): TypeSpec.Builder = apply {
    val ksFile = ksClassDeclaration.containingFile
    if (ksFile != null) {
        addOriginatingKSFile(ksFile)
    }
}

fun Resolver.getServiceInterfaces(): Sequence<KSClassDeclaration> {
    suspend fun SequenceScope<KSClassDeclaration>.visit(declarations: Sequence<KSDeclaration>) {
        for (declaration in declarations) {
            if (declaration is KSClassDeclaration) {
                if (declaration.isServiceInterface()) {
                    yield(declaration)
                }

                visit(declaration.declarations)
            }
        }
    }
    return sequence {
        for (file in getNewFiles()) {
            visit(file.declarations)
        }
    }
}