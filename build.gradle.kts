// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.detekt)
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.detekt.get().pluginId)
    }
    detekt {
        autoCorrect = true
        config.setFrom("$rootDir/config/detekt/detekt.yml")
    }
    dependencies {
        detektPlugins(rootProject.libs.detekt.formatting)
        detekt(rootProject.libs.detekt.cli)
        detekt(rootProject.libs.detekt.rules.compose)
    }
}