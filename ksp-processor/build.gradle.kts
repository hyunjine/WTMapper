@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.ksp)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinPoetKsp)
    implementation(libs.ksp.api)
}