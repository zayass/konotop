package konotop.compiler.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import io.ktor.client.statement.*


class MethodGenerator(private val logger: KSPLogger, private val method: Method) {
    fun generate() = FunSpec
        .builder(method.name)
        .buildSignature()
        .buildBody()
        .build()


    private fun FunSpec.Builder.buildSignature() = buildWith(method) {
        addModifiers(KModifier.OVERRIDE)

        if (isSuspend()) {
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

    private fun FunSpec.Builder.buildBody() = apply {
        addCode(
            CodeBlock
                .builder()
                .emitMethodBody()
                .build()
        )
    }

    private fun CodeBlock.Builder.emitMethodBody() = buildWith(method) {
        addStatement("val path = \"${path}\"")

        indent()
        for (argument in pathArguments()) {
            val kotlinArgumentName = argument.kotlinArgumentName
            val pathArgumentName = argument.pathArgumentName

            addStatement(".replace(\"{${pathArgumentName}}\", ${kotlinArgumentName}.toString())")
        }
        unindent()

        val verb = httpMethod.name.lowercase()
        val returnType = declaration.returnType
        val isRawResponse = returnType?.toTypeName() == HttpResponse::class.asTypeName()
        val bodyArgument = bodyArgument()
        val queryArguments = queryArguments()

        if (returnType != null) {
            add("return ")
        }

        addStatement("httpClient")
        indent()

        if (bodyArgument != null || queryArguments.isNotEmpty()) {
            beginControlFlow(".$verb(path)")
            emitHttpBody(bodyArgument)
            emitQueryArguments(queryArguments)
            endControlFlow()
        } else {
            addStatement(".$verb(path)")
        }

        if (!isRawResponse) {
            addStatement(".body()")
        }

        unindent()
    }

    private fun CodeBlock.Builder.emitHttpBody(argument: Arg.BodyArgument?) {
        if (argument != null) {
            addStatement("contentType(ContentType.Application.Json)")
            addStatement("setBody(${argument.kotlinArgumentName})")
        }
    }

    private fun CodeBlock.Builder.emitQueryArguments(arguments: List<Arg.QueryArgument>) {
        if (arguments.isEmpty()) {
            return
        }

        beginControlFlow("url")
        for (argument in arguments) {
            emitQueryArgument(argument)
        }
        endControlFlow()
    }

    private fun CodeBlock.Builder.emitQueryArgument(argument: Arg.QueryArgument) = buildWith(argument) {
        if (isNullable) {
            beginControlFlow("$kotlinArgumentName?.let")
            addStatement("""parameters.append("$queryArgumentName", it.toString())""")
            endControlFlow()
        } else {
            addStatement("""parameters.append("$queryArgumentName", $kotlinArgumentName.toString())""")
        }
    }
}

inline fun <Builder, Receiver> Builder.buildWith(receiver: Receiver, block: Receiver.() -> Unit): Builder {
    receiver.block()
    return this
}

private fun KSValueParameter.toParameterSpec() = ParameterSpec(
    name!!.asString(),
    this.type.toTypeName()
)
