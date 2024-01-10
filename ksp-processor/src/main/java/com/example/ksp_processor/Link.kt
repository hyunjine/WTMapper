package com.example.ksp_processor

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Link(
    val name: String = "",
    val deprecated: Boolean = false
)