package com.devlopersquad.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.createBlockBody
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class ClassGenerationIrExtension(private val logger: MessageCollector, private val compilerParameter: String) :
    IrGenerationExtension {
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

        for (file in moduleFragment.files) {
            for (declaration in file.declarations) {
                if (declaration is IrClass && declaration.name.asString() == "HelloWorld") {

                    logger.report(CompilerMessageSeverity.WARNING, "Compiler parameter: ${declaration.dump()}")

                    moduleFragment.transform(Transformer(logger, pluginContext, compilerParameter), null)
                }
            }
        }
    }
}

class Transformer(
    private val logger: MessageCollector,
    private val pluginContext: IrPluginContext,
    private val compilerParameter: String
) : IrElementTransformerVoid() {
    override fun visitClass(declaration: IrClass): IrStatement {
        val function = declaration.addFunction {
            name = Name.identifier("test123")
            visibility = DescriptorVisibilities.PUBLIC
            returnType = pluginContext.irBuiltIns.stringType
            modality = Modality.OPEN
            origin = IrDeclarationOrigin.DEFINED
        }

        function.parent = declaration

        val variable = IrVariableImpl(
            startOffset = function.startOffset,
            endOffset = function.endOffset,
            name = Name.identifier("name"),
            type = pluginContext.irBuiltIns.stringType,
            isVar = false,
            isConst = false,
            isLateinit = false,
            origin = IrDeclarationOrigin.DEFINED,
            symbol = IrVariableSymbolImpl()
        ).apply {
            initializer = IrConstImpl.string(
                startOffset = function.startOffset,
                endOffset = function.endOffset,
                type = pluginContext.irBuiltIns.stringType,
                value = "asd"
            )
        }

        variable.parent = function

        val returnStatement = IrReturnImpl(
            startOffset = function.startOffset,
            endOffset = function.endOffset,
            type = pluginContext.irBuiltIns.stringType,
            returnTargetSymbol = function.symbol,
            value = IrGetValueImpl(
                startOffset = function.startOffset,
                endOffset = function.endOffset,
                type = pluginContext.irBuiltIns.stringType,
                symbol = variable.symbol
            )
        )

        function.body = pluginContext.irFactory.createBlockBody(
            function.startOffset, function.endOffset, statements = listOf(
                variable,
                returnStatement
            )
        )

        return super.visitClass(declaration)
    }

    @OptIn(ObsoleteDescriptorBasedAPI::class)
    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (declaration.name == Name.identifier("test3")) {
            val bodyBuilder =
                DeclarationIrBuilder(pluginContext, declaration.symbol, declaration.startOffset, declaration.endOffset)

            val composableClassId = ClassId.topLevel(FqName("android.util.Log"))
            val composableSymbol = pluginContext.referenceClass(composableClassId)

            val statement = createLogCall(
                bodyBuilder,
                composableSymbol?.functions?.find { it.descriptor.name == Name.identifier("d") }!!,
                compilerParameter
            )

            declaration.body = pluginContext.irFactory.createBlockBody(
                declaration.startOffset, declaration.endOffset, statements = listOf(
                    statement
                )
            )
        }

        return super.visitFunction(declaration)
    }

    private fun createLogCall(
        builder: IrBuilderWithScope,
        logFunctionSymbol: IrSimpleFunctionSymbol,
        compilerParameter: String
    ) = builder.irCall(logFunctionSymbol).apply {
        putValueArgument(0, builder.irString("test"))
        putValueArgument(1, builder.irString(compilerParameter))
    }
}

