plugins {
    id("module.jvm")
}

dependencies {
    implementation(project(":runtime"))
    implementation(libs.ksp)
    implementation(libs.kotlinpoet.ksp)
}