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

    private fun FunSpec.Builder.buildBody() =
        addCode(
            buildCodeBlock {
                emitMethodBody()
            }
        )

    private fun CodeBlock.Builder.emitMethodBody() = buildWith(method) {
        emitPathVal()

        val verb = KtorMembers.httpVerb(httpMethod)
        val returnType = declaration.returnType
        val isRawResponse = returnType?.toTypeName() == HttpResponse::class.asTypeName()
        val bodyArgument = bodyArgument()
        val queryArguments = queryArguments()

        if (returnType != null) {
            add("return ")
        }

        addStatement("httpClient")
        withIndent {
            if (bodyArgument != null || queryArguments.isNotEmpty()) {
                withControlFlow(".%M(path)", verb) {
                    emitHttpBody(bodyArgument)
                    emitQueryArguments(queryArguments)
                }
            } else {
                addStatement(".%M(path)", verb)
            }

            if (!isRawResponse) {
                addStatement(".%M()", KtorMembers.getBody)
            }
        }
    }

    private fun CodeBlock.Builder.emitPathVal() = buildWith(method) {
        addStatement("val path = %S", path)

        withIndent {
            for (argument in pathArguments()) {
                val kotlinArgumentName = argument.kotlinArgumentName
                val pathArgumentName = argument.pathArgumentName
                val pathArgumentPlaceholder = "{${pathArgumentName}}"

                addStatement(".replace(%S, ${kotlinArgumentName}.toString())", pathArgumentPlaceholder)
            }
        }
    }

    private fun CodeBlock.Builder.emitHttpBody(argument: Arg.BodyArgument?) {
        if (argument != null) {
            addStatement("%M(%M)", KtorMembers.setContentType, KtorMembers.applicationJson)
            addStatement("%M(${argument.kotlinArgumentName})", KtorMembers.setBody)
        }
    }

    private fun CodeBlock.Builder.emitQueryArguments(arguments: List<Arg.QueryArgument>) {
        if (arguments.isEmpty()) {
            return
        }

        withControlFlow("url") {
            for (argument in arguments) {
                emitQueryArgument(argument)
            }
        }
    }

    private fun CodeBlock.Builder.emitQueryArgument(argument: Arg.QueryArgument) = buildWith(argument) {
        if (isNullable) {
            withControlFlow("$kotlinArgumentName?.let") {
                addStatement("parameters.append(%S, it.toString())", queryArgumentName)
            }
        } else {
            addStatement("parameters.append(%S, $kotlinArgumentName.toString())", queryArgumentName)
        }
    }
}

inline fun <Builder, Receiver> Builder.buildWith(receiver: Receiver, block: Receiver.() -> Unit): Builder {
    receiver.block()
    return this
}

inline fun CodeBlock.Builder.withControlFlow(controlFlow: String, vararg args: Any?, builderAction: CodeBlock.Builder.() -> Unit) =
    beginControlFlow(controlFlow, *args).also(builderAction).endControlFlow()

private fun KSValueParameter.toParameterSpec() = ParameterSpec(
    name!!.asString(),
    this.type.toTypeName()
)
