package com.devlopersquad.compiler

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

/**
 *
 */
class FirClassGeneratorRegistrar(private val logger: MessageCollector) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        logger.report(CompilerMessageSeverity.WARNING, "dsaewqewqe")
        +::FirClassDeclaration
    }
}
