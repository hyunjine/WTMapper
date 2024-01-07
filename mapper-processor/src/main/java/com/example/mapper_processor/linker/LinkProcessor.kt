package com.example.mapper_processor.linker

import com.example.mapper_processor.BaseAbstractProcessor
import com.example.mapper_processor.builder.BuilderProcessor
import com.example.mapper_processor.kson.KsonProcessor
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.BYTE
import com.squareup.kotlinpoet.CHAR
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.SHORT
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.ErrorType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.NoType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.SimpleTypeVisitor8


@AutoService(Processor::class)
class LinkProcessor : BaseAbstractProcessor() {
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Link::class.java.name)
    }

    private val targetDirectory: String
        get() = processingEnv.options[BuilderProcessor.KAPT_KOTLIN_GENERATED_OPTION]
            ?: throw IllegalStateException("Unable to get target directory")

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(Link::class.java)
        errorMessage { annotations.toString() }
        if (annotations.isEmpty()) {
            noteMessage { "Not able to find @${Link::class.java.name} in this round $roundEnv" }
            return true
        }

        for (element in elements) {
            errorMessage { "??" }
            val annotation = element.getAnnotation(Link::class.java)
            runCatching {
                annotation.kClass
            }.onSuccess {
                return true
            }
                .onFailure { exception ->
                if (exception is MirroredTypeException) {
                    val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
                    val fileName = "${element.simpleName}LinkerUtil"

                    val typeElement = exception.typeMirror.asTypeElement()
                    val className = ClassName("${typeElement.enclosingElement}", "${typeElement.simpleName}")
                    val allMembers = processingEnv.elementUtils.getAllMembers(typeElement)
                    val fieldElements = ElementFilter.fieldsIn(allMembers)

                    errorMessage { "$className ${className.tags}" }
                    errorMessage { "$fieldElements" }

                    val allMembers2 = processingEnv.elementUtils.getAllMembers(element as TypeElement)
                    val valueElements = ElementFilter.fieldsIn(allMembers2)
                    val classBuilder = TypeSpec.objectBuilder(fileName)

//                    classBuilder.addFunction(createBuildMethod(typeElement, element, fieldElements, valueElements))
//
//                    val file = FileSpec.builder(packageName, fileName)
//                        .addType(classBuilder.build())
//                        .build()
//                    file.writeTo(File(targetDirectory))
                    errorMessage { "rr" }
                    return true
                } else {
                    return true
                }
            }
        }
        errorMessage { "zxczxc" }
        return true
    }

    private fun createBuildMethod(typeElement: TypeElement, modelElement: TypeElement, fieldElements: List<Element>, valueElements: List<Element>): FunSpec {
        return FunSpec.builder("build").apply {
            addParameter("entity", asKotlinTypeName(typeElement))
            val fieldNullCheck = StringBuilder()
            for (field in fieldElements) {
                fieldNullCheck.append("requireNotNull(entity.$field)").appendLine()
            }
            addCode(fieldNullCheck.toString())
        }.build()
    }
//            .returns(ClassName("${modelElement.enclosingElement}", "${modelElement.simpleName}"))
//            .apply {
//                val code = StringBuilder()
//                val iterator = fieldElements.listIterator()
//                repeat((fieldElements.indices).count()) {
//                    val field = fieldElements[it]
//                    val value = valueElements[it]
//                    code.appendLine()
//                    code.append("\t$value = entity.$field")
//                    if (iterator.hasNext()) {
//                        code.append(",")
//                    }
//                }
//                addCode(
//                    """
//                    |return ${modelElement.simpleName}($code
//                    |)
//                """.trimMargin()
//                )
//            }
//            .build()
//    }

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
}