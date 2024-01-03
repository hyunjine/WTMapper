package com.example.reflection

import kotlin.reflect.KFunction

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class Match(
    val name: String
)