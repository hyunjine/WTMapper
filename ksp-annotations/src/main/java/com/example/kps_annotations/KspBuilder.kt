package com.example.kps_annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class KspBuilder(
    val name: String = "",
    val deprecated: Boolean = false
) {
    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.PROPERTY)
    annotation class Default(val value: String)
}