plugins {
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(kotlin("reflect"))
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
    // https://mvnrepository.com/artifact/org.mapstruct/mapstruct
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
}