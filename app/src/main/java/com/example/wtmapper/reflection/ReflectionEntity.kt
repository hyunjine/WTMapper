package com.example.wtmapper.reflection

data class ReflectionEntity(
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

