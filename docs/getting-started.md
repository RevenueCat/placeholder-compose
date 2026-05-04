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
placeholder = "1.0.2"

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

## Supported Targets

The library targets:

- **Android**: API 24+
- **JVM Desktop**
- **iOS**: x64, arm64, simulator arm64
- **macOS**: x64, arm64

## Next Steps

- [Basic Usage](usage.md) — your first placeholder, color and shape customization
- [Highlight Effects](highlights.md) — built-in animations
- [Theming](theming.md) — set defaults once for an entire screen
