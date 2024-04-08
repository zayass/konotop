package konotop.compiler.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.ksp.toClassName
import io.ktor.client.*
import konotop.ApiFactory

class FileGenerator(val logger: KSPLogger) {

    fun generate(service: Service) = service.generateFile()

    private fun Service.generateFile(): FileSpec {
        val implementationClass = generateType()
        val factoryObject = generateFactoryObject(implementationClass)

        return FileSpec
            .builder(packageName, implementationName)
            .addType(implementationClass)
            .addType(factoryObject)
            .build()
    }

    private fun Service.generateFactoryObject(implementationClass: TypeSpec) = TypeSpec
        .objectBuilder(factoryName)
        .addOriginatingKSClass(declaration)
        .addModifiers(KModifier.INTERNAL)
        .addSuperinterface(ApiFactory::class.asClassName().plusParameter(declaration.toClassName()))
        .addFunction(
            FunSpec
                .builder("create")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("httpClient", HttpClient::class)
                .returns(declaration.toClassName())
                .addCode(CodeBlock.of("return %N(httpClient)", implementationClass))
                .build()
        )
        .build()

    private fun Service.generateType() = TypeSpec
        .classBuilder(implementationName)
        .addOriginatingKSClass(declaration)
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
