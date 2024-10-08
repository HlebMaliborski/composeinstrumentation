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
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.addMember
import org.jetbrains.kotlin.ir.declarations.createExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrValueParameterSymbolImpl
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.copyTo
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 *
 */
class ClassGenerationIrExtension(private val logger: MessageCollector) :
    IrGenerationExtension {
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
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
                        returnType = irBuiltIns.intType
                        modality = Modality.OPEN
                        origin = IrDeclarationOrigin.DEFINED
                    }.apply {
                        // add into function parameter

                        val bodyBuilder1 =
                            DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset)
                        // Add @Composable annotation
                   /*     val composableClassId = ClassId.topLevel(FqName("androidx.compose.runtime.Composable"))
                        val composableSymbol = pluginContext.referenceClass(composableClassId)?.owner?.symbol
                        if (composableSymbol != null) {
                            annotations += bodyBuilder1.irCallConstructor(
                                composableSymbol.constructors.first(),
                                emptyList()
                            )
                        }*/

                        // Insert the variable into the beginning of the function body
                        body = bodyBuilder1.irBlockBody {
                            +bodyBuilder1.irReturn(bodyBuilder1.irInt(1))
                        }

                        dispatchReceiverParameter =
                            declaration.thisReceiver?.copyTo(this) // to define functions is static or not

                        parent = declaration
                    }




                    declaration.addMember(helloWorldFunction)
                }
            }
        }

        moduleFragment.transform(Transformer(logger), null)
    }
}

class Transformer(private val logger: MessageCollector) : IrElementTransformerVoid() {
    override fun visitClass(declaration: IrClass): IrStatement {
        logger.report(CompilerMessageSeverity.WARNING, declaration.name.asString())
        return super.visitClass(declaration)
    }

    override fun visitFunction(declaration: IrFunction): IrStatement {
        logger.report(CompilerMessageSeverity.WARNING, declaration.name.asString() + "1")
        return super.visitFunction(declaration)
    }
}