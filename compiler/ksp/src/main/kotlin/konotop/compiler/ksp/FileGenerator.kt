package konotop.compiler.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.ksp.toClassName
import io.ktor.client.*
import konotop.ApiFactory

class FileGenerator(val logger: KSPLogger) {

    fun generate(service: Service) = service.generateFile()

    private fun Service.generateFile() = FileSpec
        .builder(packageName, implementationName)
        .apply {
            addImport(
                packageName = "io.ktor.http",
                names = listOf("contentType", "ContentType")
            )
            addImport(
                packageName = "io.ktor.client.call",
                names = listOf("body")
            )
            addImport(
                packageName = "io.ktor.client.request",
                names = listOf("delete", "get", "head", "options", "patch", "post", "put", "setBody")
            )
            addType(generateType())
            addType(generateFactoryObject())
        }
        .build()

    private fun Service.generateFactoryObject() = TypeSpec
        .objectBuilder(factoryName)
        .addModifiers(KModifier.INTERNAL)
        .addSuperinterface(ApiFactory::class.asClassName().plusParameter(declaration.toClassName()))
        .addFunction(
            FunSpec
                .builder("create")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("httpClient", HttpClient::class)
                .returns(declaration.toClassName())
                .addCode(CodeBlock.of("return $implementationName(httpClient)"))
                .build()
        )
        .build()

    private fun Service.generateType() = TypeSpec
        .classBuilder(implementationName)
        .apply {
            addModifiers(KModifier.PRIVATE)
            addSuperinterface(declaration.toClassName())

            generateConstructor()

            for (method in methods) {
                addFunction(method.generateFunction())
            }
        }
        .build()

    private fun TypeSpec.Builder.generateConstructor() {
        val name = "httpClient"

        primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter(name, HttpClient::class)
                .build()
        )

        addProperty(
            PropertySpec.builder(name, HttpClient::class)
                .initializer(name)
                .addModifiers(KModifier.PRIVATE)
                .build()
        )
    }

    private fun Method.generateFunction() =
        MethodGenerator(logger, this).generate()
}
