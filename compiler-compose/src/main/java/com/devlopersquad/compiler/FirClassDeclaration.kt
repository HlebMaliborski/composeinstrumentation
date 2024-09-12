package com.devlopersquad.compiler

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.fir.expressions.builder.buildBlock
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

/**
 *
 */
class FirClassDeclaration(session: FirSession) : FirDeclarationGenerationExtension(session) {
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {

            // Create the `test()` function
            val testFunction = createTestFunction(callableId.classId!!)
            return listOf(testFunction.symbol)
        /*val function = createMemberFunction(
            context?.owner!!,
            Key,
            Name.identifier("test"),
            returnType = session.builtinTypes.intType.type
        )
        return listOf(function.symbol)*/
    }

    private fun createTestFunction(classId: ClassId): FirSimpleFunction {
        return buildSimpleFunction {
            origin = FirDeclarationOrigin.Source
            returnTypeRef = buildResolvedTypeRef {
                type = session.builtinTypes.intType.type
            }
            name = Name.identifier("test")
            symbol = FirNamedFunctionSymbol(CallableId(classId, Name.identifier("test")))

            // Define the function body (empty body)
            body = buildBlock {
                // Optionally, add statements here
            }
        }
    }

    object Key : GeneratedDeclarationKey()
}
