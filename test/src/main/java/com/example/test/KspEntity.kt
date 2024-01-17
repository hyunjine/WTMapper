package com.example.test

import com.example.kps_annotations.KspBuilder

@KspBuilder
data class KspEntity(
    val title: String,
    val subTitle: String,
    val content: String,
)