package com.example.kps_annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class KspLink(
    val kClass: KClass<*>,
    val deprecated: Boolean = false
) {
    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.PROPERTY)
    annotation class Default(val value: String)
}