package com.example.mapper_processor

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.SourceVersion
import javax.tools.Diagnostic

abstract class BaseAbstractProcessor: AbstractProcessor() {
    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
    protected fun noteMessage(callback: () -> String) {
        val msg = callback.invoke()
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, msg)
    }

    protected fun errorMessage(callback: () -> String) {
        val msg = callback.invoke()
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg)
    }
}