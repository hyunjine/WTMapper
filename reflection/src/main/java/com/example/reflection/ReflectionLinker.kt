package com.example.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object ReflectionLinker {
    fun <T : Any> from(input: Any, output: KClass<T>): T? {
        val inputClass = input::class
        val inputProperties = inputClass.memberProperties
        val outputProperties = output.primaryConstructor?.parameters

        val constructor = mutableMapOf<KParameter, Any?>()

        for (outputProperty in outputProperties.orEmpty()) {
            val outputPropertyName = outputProperty.name
            val annotationName = outputProperty.findAnnotation<ReflectionSwap>()?.name
            val matchParam = inputProperties.find { it.name == annotationName || it.name == outputPropertyName}
            matchParam?.getter?.call(input)?.let { constructor[outputProperty] = it }
        }

        return output.primaryConstructor?.callBy(constructor)
    }
}
