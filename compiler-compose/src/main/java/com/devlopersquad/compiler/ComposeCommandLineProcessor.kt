import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class ComposeCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "com.devlopersquad.kotlin.compose"
    override val pluginOptions: Collection<AbstractCliOption> = emptyList()
}
