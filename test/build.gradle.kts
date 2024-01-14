plugins {
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
    alias(libs.plugins.kotlin.jvm)
    kotlin("kapt")
}

dependencies {
    implementation(libs.junit)
    implementation(project(":kapt-annotations"))
    kapt(project(":kapt-processor"))
}