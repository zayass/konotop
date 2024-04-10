package konotop.kotlin.compiler.plugin

import org.jetbrains.kotlin.backend.common.ClassLoweringPass
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.konan.isNative

class LoweringExtension(private val logger: Logger) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if (!pluginContext.platform.shouldGenerateForPlatform()) {
            return
        }

        val pass = LoweringPass(pluginContext, logger)
        pass.lower(moduleFragment)
    }

    private fun TargetPlatform?.shouldGenerateForPlatform(): Boolean {
        return isNative() || isJs()
    }
}

class PluginContext(baseContext: IrPluginContext, val logger: Logger) : IrPluginContext by baseContext {
    val apiFactoryClass by lazy {
        referenceClass(Qualifiers.ApiFactory)
    }

    val annotationMarkerClass by lazy {
        referenceClass(Qualifiers.AssociatedFactory)
    }
}

private class LoweringPass(baseContext: IrPluginContext, logger: Logger) : IrElementTransformerVoid(), ClassLoweringPass {
    private val context = PluginContext(baseContext, logger)

    override fun lower(irClass: IrClass) {
        MarkerAnnotationIrGenerator.generate(irClass, context)
    }
}
