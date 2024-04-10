package konotop.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption


open class KonotopGradleExtension {
    var enabled: Boolean = true
}

class KonotopGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>) = true

    override fun getCompilerPluginId() = "konotop.compiler.plugin"

    override fun getPluginArtifact() = SubpluginArtifact(
        groupId = "konotop",
        artifactId = "kotlin-compiler-plugin"
    )

    override fun getPluginArtifactForNative() = getPluginArtifact()

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(KonotopGradleExtension::class.java)

        return project.provider {
            listOf(
                SubpluginOption("enabled", extension.enabled.toString())
            )
        }
    }

    override fun apply(target: Project): Unit = with(target) {
        extensions.create("konotop", KonotopGradleExtension::class.java)
    }
}