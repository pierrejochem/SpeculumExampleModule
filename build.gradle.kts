import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("config/detekt/detekt.yml"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // The Speculum host app provides these at runtime (shared via the parent
    // classloader), so they are compile-only and the JAR stays thin — it ends
    // up containing only this module's own classes. `mirror-api` is the
    // published Speculum plugin API (see gradle/libs.versions.toml).
    compileOnly(libs.mirror.api)
    compileOnly(libs.compose.runtime)
    compileOnly(libs.compose.foundation)
    compileOnly(libs.compose.material3)
    compileOnly(libs.kotlinx.coroutines.core)
}

// Copy the built JAR into a Speculum install's `plugins/` folder so the app
// discovers it at startup. Point it at your checkout with
//   ./gradlew deployToMirror -Pspeculum.pluginsDir=/path/to/Speculum/plugins
// or the SPECULUM_PLUGINS_DIR env var. Defaults to build/plugins/ otherwise.
val deployToMirror by tasks.registering(Copy::class) {
    description = "Builds the module JAR and copies it into a Speculum install's plugins/ folder."
    from(tasks.named("jar"))
    into(
        providers.gradleProperty("speculum.pluginsDir").orNull
            ?: System.getenv("SPECULUM_PLUGINS_DIR")
            ?: layout.buildDirectory
                .dir("plugins")
                .get()
                .asFile.path,
    )
}
