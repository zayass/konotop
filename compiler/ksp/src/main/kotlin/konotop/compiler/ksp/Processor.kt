package konotop.compiler.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.writeTo

class Processor(
    private val options: Options,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val generator = Generator(logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val services = resolver.getServiceInterfaces()

        for (service in services) {
            process(service)
        }

        return emptyList()
    }

    private fun process(service: KSClassDeclaration) {
        val file = generator.generate(service.toModel())
        file.writeTo(codeGenerator, aggregating = true)
    }
}

class ProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Processor(
            Options.from(environment.options),
            environment.codeGenerator,
            environment.logger
        )
    }
}