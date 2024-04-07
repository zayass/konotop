package konotop.compiler.ksp

import com.squareup.kotlinpoet.ClassName

private const val ANNOTATION_PACKAGE_NAME = "konotop.http"

val HttpVerb = ClassName(ANNOTATION_PACKAGE_NAME, "HttpVerb")
val GET = ClassName(ANNOTATION_PACKAGE_NAME, "GET")
val POST = ClassName(ANNOTATION_PACKAGE_NAME, "POST")


val Path = ClassName(ANNOTATION_PACKAGE_NAME, "Path")
