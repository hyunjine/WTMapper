package com.example.wtmapper.linker

import com.example.mapper_processor.linker.KaptLink
import com.example.mapper_processor.linker.KaptSwap2

@KaptLink(LinkerEntity2::class)
data class LinkerModel2(
    @KaptSwap2("what?") val a: Long,
    val b: String,
    val c: String,
    val d: String
)