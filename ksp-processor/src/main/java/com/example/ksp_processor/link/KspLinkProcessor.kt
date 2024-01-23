/*
 * Copyright (c) 2022 Toast Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.ksp_processor.link

import com.example.kps_annotations.KspLink
import com.example.kps_annotations.KspLinkApp
import com.example.kps_annotations.KspLinkName
import com.example.kps_annotations.KspLinkTest
import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * SymbolProcessor는 플러그인이 Kotlin Symbol Processing에 통합하기 위해 사용하는 인터페이스입니다.
 * SymbolProcessor는 여러 라운드의 실행을 지원하며, 프로세서는 매 라운드가 끝날 때마다 지연된 심볼 목록을 반환할 수 있으며,
 * 이는 새로 생성된 심볼과 함께 다음 라운드에서 다시 프로세서에 전달됩니다.
 * 예외인 경우, KSP는 KSP로부터의 예외와 프로세서로부터의 예외를 구분하려고 합니다.
 * 프로세서로부터의 예외는 즉시 처리를 종료하고 KSPLogger에 오류로 기록됩니다.
 * KSP로부터의 예외는 추가 조사를 위해 KSP 개발자에게 보고되어야 합니다.
 * 예외 또는 오류가 발생한 라운드가 끝날 때 모든 프로세서는 onError() 함수를 호출하여 자체적으로 오류를 처리합니다.
 */
class KspLinkProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    /**
     * 처리 작업을 실행하기 위해 Kotlin Symbol Processing에서 호출합니다.
     * Class 타입 중에 KspLink annotation이 달려있는 Class들을 필터링 합니다.
     * @param resolver Symbol Processor에게 Symbol 등 컴파일러 세부 정보에 대한 액세스 권한을 제공합니다.
     * @return 프로세서에서 처리할 수 없는 지연된 심볼 목록입니다. 이 라운드에서 처리할 수 없는 심볼만 반환되어야 합니다.
     * 컴파일된 코드(라이브러리)의 심볼은 항상 유효하며, 지연 목록에서 반환되면 무시됩니다.
     */
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotateds1 = resolver
            .getSymbolsWithAnnotation<KspLinkTest>()
            .filterIsInstance<KSClassDeclaration>()

        annotateds1.forEach {
            it.getAllFunctions().forEach {
                logger.d(it.simpleName.asString())
            }

        }
        val annotateds = resolver
            .getSymbolsWithAnnotation<KspLinkApp>()
            .filterIsInstance<KSClassDeclaration>()
        annotateds.forEach { cls ->
            cls.getAnnotationsByType(KspLinkApp::class).forEach { a ->
                runCatching {
                    a.type to a.value
                }.onSuccess {
                    error("${cls.simpleName} property is not member of KClass")
                }.onFailure { e ->
                    if (e is KSTypeNotPresentException) {
                        val declaration = e.ksType.declaration
                        if (declaration is KSClassDeclaration) {
                            logger.d("${declaration} ${a.value}")
                        } else {
                            error("${declaration.simpleName} property is not member of Class")
                        }
                    } else {
                        error(e.message.toString())
                    }
                }
            }
        }


        val annotated = resolver
            .getSymbolsWithAnnotation<KspLink>()
            .filterIsInstance<KSClassDeclaration>()

        for (item in annotated) {
            filterKClass(item)
        }

        return emptyList()
    }


    @OptIn(KspExperimental::class)
    private fun filterKClass(cls: KSClassDeclaration) {
        runCatching {
            cls.getAnnotationsByType(KspLink::class).first().kClass
        }.onSuccess {
            error("${cls.simpleName} property is not member of KClass")
        }.onFailure { e ->
            if (e is KSTypeNotPresentException) {
                val declaration = e.ksType.declaration
                if (declaration is KSClassDeclaration) {
                    parseKSClassDeclaration(declaration, cls)
                } else {
                    error("${declaration.simpleName} property is not member of Class")
                }
            } else {
                error(e.message.toString())
            }
        }

    }

    /**
     * KSClassDeclaration의 생성자가 요구하는 Data Class의 형식인지 판단하는 함수입니다.
     * @param input Link annotation의 ksClass 프로퍼티로 변환 할 대상자 입니다.
     * 인스턴스로 되어있어 각 프로퍼티들에 값이 할당되어 있습니다.
     * @param output ksClass로 들어온 객체를 변환시킬 타입입니다.
     */
    @OptIn(KspExperimental::class)
    private fun parseKSClassDeclaration(input: KSClassDeclaration, output: KSClassDeclaration) {
        val inputClassName = input.toClassName()
        val linkerClassName = ClassName(output.packageName.asString(), output.simpleName.asString() + "Linker")
        val outputProperties = output.getAllProperties()

        FileSpec.builder(linkerClassName).toastIndent()
            .addFileComment("This code was generated by ksp-builder-gen. Do not modify.")
            .addType(
                TypeSpec.classBuilder(linkerClassName).apply {

                    val classNames = output.toClassName()
                    val code = TestList()
                    for (outputProperty in outputProperties) {
                        val a = input.getAllProperties().find { it.simpleName.asString() == outputProperty.simpleName.asString() }


                        val linkName = outputProperty.getAnnotationsByType(KspLinkName::class).firstOrNull()


                        val custom = linkName?.custom
                        val target = linkName?.target
                        val strategy = linkName?.strategy


                        if (!custom.isNullOrBlank()) {
                            val name = outputProperty.simpleName.asString()
                            code.submit("$name = $custom")
                            continue
                        }

                        if (!target.isNullOrBlank()) {
                            val name = outputProperty.simpleName.asString()
                            code.submit("$name = entity.${target}$strategy")
                        } else {
                            val name = outputProperty.simpleName.asString()
                            code.submit("$name = entity.$name")
                        }

                    }

                    val function1 = FunSpec.builder("build").apply {
                        returns(classNames)
                        addParameter("entity", inputClassName)
                        addStatement("return %T($code)", classNames)
                    }.build()
                    addFunction(function1)
                }.build()
            )
            .build()
            .writeTo(codeGenerator, Dependencies(aggregating = false, input.containingFile!!))
    }

    /**
     * Linker Class를 생성하는 함수입니다.
     * @param kcd output이 인자로 전달됩니다.
     */
    private fun getLinker(kcd: KSClassDeclaration): ClassInfo {
        return ClassInfo(
            packageName = kcd.packageName.asString(),
            simpleName = "${kcd.simpleName}Linker"
        )
    }

    private fun FileSpec.Builder.toastIndent() = indent(" ".repeat(4))

    private inline fun <reified T : Annotation> Resolver.getSymbolsWithAnnotation() =
        getSymbolsWithAnnotation(T::class.qualifiedName!!)

    private fun KSPLogger.d(msg: Any?) {
        warn("---------------------------------------------------------------------------------------")
        warn("Message: $msg")
        warn("---------------------------------------------------------------------------------------")
    }

    class TestList {
        private var list: String = "\n"
        fun submit(item: String) {
            list += "${item}, \n"
        }

        override fun toString(): String {
            return list.dropLast(3).plus("\n")
        }
    }
}
