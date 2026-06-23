rootProject.name = "SpeculumExampleModule"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // The Speculum plugin API (org.speculum:mirror-api) is published to
        // GitHub Packages. The package is public, but GitHub's Maven registry
        // still requires a token to download — a PAT with the `read:packages`
        // scope. Put `gpr.user`/`gpr.token` in ~/.gradle/gradle.properties, or
        // export GITHUB_ACTOR/GITHUB_TOKEN. See README.md.
        maven {
            url = uri("https://maven.pkg.github.com/pierrejochem/SpeculumSmartMirror")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.token").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
