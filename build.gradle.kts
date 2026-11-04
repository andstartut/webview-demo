plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services) apply false
}

detekt {
    config.setFrom("$projectDir/config/detekt/detekt-config.yml")
    buildUponDefaultConfig = true
    allRules = false
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        config.setFrom("$rootDir/config/detekt/detekt-config.yml")
        buildUponDefaultConfig = true
    }
}
