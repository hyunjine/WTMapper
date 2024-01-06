package com.example.wtmapper.reflection

import com.example.reflection.ReflectionSwap

data class ReflectionModel(
    val seq: Long,
    val name: String,
    val phoneNumber: String,
    @ReflectionSwap("birthday")
    val birth: String
)