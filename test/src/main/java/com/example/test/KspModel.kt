package com.example.test

import com.example.kps_annotations.KspBuilder
import com.example.kps_annotations.KspLink
import com.example.kps_annotations.KspLinkName

@KspLink(KspEntity::class)
data class KspModel(
    val title: String,
    @KspLinkName("thumbnail")
    val subTitle: String?,
    val content: String,
)