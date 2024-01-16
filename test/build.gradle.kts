plugins {
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
    alias(libs.plugins.kotlin.jvm)
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
    alias(libs.plugins.ksp)
    kotlin("kapt")
}

dependencies {
    implementation(libs.junit)
    implementation(project(":kapt-annotations"))
    kapt(project(":kapt-processor"))
    implementation(project(":ksp-annotations"))
    ksp(project(":ksp-processor"))
}