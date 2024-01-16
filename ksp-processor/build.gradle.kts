plugins {
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
    alias(libs.plugins.ksp)
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    ksp(libs.autoservice.ksp)
    implementation(project(":ksp-annotations"))
    implementation(kotlin("stdlib"))
    implementation(libs.ksp.api)
    implementation(libs.autoservice.annotations)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoetKsp)
}