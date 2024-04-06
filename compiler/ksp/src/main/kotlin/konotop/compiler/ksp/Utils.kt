package konotop.compiler.ksp

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName

fun KSClassDeclaration.isInterface() = classKind == ClassKind.INTERFACE

fun KSClassDeclaration.getCompanion() =
    declarations.find { it is KSClassDeclaration && it.isCompanionObject } as? KSClassDeclaration

fun KSClassDeclaration.isServiceInterface() =
    isInterface() && getAllFunctions().any { it.isServiceMethod() }

fun KSFunctionDeclaration.isServiceMethod() = hasAnnotation(GET)

fun KSClassDeclaration.getServiceMethods() =
    getAllFunctions().filter { it.isServiceMethod() }

fun KSAnnotated.getAnnotation(annotationClassName: ClassName) = annotations.firstOrNull {
    it.match(annotationClassName)
}

fun KSAnnotated.hasAnnotation(annotationClassName: ClassName) = annotations.any {
    it.match(annotationClassName)
}

private fun KSAnnotation.match(className: ClassName): Boolean {
    return shortName.getShortName() == className.simpleName &&
            annotationType.resolve().declaration.qualifiedName?.asString() == className.canonicalName
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