package com.devlopersquad.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.create
import org.objectweb.asm.ClassVisitor

/**
 *
 */
public class ComposeInstrumentationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<InstrumentationPluginExtension>("instrumentation")

        // After that we have to listen when the Android Gradle plugin is applied to the project and retrieve the
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_ALL_CLASSES)
            variant.instrumentation.transformClassesWith(
                InstrumentationPluginFactory::class.java,
                InstrumentationScope.ALL
            ) { params ->
                params.instrumentedClasses.set(extension.instrumentedClasses)
            }
        }
    }

    abstract class InstrumentationPluginFactory : AsmClassVisitorFactory<InstrumentationPluginParameters> {
        override fun createClassVisitor(
            classContext: ClassContext, nextClassVisitor: ClassVisitor,
        ): ClassVisitor {
            return ClassVisitorFactory.getClassVisitor(
                classContext.currentClassData.className,
                nextClassVisitor
            )
        }

        override fun isInstrumentable(classData: ClassData): Boolean {

            if (classData.className.contains("Test")) {
                println(classData.className)
            }
            return parameters.get().instrumentedClasses.get().contains(classData.className)
        }
    }

    interface InstrumentationPluginParameters : InstrumentationParameters {
        @get:Input
        val instrumentedClasses: ListProperty<String>
    }
}

interface InstrumentationPluginExtension {
    val instrumentedClasses: ListProperty<String>
}
