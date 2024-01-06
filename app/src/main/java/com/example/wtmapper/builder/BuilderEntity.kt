package com.example.wtmapper.builder

import com.example.mapper_processor.builder.Builder

@Builder
data class BuilderEntity(
    val seq: Long?,
    val name: String?,
    val phoneNumber: String?,
    val birthday: String?
)