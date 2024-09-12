package com.devlopersquad.compiler

import ComposeCommandLineProcessor.Companion.ARG_STRING
import com.devlopersquad.compiler.piecemeal.PiecemealFirExtensionRegistrar
import com.devlopersquad.compiler.piecemeal.PiecemealIrGenerationExtension
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

/**
 *
 */
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class ComposeCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        // configuration.get(key: CompilerConfigurationKey<T>, defaultValue: T (optional))

        val logger = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        val tag = configuration.get(ARG_STRING)
        /* FirExtensionRegistrarAdapter.registerExtension(FirClassGeneratorRegistrar(logger))
         IrGenerationExtension.registerExtension(ClassGenerationIrExtension(logger))*/
        FirExtensionRegistrarAdapter.registerExtension(PiecemealFirExtensionRegistrar())
        IrGenerationExtension.registerExtension(PiecemealIrGenerationExtension())
    }
}

class Test : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        TODO("Not yet implemented")
    }
}