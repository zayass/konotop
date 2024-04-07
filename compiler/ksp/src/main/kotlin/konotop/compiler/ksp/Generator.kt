package konotop.compiler.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import io.ktor.client.*
import io.ktor.client.statement.*
import konotop.ApiFactory

class Generator(val logger: KSPLogger) {

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

    private fun Method.generateFunction() = FunSpec
        .builder(declaration.simpleName.asString())
        .apply {
            addModifiers(KModifier.OVERRIDE)

            if (declaration.modifiers.contains(Modifier.SUSPEND)) {
                addModifiers(KModifier.SUSPEND)
            }

            for (argument in arguments) {
                addParameter(argument.declaration.toParameterSpec())
            }

            val returnType = declaration.returnType
            if (returnType != null) {
                returns(returnType.toTypeName())
            }
        }
        .addCode(generateBody())
        .build()


    private fun Method.generateBody() = CodeBlock
        .builder()
        .apply {
            addStatement("val path = \"${path}\"")

            indent()
            for (argument in arguments.filterIsInstance<Arg.PathArgument>()) {
                addStatement(".replace(\"{${argument.name}}\", ${argument.declaration.name!!.asString()}.toString())")
            }
            unindent()

            val verb = httpMethod.name.lowercase()
            val returnType = declaration.returnType
            val isRawResponse = returnType?.toTypeName() == HttpResponse::class.asTypeName()
            val bodyArgumentName = bodyArgument()?.declaration?.name?.asString()

            if (returnType != null) {
                add("return ")
            }

            addStatement("httpClient")
            indent()

            if (bodyArgumentName != null) {
                beginControlFlow(".$verb(path)")
                addStatement("contentType(ContentType.Application.Json)")
                addStatement("setBody($bodyArgumentName)")
                endControlFlow()
            } else {
                addStatement(".$verb(path)")
            }

            if (!isRawResponse) {
                addStatement(".body()")
            }

            unindent()
        }
        .build()
}

private fun KSValueParameter.toParameterSpec() = ParameterSpec(
    name!!.asString(),
    this.type.toTypeName()
)
