package com.example.mapper_processor.kson

import com.example.mapper_processor.BaseAbstractProcessor
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement


@AutoService(Processor::class)
class KsonProcessor : BaseAbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Kson::class.java.name)
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Kson::class.java)
            .forEach {
                val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                generateClass(it, pack)
            }
        return true
    }

    private fun generateClass(klass: Element, pack: String) {
        val fileName = "${klass.simpleName}KsonUtil"
        val tripleQuote = "\"\"\""
        val body = mutableListOf<String>()
        klass.enclosedElements.forEach { element ->
            when(element.kind) {
                ElementKind.FIELD -> {
                    body.add(""""$element": ${'$'}$element""")
                }
                else -> { }
            }
        }
        val bodyString = body.joinToString(", ")
        val statement = """return $tripleQuote{$bodyString}$tripleQuote"""
        val file = FileSpec.builder(pack, fileName)
            .addFunction(FunSpec.builder("toJson")
                .returns(String::class)
                .receiver(klass.asType().asTypeName())
                .addStatement(statement)
                .build())
            .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$fileName.kt"))
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}