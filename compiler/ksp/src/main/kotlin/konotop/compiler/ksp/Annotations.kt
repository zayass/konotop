package konotop.compiler.ksp

import com.squareup.kotlinpoet.ClassName

private const val ANNOTATION_PACKAGE_NAME = "konotop.http"

val HttpVerb = ClassName(ANNOTATION_PACKAGE_NAME, "HttpVerb")


val Path = ClassName(ANNOTATION_PACKAGE_NAME, "Path")
val Body = ClassName(ANNOTATION_PACKAGE_NAME, "Body")

