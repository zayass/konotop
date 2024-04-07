pluginManagement {
    includeBuild("convention-plugins")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "konotop"
include(":runtime")
include(":integration-tests")
include(":compiler:ksp")
include(":compiler-plugin:kotlin")
