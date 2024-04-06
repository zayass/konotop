package konotop.compiler.ksp

private const val OPTION_VERBOSE = "konotop.verbose"

data class Options(
    val verbose: Boolean = true
) {
    companion object {
        fun from(map: Map<String, String>): Options {
            return Options(
                verbose = map[OPTION_VERBOSE]?.toBoolean() ?: true,
            )
        }
    }

    fun toMap(): Map<String, String> = mapOf(
        OPTION_VERBOSE to verbose.toString(),
    )
}