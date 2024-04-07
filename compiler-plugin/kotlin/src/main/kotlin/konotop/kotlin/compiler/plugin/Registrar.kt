package konotop.kotlin.compiler.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class PluginOptions : CommandLineProcessor {
    override val pluginId = "konotop.compiler.plugin"
    override val pluginOptions = emptyList< AbstractCliOption>()
}

@OptIn(ExperimentalCompilerApi::class)
class PluginRegistrar : CompilerPluginRegistrar() {
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(LoweringExtension())
    }

    override val supportsK2: Boolean
        get() = true
}
