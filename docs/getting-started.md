# Getting Started

[![Maven Central](https://img.shields.io/maven-central/v/com.revenuecat.purchases/placeholder.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.revenuecat.purchases%22%20AND%20a:%22placeholder%22)

## Installation

### Gradle (Kotlin DSL)

Add the dependency to your **module**'s `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.revenuecat.purchases:placeholder:$version")
}
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation "com.revenuecat.purchases:placeholder:$version"
}
```

### Version Catalog

If you're using a version catalog, add the entry to `gradle/libs.versions.toml`:

```toml
[versions]
placeholder = "1.0.4"

[libraries]
compose-placeholder = { module = "com.revenuecat.purchases:placeholder", version.ref = "placeholder" }
```

Then reference it in your module:

```kotlin
dependencies {
    implementation(libs.compose.placeholder)
}
```

### Kotlin Multiplatform

For Kotlin Multiplatform projects, add the dependency to the `commonMain` source set:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.placeholder)
            }
        }
    }
}
```

## Supported Platforms

| Platform | Targets | Since |
|----------|---------|-------|
| Android | `minSdk 23` | 1.0.0 |
| JVM Desktop | `jvm("desktop")` | 1.0.0 |
| iOS | `iosArm64`, `iosX64`, `iosSimulatorArm64` | 1.0.0 |
| macOS | `macosArm64`, `macosX64` | 1.0.0 |
| Web | `js` (browser), `wasmJs` (browser) | 1.0.4 |

One dependency covers every target, and Gradle resolves the right artifact for you.

!!! tip "Try it in the browser"

    The [web playground](https://revenuecat.github.io/placeholder-compose/demo/) is a Wasm build of
    this library. Tweak every option live and copy the Kotlin it generates. Needs a browser with
    WebAssembly GC: Chrome 119+, Firefox 120+, or Safari 18.2+.

## Next Steps

- [Basic Usage](usage.md) — your first placeholder, color and shape customization
- [Highlight Effects](highlights.md) — built-in animations
- [Theming](theming.md) — set defaults once for an entire screen
