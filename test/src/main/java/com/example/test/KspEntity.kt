package com.example.test

import com.example.kps_annotations.KspBuilder

@KspBuilder
data class KspEntity(
        val seq: Long,
        val title: String,
        val subTitle: String?,
        val rank: Int,
        val content: String,
    )