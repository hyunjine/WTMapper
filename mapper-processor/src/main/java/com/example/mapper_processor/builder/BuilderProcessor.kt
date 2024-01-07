package com.example.mapper_processor.builder

import com.example.mapper_processor.BaseAbstractProcessor
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import java.util.Locale
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter.fieldsIn
import javax.tools.Diagnostic

@AutoService(Processor::class)
class BuilderProcessor : BaseAbstractProcessor() {

    private val targetDirectory: String
        get() = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION]
            ?: throw IllegalStateException("Unable to get target directory")

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Builder::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.processingOver()) {
            noteMessage { "This round will not be subject to a subsequent round of annotation processing" }
        }

        return processAnnotation(roundEnv)
    }

    private fun processAnnotation(roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(Builder::class.java)
        if (elements.isEmpty()) {
            noteMessage { "Not able to find @${Builder::class.java.name} in this round $roundEnv" }
            return true
        }

        for (element in elements) {
            element as TypeElement
            when (element.kind) {
                ElementKind.CLASS -> writeForClass(element)
                else -> errorMessage { "The annotation is invalid for the element type ${element.simpleName}. Please add ${Builder::class.java.name} either on Constructor or Class" }
            }
        }

        return true
    }

    private fun writeForClass(element: TypeElement) {
        val packageName = "${element.enclosingElement}".lowercase(Locale.getDefault())
        val fileName = "${element.simpleName}Builder".replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        noteMessage { "Writing $packageName.$fileName" }

        val allMembers = processingEnv.elementUtils.getAllMembers(element)
        val fieldElements = fieldsIn(allMembers)

        noteMessage { "All members for $packageName : $fieldElements" }

        val classBuilder = TypeSpec.objectBuilder(fileName)
        val builderClass = ClassName(packageName, fileName)

        for (field in fieldElements) {
            val propertyName = field.toString()
            val type = ClassName("${element.enclosingElement}", "String")

            classBuilder.addProperty(createProperty(field, propertyName))
            classBuilder.addFunction(createSetterMethod(field, propertyName, builderClass))
        }

        classBuilder.addFunction(createBuildMethod(element, fieldElements))

        val file = FileSpec.builder(packageName, fileName)
            .addType(classBuilder.build())
            .build()
        file.writeTo(File(targetDirectory))
    }

    private fun asKotlinTypeName(element: Element): TypeName {
        return element.asType().asKotlinType()
    }

    private fun TypeMirror.asKotlinType(): TypeName {
        return when (this) {
            is PrimitiveType -> {
                noteMessage { "TypeMirror is PrimitiveType" }
                processingEnv.typeUtils.boxedClass(this).asKotlinClassName()
            }
            is DeclaredType -> {
                noteMessage { "TypeMirror is DeclaredType" }
                this.asTypeElement().asKotlinClassName()
            }
            else -> this.asTypeElement().asKotlinClassName()
        }
    }

    /** private var name: kotlin.String? = null */
    private fun createProperty(field: Element, propertyName: String): PropertySpec {
        return PropertySpec
            .builder(propertyName, asKotlinTypeName(field).copy(nullable = true))
            .addModifiers(KModifier.PRIVATE)
            .mutable()
            .initializer("null")
            .build()
    }

    /** public fun methodName(propertyName: String): AnimalBuilder = apply {
     *    this.propertyName = propertyName
     *  } */
    private fun createSetterMethod(field: Element, propertyName: String, className: ClassName): FunSpec {
        return FunSpec
            .builder(propertyName)
            .addParameter(name = propertyName, type = asKotlinTypeName(field).copy(nullable = false))
            .returns(className)
            .addCode(
                StringBuilder()
                    .append("return apply {\n")
                    .append("\tthis.$propertyName = $propertyName\n")
                    .append("}")
                    .toString()
            )
            .build()
    }


    /** public fun build(): Builder */
    private fun createBuildMethod(typeElement: TypeElement, fieldElements: List<Element>): FunSpec {
        return FunSpec.builder("build").apply {
            val fieldNullCheck = StringBuilder()
            for (field in fieldElements) {
                fieldNullCheck.append("requireNotNull($field)").appendLine()
            }
            addCode(fieldNullCheck.toString())
        }.returns(ClassName("${typeElement.enclosingElement}", "${typeElement.simpleName}"))
            .apply {
                val code = StringBuilder()
                val iterator = fieldElements.listIterator()
                while (iterator.hasNext()) {
                    val field = iterator.next()
                    code.appendLine()
                    code.append("\t$field = $field")
                    code.append("!!")
                    if (iterator.hasNext()) {
                        code.append(",")
                    }
                }
                addCode(
                    """
                    |return ${typeElement.simpleName}($code
                    |)
                """.trimMargin()
                )
            }
            .build()
    }

    /** Returns the [TypeElement] represented by this [TypeMirror]. */
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

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION = "kapt.kotlin.generated"
    }
}