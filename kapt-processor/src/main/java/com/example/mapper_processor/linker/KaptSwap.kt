package com.example.mapper_processor.linker

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class KaptSwap(val name: String)
