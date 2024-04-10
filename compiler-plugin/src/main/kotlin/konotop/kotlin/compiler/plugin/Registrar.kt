package konotop.kotlin.compiler.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

private val KEY_ENABLED = CompilerConfigurationKey<Boolean>("whether the plugin is enabled")

@OptIn(ExperimentalCompilerApi::class)
class PluginOptions : CommandLineProcessor {
    override val pluginId = "konotop.compiler.plugin"

    override val pluginOptions: Collection<CliOption> = listOf(
        ENABLED_OPTION
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) = when (option) {
        ENABLED_OPTION -> configuration.put(KEY_ENABLED, value.toBoolean())
        else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
    }

    companion object {
        val ENABLED_OPTION = CliOption(
            optionName = "enabled",
            valueDescription = "<true|false>",
            description = "whether to enable the plugin or not",
            required = false
        )
    }
}

@OptIn(ExperimentalCompilerApi::class)
class PluginRegistrar : CompilerPluginRegistrar() {
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }

        val logger = Logger(configuration.get(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE
        ))

        IrGenerationExtension.registerExtension(LoweringExtension(logger))
    }

    override val supportsK2: Boolean
        get() = true
}
