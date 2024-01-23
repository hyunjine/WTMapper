package com.example.kps_annotations

import kotlin.reflect.KClass

@Repeatable
annotation class KspLinkApp(val type: KClass<*>, val value: String)
