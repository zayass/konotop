package konotop.compiler.ksp

import com.squareup.kotlinpoet.ClassName

private const val ANNOTATION_PACKAGE_NAME = "konotop.http"
val GET = ClassName(ANNOTATION_PACKAGE_NAME, "GET")
val Path = ClassName(ANNOTATION_PACKAGE_NAME, "Path")
