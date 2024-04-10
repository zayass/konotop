plugins {
    id("module.jvm")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlin.stdlib)
}