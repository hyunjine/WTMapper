package com.example.kapt_processor

import com.example.kapt_annotations.KaptLink
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.sun.source.tree.MethodTree
import com.sun.source.util.TreePath
import com.sun.source.util.TreePathScanner
import com.sun.source.util.Trees
import java.io.File
import java.lang.Exception
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic
import kotlin.reflect.jvm.internal.impl.descriptors.Named




@AutoService(Processor::class)
class KaptLinkProcessor : AbstractProcessor() {
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(KaptLink::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_17
    }

    private val targetDirectory: String
        get() = processingEnv.options[KaptBuilderProcessor.KAPT_KOTLIN_GENERATED_OPTION]
            ?: throw IllegalStateException("Unable to get target directory")

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
//        val test = roundEnv.getElementsAnnotatedWith(Swap2::class.java)
//        test.forEach {
//
//            val d = it.getAnnotation(Swap2::class.java)
//            if (d != null) {
//                noteMessage { "${it.simpleName} ${d.value}" }
//            }
//        }

        val elements = roundEnv
            .getElementsAnnotatedWith(KaptLink::class.java)
            .filterIsInstance<TypeElement>()
        if (elements.isEmpty()) {
            noteMessage { "Not able to find @${KaptLink::class.java.name} in this roundS $roundEnv" }
            return false
        }

        for (element in elements) {
            element as TypeElement
            when (element.kind) {
                ElementKind.CLASS -> {
                    writeForClass(element)
                }
                else -> errorMessage { "The annotation is invalid for the element type ${element.simpleName}. Please add ${KaptLink::class.java.name} either on Constructor or Class" }
            }
        }
        return true
    }

    private fun writeForClass(element: TypeElement) {
//        errorMessage { "??" }
        val annotation = element.getAnnotation(KaptLink::class.java)
        runCatching {
            annotation.kClass
        }.onFailure { exception ->
                if (exception is MirroredTypeException) {
                    element.enclosedElements.forEach {
                        if (it is ExecutableElement) {

                            noteMessage { it.receiverType }
                        }
                    }

                    val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
                    val fileName = "${element.simpleName}LinkerUtil"

                    val typeElement = exception.typeMirror.asTypeElement()
                    val className = ClassName("${typeElement.enclosingElement}", "${typeElement.simpleName}")
                    val allMembers = processingEnv.elementUtils.getAllMembers(typeElement)
                    val fieldElements = ElementFilter.fieldsIn(allMembers)

//                    element.enclosedElements.forEach {
//                        noteMessage { it.simpleName.toString() }
//                        val nn = it.getAnnotation(KaptSwap2::class.java)
//                        if (nn != null) {
//                            noteMessage { "!!!!!!!!!@@@@@@@@@@@@$nn" }
//                        }
//                    }

//                    errorMessage { "$className ${className.tags}" }
//                    errorMessage { "$fieldElements" }

                    val allMembers2 = processingEnv.elementUtils.getAllMembers(element as TypeElement)
                    val valueElements = ElementFilter.fieldsIn(allMembers2)
                    val classBuilder = TypeSpec.objectBuilder(fileName)

                    val file = FileSpec.builder(packageName, fileName)
                        .addFunction(createBuildMethod(typeElement, element, fieldElements, valueElements))
//                        .addType(classBuilder.build())
                        .build()
                    file.writeTo(File(targetDirectory))
                }
            }
    }

    private fun createBuildMethod(typeElement: TypeElement, modelElement: TypeElement, fieldElements: List<Element>, valueElements: List<Element>): FunSpec {
        return FunSpec.builder("build").apply {
            addParameter("entity", asKotlinTypeName(typeElement))
            val fieldNullCheck = StringBuilder()
            for (field in fieldElements) {
                fieldNullCheck.append("requireNotNull(entity.$field)").appendLine()
            }

            addCode(fieldNullCheck.toString())
        }

            .returns(ClassName("${modelElement.enclosingElement}", "${modelElement.simpleName}"))
            .receiver(modelElement.asType().asTypeName())
            .apply {
                val code = StringBuilder()
                val iterator = fieldElements.listIterator()
                repeat((fieldElements.indices).count()) {
                    val field = fieldElements[it]
                    val value = valueElements[it]

//                    val ann = value.getAnnotation(KaptSwap2::class.java)
//                    noteMessage { value.simpleName.toString() }
//                    if (ann != null) {
//                        noteMessage { "test: ${ann}" }
//                    }

                    code.appendLine()
                    code.append("\t$value = entity.$field")
                    if (iterator.hasNext()) {
                        code.append(",")
                    }
                }
                addCode(
                    """
                    |return ${modelElement.simpleName}($code
                    |)
                """.trimMargin()
                )
            }
            .build()
    }

    private fun TypeMirror.asTypeElement() = processingEnv.typeUtils.asElement(this) as TypeElement

    private fun TypeElement.asKotlinClassName(): ClassName {
        val className = asClassName()
        return try {
            // ensure that java.lang.* and java.util.* etc classes are converted to their kotlin equivalents
            Class.forName(className.canonicalName).kotlin.asClassName()
        } catch (e: ClassNotFoundException) {
            // probably part of the same source tree as the annotated class
            className
        }
    }

    private fun asKotlinTypeName(element: Element): TypeName {
        return element.asType().asKotlinType()
    }

    private fun TypeMirror.asKotlinType(): TypeName {
        return when (this) {
            is PrimitiveType -> {
                noteMessage { "TypeMirror is PrimitiveType" }
                val a = processingEnv.typeUtils.boxedClass(this).asKotlinClassName()
                a
            }
            is DeclaredType -> {
                noteMessage { "TypeMirror is DeclaredType" }
                this.asTypeElement().asKotlinClassName()
            }
            else -> this.asTypeElement().asKotlinClassName()
        }
    }

    private fun noteMessage(callback: () -> Any?) {
        val msg = callback.invoke()
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, msg.toString())
    }

    private fun errorMessage(callback: () -> String) {
        val msg = callback.invoke()
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg)
    }
}