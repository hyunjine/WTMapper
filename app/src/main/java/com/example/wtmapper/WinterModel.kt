package com.example.wtmapper

import com.example.ksp_link_gen_annotations.Link
import com.example.ksp_link_gen_annotations.Swap

@Link
class WinterModel(
    val name: String,
    val email: String?,
    val seq: Long,
    @Swap("true")
    val active: Boolean = true
)
