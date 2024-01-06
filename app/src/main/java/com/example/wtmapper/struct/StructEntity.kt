package com.example.wtmapper.struct

import com.example.wtmapper.reflection.ReflectionEntity

data class StructEntity(
    val seq: Long?,
    val name: String?,
    val phoneNumber: String?,
    val birthday: String?
) {
    companion object {
        fun empty(): ReflectionEntity {
            return ReflectionEntity(
                seq = 1998L,
                name = "양현진",
                phoneNumber = "010-8544-7118",
                birthday = "1998.01.30"
            )
        }
    }
}
