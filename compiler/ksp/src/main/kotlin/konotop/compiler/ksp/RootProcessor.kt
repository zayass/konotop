package konotop.compiler.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.writeTo

class RootProcessor(
    private val options: Options,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val generator = Generator(logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("!!! Started")

        val services = resolver.getServiceInterfaces()

        for (service in services) {
            process(service)
        }

        logger.warn("!!! Finished")
        return emptyList()
    }

    private fun process(service: KSClassDeclaration) {
        val file = generator.generate(service.toModel())
        file.writeTo(codeGenerator, aggregating = true)
    }
}
