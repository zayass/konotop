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
            addImport("io.ktor.client.call", "body")
            addImport(
                packageName = "io.ktor.client.request",
                names = listOf("delete", "get", "head", "options", "patch", "post", "put")
            )
            addType(generateType())
            addType(generateFactoryObject())
        }
        .build()

    private fun Service.generateFactoryObject() = TypeSpec
        .objectBuilder(factoryName)
        .addModifiers(KModifier.INTERNAL)
        .addSuperinterface(ApiFactory::class.asClassName().plusParameter(origin.toClassName()))
        .addFunction(
            FunSpec
                .builder("create")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("httpClient", HttpClient::class)
                .returns(origin.toClassName())
                .addCode(CodeBlock.of("return $implementationName(httpClient)"))
                .build()
        )
        .build()

    private fun Service.generateType() = TypeSpec
        .classBuilder(implementationName)
        .apply {
            addModifiers(KModifier.PRIVATE)
            addSuperinterface(origin.toClassName())

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
        .builder(origin.simpleName.asString())
        .apply {
            addModifiers(KModifier.OVERRIDE)

            if (origin.modifiers.contains(Modifier.SUSPEND)) {
                addModifiers(KModifier.SUSPEND)
            }

            for (argument in arguments) {
                addParameter(argument.origin.toParameterSpec())
            }

            val returnType = origin.returnType
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
                addStatement(".replace(\"{${argument.name}}\", ${argument.origin.name!!.asString()}.toString())")
            }
            unindent()

            val verb = httpMethod.name.lowercase()
            val returnType = origin.returnType

            if (returnType == null) {
                addStatement("httpClient.$verb(path)")
            } else if (returnType.toTypeName() == HttpResponse::class.asTypeName()) {
                addStatement("return httpClient.$verb(path)")
            } else {
                addStatement("return httpClient.$verb(path).body()")
            }

        }
        .build()
}

private fun KSValueParameter.toParameterSpec() = ParameterSpec(
    name!!.asString(),
    this.type.toTypeName()
)
