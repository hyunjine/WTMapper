plugins {
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
    alias(libs.plugins.kotlin.jvm)
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
    kotlin("kapt")
}

dependencies {
    compileOnly(project(":kapt-annotations"))
    implementation(libs.autoservice)
    kapt(libs.autoservice)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlin.reflect)
}