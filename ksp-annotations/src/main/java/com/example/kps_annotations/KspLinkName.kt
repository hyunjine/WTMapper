package com.example.kps_annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY)
annotation class KspLinkName(val target: String, val strategy: String = "", val custom: String = "")
