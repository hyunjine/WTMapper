package com.example.wtmapper.kson

import com.example.mapper_processor.kson.Kson

@Kson
data class KsonEntity(
    val seq: Long?,
    val name: String?,
    val phoneNumber: String?,
    val birthday: String?
)
