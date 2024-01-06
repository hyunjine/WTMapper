package com.example.wtmapper.linker

import com.example.mapper_processor.linker.Link

@Link(LinkerEntity::class)
data class LinkerModel(
    val seq: Long,
    val name: String,
    val phoneNumber: String,
    val birthday: String
)