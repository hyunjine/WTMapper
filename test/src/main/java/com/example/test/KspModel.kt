package com.example.test

import com.example.kps_annotations.KspBuilder
import com.example.kps_annotations.KspLink

@KspLink(deprecated = true)
data class KspModel(
//    val seq: Long,
    val title: String,
    val subTitle: String?,
//    val rank: Int,
    val content: String,
)