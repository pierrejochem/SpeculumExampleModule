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

## Bumping the API version

`mirror-api` is released on every `v*` tag of the Speculum project; the version
is the tag without its leading `v`. Update `mirrorApi` in
[`gradle/libs.versions.toml`](gradle/libs.versions.toml) to compile against a
different release (current: `0.5.7`).
