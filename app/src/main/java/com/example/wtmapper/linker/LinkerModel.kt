package com.example.wtmapper.linker

import com.example.mapper_processor.linker.KaptLink
import com.example.mapper_processor.linker.KaptSwap2

@KaptLink(LinkerEntity::class)
data class LinkerModel(
    @KaptSwap2("dd") val seq: Long,
    val name: String,
    val phoneNumber: String,
    val birthday: String
)