import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class ComposeCommandLineProcessor : CommandLineProcessor {
    public companion object {
        private const val OPTION_STRING = "tag"

        public val ARG_STRING = CompilerConfigurationKey<String>(OPTION_STRING)
    }

    override val pluginId: String = "com.devlopersquad.kotlin.compose"
    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = OPTION_STRING,
            valueDescription = "string",
            description = ARG_STRING.toString(),
            required = false,
        ),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        return when (option.optionName) {
            OPTION_STRING -> configuration.put(ARG_STRING, value)
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }
}
