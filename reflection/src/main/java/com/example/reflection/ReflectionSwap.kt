package com.example.reflection

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class ReflectionSwap(
    val name: String
)