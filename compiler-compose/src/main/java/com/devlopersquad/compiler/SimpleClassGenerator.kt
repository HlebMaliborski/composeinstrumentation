package com.devlopersquad.compiler

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.AnnotationFqn
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createNestedClass
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.constructStarProjectedType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

/*
 * Generates top level class
 *
 * public final class foo.bar.MyClass {
 *     fun foo(): String = "Hello world"
 * }
 */
class SimpleClassGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
    companion object {
        private val MATERIALIZE_NAME = Name.identifier("materialize")
        private val NESTED_NAME = Name.identifier("Nested")

        /**
         * This annotation does not exist in 'plugin-annotations' module/jar; instead,
         * it's supposed to be defined in the user's (or test case) code.
         *
         * We need this to test that the generation extensions and annotation resolvers
         * properly work with such annotations and with the declarations marked by them.
         */
        private val MY_ANNOTATION = AnnotationFqn("foo.MyAnnotation")

        private val PREDICATE = LookupPredicate.create {
            annotated(AnnotationFqn("NestedClassAndMaterializeMember")) or annotated(MY_ANNOTATION)
        }
    }

    private val predicateBasedProvider = session.predicateBasedProvider
    private val matchedClasses by lazy {
        predicateBasedProvider.getSymbolsByPredicate(PREDICATE).filterIsInstance<FirRegularClassSymbol>()
    }

    private val nestedClassIds by lazy {
        matchedClasses.map { it.classId.createNestedClassId(NESTED_NAME) }
    }

    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        if (callableId.callableName != MATERIALIZE_NAME) return emptyList()
        if (context == null) return emptyList()
        val matchedClassSymbol = matchedClasses.firstOrNull { it == context.owner } ?: return emptyList()
        val function = createMemberFunction(
            context.owner,
            Key,
            callableId.callableName,
            matchedClassSymbol.constructStarProjectedType()
        )
        return listOf(function.symbol)
    }

    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext
    ): FirClassLikeSymbol<*>? {
        if (matchedClasses.none { it == owner }) return null
        return createNestedClass(owner, name, Key).symbol
    }

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val createConstructor = createConstructor(context.owner, Key, generateDelegatedNoArgConstructorCall = true)
        return listOf(createConstructor.symbol)
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        return when {
            classSymbol in matchedClasses -> setOf(MATERIALIZE_NAME)
            classSymbol.classId in nestedClassIds -> setOf(SpecialNames.INIT)
            else -> emptySet()
        }
    }

    override fun getNestedClassifiersNames(
        classSymbol: FirClassSymbol<*>,
        context: NestedClassGenerationContext
    ): Set<Name> {
        return if (classSymbol in matchedClasses) setOf(NESTED_NAME) else emptySet()
    }

    object Key : GeneratedDeclarationKey() {
        override fun toString(): String {
            return "AllOpenMembersGeneratorKey"
        }
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(PREDICATE)
    }
}
