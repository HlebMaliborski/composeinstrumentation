//
//  Copyright © 2024 Dynatrace LLC. All rights reserved.
//

package com.devlopersquad.compiler

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid

/**
 *
 */
class FPIrVisitor(
    private val logger: MessageCollector,
    private val loggingTag: String,
) : IrElementVisitorVoid {
    override fun visitModuleFragment(declaration: IrModuleFragment) {
        declaration.files.forEach { file ->
            file.accept(this, null)
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitFile(declaration: IrFile) {
        declaration.declarations.forEach { item ->
            item.accept(this, null)
        }
    }

    override fun visitFunction(declaration: IrFunction) {
        val render = buildString {
            append(declaration.fqNameWhenAvailable!!.asString() + "(")
            val parameters = declaration.valueParameters.iterator()
            while (parameters.hasNext()) {
                val parameter = parameters.next()
                append(parameter.name.asString())
                append(": ${parameter.type.classFqName!!.shortName().asString()}")
                if (parameters.hasNext()) append(", ")
            }
            append("): " + declaration.returnType.classFqName!!.shortName().asString())
        }
        logger.report(CompilerMessageSeverity.WARNING, "[$loggingTag] $render")
    }
}