package konotop.kotlin.compiler.plugin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.OutputMessageUtil

class Logger(private val messageCollector: MessageCollector) {
    private fun log(severity: CompilerMessageSeverity, message: String) {
        messageCollector.report(severity, TAG + message)
    }

    fun exception(exception: Throwable) =
        log(CompilerMessageSeverity.EXCEPTION, OutputMessageUtil.renderException(exception))

    fun error(message: String) =
        log(CompilerMessageSeverity.ERROR, message)
    fun warning(message: String) =
        log(CompilerMessageSeverity.WARNING, message)
    fun info(message: String) =
        log(CompilerMessageSeverity.INFO, message)
    fun logging(message: String) =
        log(CompilerMessageSeverity.LOGGING, message)

    companion object {
        private const val TAG = "[konotop] "
    }
}