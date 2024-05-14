package com.devlopersquad.plugin

import com.devlopersquad.plugin.ClickableClassVisitor.ClickableMethodVisitor.Companion.CLICAKBLE_METHOD_DESCRIPTOR
import com.devlopersquad.plugin.ClickableClassVisitor.ClickableMethodVisitor.Companion.CLICKBLE_METHOD_NAME
import com.devlopersquad.plugin.ClickableClassVisitor.Companion.CLICKBLE_CLASS
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

public object ClassVisitorFactory {
    fun getClassVisitor(classFilterName: String, classVisitor: ClassVisitor): ClassVisitor {
        return when (classFilterName) {
            CLICKBLE_CLASS -> ClickableClassVisitor(classVisitor = classVisitor)
            else -> classVisitor
        }
    }
}

public class ClickableClassVisitor(
    api: Int = ASM9,
    classVisitor: ClassVisitor
) : ClassVisitor(api, classVisitor) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (CLICKBLE_METHOD_NAME.equals(name) && CLICAKBLE_METHOD_DESCRIPTOR.equals(descriptor)) {
            ClickableMethodVisitor(methodVisitor)
        } else {
            methodVisitor
        }
    }

    companion object {
        public const val CLICKBLE_CLASS = "androidx.compose.foundation.ClickableKt"
        fun instrumentClass(classFilterName: String): Boolean {
            return CLICKBLE_CLASS.equals(classFilterName)
        }
    }

    private class ClickableMethodVisitor(mv: MethodVisitor) : MethodVisitor(ASM9, mv) {
        override fun visitCode() {
            mv.visitTypeInsn(NEW, "com/example/jetpackinstrumentation/ClickableComposeCallback")
            mv.visitInsn(DUP)
            mv.visitVarInsn(ALOAD, 6);
            mv.visitMethodInsn(
                INVOKESPECIAL,
                "com/example/jetpackinstrumentation/ClickableComposeCallback",
                "<init>",
                "(Lkotlin/jvm/functions/Function0;)V",
                false
            )
            mv.visitVarInsn(ASTORE, 6)
            super.visitCode()
        }

        companion object {
            public const val CLICKBLE_METHOD_NAME = "clickable-O2vRcR0"
            public const val CLICAKBLE_METHOD_DESCRIPTOR =
                "(Landroidx/compose/ui/Modifier;Landroidx/compose/foundation/interaction/MutableInteractionSource;Landroidx/compose/foundation/Indication;ZLjava/lang/String;Landroidx/compose/ui/semantics/Role;Lkotlin/jvm/functions/Function0;)Landroidx/compose/ui/Modifier;"
        }
    }
}
