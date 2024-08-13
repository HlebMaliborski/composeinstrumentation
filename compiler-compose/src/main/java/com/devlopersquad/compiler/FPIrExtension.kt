//
//  Copyright © 2024 Dynatrace LLC. All rights reserved.
//

package com.devlopersquad.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.dump

/**
 *
 */
class FPIrExtension(
    private val logger: MessageCollector,
    private val loggingTag: String,
) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {

        println(moduleFragment.dump())
        /* val transformer = object : IrElementTransformerVoidWithContext() {

         }*/
        moduleFragment.accept(FPIrVisitor(logger, loggingTag), null)
    }
}