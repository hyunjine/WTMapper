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

package com.example.ksp_processor.builder

import com.example.kps_annotations.KspBuilder
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

class KspBuilderGenerator(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotated = resolver.getSymbolsWithAnnotation<KspBuilder>()
            .filterIsInstance<KSClassDeclaration>()

        for (item in annotated) {
            generateBuilderClass(item)
        }

        return emptyList()
    }

    private fun generateBuilderClass(decl: KSClassDeclaration) {
        val classDescriptor = SimpleClassDescriptor.fromDeclaration(decl)

        val builderClassName = ClassName(classDescriptor.packageName, classDescriptor.builderName)

        FileSpec.builder(classDescriptor.packageName, classDescriptor.builderName).toastIndent()
            .addFileComment("This code was generated by ksp-builder-gen. Do not modify.")
            .addType(
                TypeSpec.classBuilder(builderClassName).apply {
                    if (classDescriptor.deprecated) {
                        addAnnotation(
                            AnnotationSpec.builder(Deprecated::class)
                                .addMember("message = %S", "Prefer to construct ${classDescriptor.simpleName} directly")
                                .build()
                        )
                    }

                    for (prop in classDescriptor.properties) {
                        addProperty(prop.makeSpec())
                        addFunction(prop.makeSetter(builderClassName))

                        if (prop is ContainerPropertyDescriptor) {
                            if (prop.type.isNullable) {
                                addFunction(prop.makeInitializerMethod())
                            }

                            addFunction(prop.makeInsertMethod(builderClassName))
                            addFunction(prop.makeInsertAllMethod(builderClassName))
                        }
                    }

                    primaryConstructor(FunSpec.constructorBuilder().build())
                    addFunction(classDescriptor.makeCopyConstructor())
                    addFunction(classDescriptor.makeBuildMethod())
                }.build()
            )
            .build().writeTo(codeGenerator, Dependencies(aggregating = false, decl.containingFile!!))
    }

    private fun PropertyDescriptor.makeSpec() =
        PropertySpec.builder(name, builderType).mutable()
            .addModifiers(KModifier.PRIVATE).initializer(initialValue).build()

    private fun PropertyDescriptor.makeSetter(thisName: ClassName) =
        FunSpec.builder(name).addParameter(name, type).returns(thisName)
            .addStatement("this.$name = $name$fromObjectConverter")
            .returnThis()

    private fun ContainerPropertyDescriptor.makeInitializerMethod() =
        FunSpec.builder(initMethodName).addModifiers(KModifier.PRIVATE)
            .returns(builderType.copy(nullable = false))
            .beginControlFlow("if (this.$name == null)")
            .addStatement("this.$name = ${containerSpec.initializer}")
            .endControlFlow()
            .addStatement("return this.$name!!")
            .build()

    private fun ContainerPropertyDescriptor.makeInsertAllMethod(className: ClassName) =
        FunSpec.builder(insertAllMethodName).returns(className)
            .addParameter(name, containerSpec.insertAllType.parameterizedBy(parameters))
            .addStatement("this.$internalAccessor.${containerSpec.insertAllName}($name)")
            .returnThis()

    private fun ContainerPropertyDescriptor.makeInsertMethod(className: ClassName) =
        FunSpec.builder(insertMethodName).returns(className)
            .addParameters(containerSpec.insertParams.zip(parameters, ::ParameterSpec))
            .addStatement("this.$internalAccessor.${containerSpec.insertName}(${containerSpec.insertParams.joinToString()})")
            .returnThis()

    private fun SimpleClassDescriptor.makeBuildMethod(): FunSpec {
        val className = ClassName(packageName, simpleName)

        val constructorParams = properties.joinToString {
            "${it.name} = " + if (it.type.isNullable || it is ContainerPropertyDescriptor) {
                it.name
            } else {
                """
                        checkNotNull(${it.name}) { 
                            "required property ${it.name} is not set" 
                        }
                """.trimIndent()
            }
        }

        return FunSpec.builder("build").returns(className)
            .addStatement("return %T($constructorParams)", className)
            .build()
    }

    private fun SimpleClassDescriptor.makeCopyConstructor() =
        FunSpec.constructorBuilder().callThisConstructor()
            .addParameter("o", ClassName(packageName, simpleName)).apply {
                for (prop in properties) {
                    addStatement("this.${prop.name} = o.${prop.name}${prop.fromObjectConverter}")
                }
            }.build()

    private fun FunSpec.Builder.returnThis() = addStatement("return this").build()

    private fun FileSpec.Builder.toastIndent() = indent(" ".repeat(4))

    private inline fun <reified T : Annotation> Resolver.getSymbolsWithAnnotation() =
        getSymbolsWithAnnotation(T::class.qualifiedName!!)
}
