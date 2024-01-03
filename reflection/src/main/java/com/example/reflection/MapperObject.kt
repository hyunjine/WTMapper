package com.example.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object MapperObject {
    fun <T : Any> from(from: Any, toClass: KClass<T>): T? {
        val fromClass = from::class
        val fromParams = fromClass.memberProperties
        val toParams = toClass.primaryConstructor?.parameters

        val constructor = mutableMapOf<KParameter, Any?>()

        for (toParam in toParams.orEmpty()) {
            val name = toParam.name
            val annotationName = toParam.findAnnotation<Match>()?.name
            val matchParam = fromParams.find { it.name == name || it.name == annotationName }
            matchParam?.getter?.call(from)?.let { constructor[toParam] = it }
        }

        return toClass.primaryConstructor?.callBy(constructor)
    }
}
