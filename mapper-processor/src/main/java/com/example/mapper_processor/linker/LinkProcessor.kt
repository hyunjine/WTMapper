package com.example.mapper_processor.linker

import com.example.mapper_processor.BaseAbstractProcessor
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.BYTE
import com.squareup.kotlinpoet.CHAR
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.SHORT
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
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
import javax.lang.model.util.SimpleTypeVisitor8


@AutoService(Processor::class)
class LinkProcessor : BaseAbstractProcessor() {
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Link::class.java.name)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(Link::class.java)
        if (elements.isEmpty()) {
            noteMessage { "Not able to find @${Link::class.java.name} in this round $roundEnv" }
            return true
        }

        for (element in elements) {
            element as TypeElement
            val annotation = element.getAnnotation(Link::class.java)

            runCatching {
                annotation.kClass
            }.onSuccess { param ->
                errorMessage { "1 $param" }
            }.onFailure { exception ->
                if (exception is MirroredTypeException) {
                    val typeMirror = exception.typeMirror.asTypeName()
                    typeMirror.copy()

                }
            }
            return true
        }
    }

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

}