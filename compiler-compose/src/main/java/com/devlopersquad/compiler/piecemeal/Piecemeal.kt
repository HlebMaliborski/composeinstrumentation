//
//  Copyright © 2024 Dynatrace LLC. All rights reserved.
//

package com.devlopersquad.compiler.piecemeal

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate.BuilderContext.annotated
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object Piecemeal {
    val ANNOTATION_FQ_NAME = FqName("com.bnorm.piecemeal.Piecemeal")
    val ANNOTATION_CLASS_ID = ClassId.topLevel(ANNOTATION_FQ_NAME)
    val ANNOTATION_PREDICATE = annotated(ANNOTATION_FQ_NAME)

    object Key : GeneratedDeclarationKey() {
        override fun toString(): String {
            return "PiecemealKey"
        }
    }
}

fun Name.toJavaSetter(): Name {
    val name = asString()
    return Name.identifier("set" + name[0].uppercase() + name.substring(1))
}