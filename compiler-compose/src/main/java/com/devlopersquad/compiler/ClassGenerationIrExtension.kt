//
//  Copyright © 2024 Dynatrace LLC. All rights reserved.
//

package com.devlopersquad.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildProperty
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.addMember
import org.jetbrains.kotlin.ir.declarations.createExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.copyTo
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name

/**
 *
 */
class ClassGenerationIrExtension(private val logger: MessageCollector) :
    IrGenerationExtension {
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        /*val transformer = object : IrElementTransformerVoid() {
            override fun visitClass(declaration: IrClass): IrStatement {

                if (declaration.name.asString() == "HelloWorld") {
                    val helloWorldFunction = pluginContext.irFactory.buildFun {
                        name = Name.identifier("helloWorld1")
                        visibility = DescriptorVisibilities.PUBLIC
                        returnType = pluginContext.irBuiltIns.unitType
                        modality = Modality.FINAL
                    }
                    declaration.addMember(helloWorldFunction)
                    logger.report(CompilerMessageSeverity.WARNING, declaration.name.asString())
                }
                return super.visitClass(declaration)
            }
        }
        moduleFragment.transform(transformer, null)
        moduleFragment.transform(transformer, null)*/

        for (file in moduleFragment.files) {
            for (declaration in file.declarations) {
                if (declaration is IrClass && declaration.name.asString() == "HelloWorld") {
                    val property = pluginContext.irFactory.buildProperty {
                        name = Name.identifier("testVariable")
                        isVar = false
                        isConst = false
                        isLateinit = false
                        isDelegated = false
                        isExternal = false
                        isExpect = false
                        visibility = DescriptorVisibilities.PUBLIC
                    }.apply {
                        backingField = pluginContext.irFactory.buildField {
                            name = Name.identifier("testVariable")
                            type = pluginContext.irBuiltIns.intType
                            visibility = DescriptorVisibilities.PRIVATE
                            isFinal = true
                            isStatic = false
                            origin = IrDeclarationOrigin.PROPERTY_BACKING_FIELD
                            parent = declaration
                        }.apply {
                            initializer = pluginContext.irFactory.createExpressionBody(
                                IrConstImpl(
                                    startOffset = declaration.startOffset,
                                    endOffset = declaration.endOffset,
                                    type = pluginContext.irBuiltIns.intType,
                                    kind = IrConstKind.Int,
                                    value = 1
                                )
                            )
                            parent = declaration
                        }
                    }

                    declaration.addMember(property)


                    logger.report(CompilerMessageSeverity.WARNING, declaration.name.asString())

                    val irFactory = pluginContext.irFactory
                    val irBuiltIns = pluginContext.irBuiltIns
                    val startOffset = declaration.startOffset
                    val endOffset = declaration.endOffset

                    val helloWorldFunction = irFactory.buildFun {
                        name = Name.identifier("test")
                        visibility = DescriptorVisibilities.PUBLIC
                        returnType = irBuiltIns.unitType
                        modality = Modality.OPEN
                    }.apply {
                        dispatchReceiverParameter =
                            declaration.thisReceiver?.copyTo(this) // to define functions is static or not
                        val bodyBuilder = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset)
                        body = bodyBuilder.irBlockBody {

                        }

                        parent = declaration
                    }

                    declaration.addMember(helloWorldFunction)
                }
            }
        }
    }
}

class Transformer : IrElementTransformerVoid() {
    override fun visitClass(declaration: IrClass): IrStatement {

        return super.visitClass(declaration)
    }
}