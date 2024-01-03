package com.example.mapper_processor

import com.example.mapper_annotation.Mapper
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("com.example.mapper_annotation.Mapper") // 여기에는 사용자 정의 애노테이션의 패키지 경로가 들어갑니다.
class MapperProcessor : AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        roundEnv?.getElementsAnnotatedWith(Mapper::class.java)?.forEach { element ->
            if (element is ExecutableElement) {
                generateMethod(element)
            }
        }
        return true
    }

    private fun generateMethod(element: ExecutableElement) {
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
        val className = element.enclosingElement.simpleName.toString()

        val generatedClassName = ClassName(packageName, "${className}Generated")

        val fileSpec = FileSpec.builder(packageName, generatedClassName.simpleName)
            .addFunction(generateTestMethod(element))
            .build()

        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        fileSpec.writeTo(File(kaptKotlinGeneratedDir, "${generatedClassName.simpleName}.kt"))
    }

    private fun generateTestMethod(element: ExecutableElement): FunSpec {
        val methodName = element.simpleName.toString()
        val returnType = element.returnType.toString()

        return FunSpec.builder("${methodName}Generated")
            .returns(String::class)
            .addParameter("age", Int::class)
            .addStatement("val name = \"이이이\"")
            .addStatement("return \$name \$age")
            .build()
    }
}
