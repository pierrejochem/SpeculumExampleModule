# Speculum Example Module

A standalone, independently buildable reference for writing an **external module**
for the [Speculum smart-mirror dashboard](https://github.com/pierrejochem/SpeculumSmartMirror).

It builds to a thin JAR that the Speculum app discovers at runtime — no app
rebuild, no source checkout of the mirror required. It demonstrates the full
module API: config reading, the `start()` / `refresh()` / `stop()` lifecycle,
the notification bus, Compose `Content()`, and packaging as a `ServiceLoader`
plugin.

The module appears at `top_center` and shows a live "Refreshes:" counter and the
last notification it received.

## How it works

- The module compiles against the **published** plugin API
  `org.speculum:mirror-api` (resolved from GitHub Packages) as `compileOnly`, so
  the built JAR contains only this module's own classes. Compose and the API are
  provided by the host app at runtime via the parent classloader.
- It extends [`MirrorModule`](https://github.com/pierrejochem/SpeculumSmartMirror)
  and exposes a `ModuleFactory` declared in
  `src/main/resources/META-INF/services/org.speculum.core.ModuleFactory`.
- On startup the Speculum app scans its `plugins/*.jar`, loads each in a
  `URLClassLoader`, and finds factories via the JDK `ServiceLoader`.
- The factory declares its config options through `settingsSchema()`, so the
  Speculum admin console renders real controls for them. See
  [Module settings](#module-settings).

## Requirements

- JDK 17–21 (the bundled Gradle wrapper pins Gradle 9.5.1).
- A **GitHub personal access token** with the `read:packages` scope. The
  `mirror-api` package is public, but GitHub's Maven registry still requires a
  token to download.

Put the credentials in `~/.gradle/gradle.properties` (outside this repo):

```properties
gpr.user=<your-github-username>
gpr.token=<a-PAT-with-read:packages>
```

…or export them in your environment instead:

```bash
export GITHUB_ACTOR=<your-github-username>
export GITHUB_TOKEN=<a-PAT-with-read:packages>
```

## Releases

Every `v*` tag publishes the built JAR as a
[GitHub Release](https://github.com/pierrejochem/SpeculumExampleModule/releases).
The current release is
**[v1.1.0](https://github.com/pierrejochem/SpeculumExampleModule/releases/tag/v1.1.0)**,
which adds [`settingsSchema()`](#module-settings) and needs `mirror-api` 1.3.0
(Speculum v1.3.0 or newer).

Grabbing `SpeculumExampleModule.jar` from there and dropping it in a Speculum
install's `plugins/` folder is enough to run it — no token and no build needed,
since downloading a release asset does not go through GitHub Packages.

## Build

```bash
./gradlew jar
```

The JAR lands in `build/libs/SpeculumExampleModule.jar`. Confirm the thin-JAR
contract — it should contain only the `example` classes and the SPI service file:

```bash
unzip -l build/libs/SpeculumExampleModule.jar
```

## Deploy into a Speculum install

Copy the JAR into the Speculum app's `plugins/` folder. Point the
`deployToMirror` task at your checkout:

```bash
./gradlew deployToMirror -Pspeculum.pluginsDir=/path/to/Speculum/plugins
```

…or set `SPECULUM_PLUGINS_DIR` in the environment. With neither set, the JAR is
copied to `build/plugins/` so you can move it yourself.

## Run

Start the Speculum app pointed at that `plugins/` folder (see the Speculum repo
for run instructions). The example loads automatically and renders at
`top_center`.

## Module settings

The module reads two config keys, `greeting` and `tickStep`. Rather than leaving
the admin console to show them as raw key/value text rows, the factory declares
them with `settingsSchema()`:

```kotlin
override fun settingsSchema(): List<SettingSpec> =
    listOf(
        SettingSpec(
            key = "greeting",
            label = "Greeting",
            default = "Hello, Speculum!",
            help = "Headline text shown at the top of the module.",
        ),
        SettingSpec(
            key = "tickStep",
            label = "Tick step",
            type = SettingType.INT,
            default = "1",
            min = 1,
            max = 100,
            help = "How much the refresh counter advances on each refresh.",
        ),
    )
```

The console knows nothing about this module by name — it renders whatever the
schema returns, served through the config-server's `GET /api/modules`. Types are
`STRING`, `INT`, `BOOL`, `ENUM`, `TEXT`, `URL`, `IP` and `CUSTOM`; `advanced =
true` folds a rarely-touched option away. Keep each `default` equal to the
fallback the module code itself reads, since that is what the console shows as
the hint for an unset key — `defaultConfig()` is a separate thing, the starting
config a freshly added module gets.

The method has a default empty implementation, so a module that declares nothing
still works and simply keeps the raw key/value rows. Any key saved in a config
but absent from the schema also falls back to a raw row.

## Bumping the API version

`mirror-api` is released on every `v*` tag of the Speculum project; the version
is the tag without its leading `v`. Update `mirrorApi` in
[`gradle/libs.versions.toml`](gradle/libs.versions.toml) to compile against a
different release (current: `1.3.0`).
