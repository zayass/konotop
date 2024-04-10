plugins {
    id("java-gradle-plugin")
    id("module.jvm")
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin.api)
    implementation(libs.kotlin.stdlib)
}

gradlePlugin {
    plugins {
        create("konotop") {
            id = "konotop.compiler.plugin"
            implementationClass = "konotop.gradle.plugin.KonotopGradlePlugin"
        }
    }
}