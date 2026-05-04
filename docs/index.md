# Overview

<p align="center">
  <img src="https://raw.githubusercontent.com/RevenueCat/placeholder-compose/main/previews/preview1.gif" width="270"/>
  <img src="https://raw.githubusercontent.com/RevenueCat/placeholder-compose/main/previews/preview2.gif" width="281"/>
</p>

**Placeholder** is a fully customizable skeleton-loading library for Jetpack Compose and Kotlin Multiplatform. Add a single modifier to any composable and get an animated placeholder that fades cleanly into the real content once it's ready.

## Key Features

- **Drop-in Modifier API**: `Modifier.placeholder(enabled = isLoading)` is all you need to start
- **Five Built-in Highlights**: shimmer, fade, pulse, light reveal, circular reveal
- **Fully Customizable**: control color, shape, animation spec, and fade transitions independently
- **Custom Highlights**: implement `PlaceholderHighlight` for any visual effect you want
- **Theming**: provide a `PlaceholderTheme` (or `materialPlaceholderTheme()`) once and every descendant call inherits the defaults
- **Coordinated Shimmer**: wrap a region in `PlaceholderSurface` and every placeholder shimmers in lockstep — no more out-of-phase noise across a list
- **Multi-line Text Skeleton**: `Modifier.placeholderText(lines = 3, ...)` renders text-shaped bars matching your `TextStyle`
- **Kotlin Multiplatform**: Android, JVM Desktop, iOS, and macOS targets

## Quick Start

[![Maven Central](https://img.shields.io/maven-central/v/com.revenuecat.purchases/placeholder.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.revenuecat.purchases%22%20AND%20a:%22placeholder%22)

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.revenuecat.purchases:placeholder:$version")
}
```

Then drop the modifier on any composable:

```kotlin
var isLoading by remember { mutableStateOf(true) }

Text(
    text = "User Name",
    modifier = Modifier.placeholder(
        enabled = isLoading,
        highlight = PlaceholderDefaults.shimmer,
    ),
)
```

When `enabled` is `true` the placeholder overlay appears with a smooth animation. When it flips to `false`, the actual content fades in.

For a full walkthrough see [Getting Started](getting-started.md), or jump straight to [Basic Usage](usage.md).

## Technical Background

If you're curious how this library leverages `Modifier.Node` for performance, read [Exploring Modifier.Node for creating custom Modifiers in Jetpack Compose](https://www.revenuecat.com/blog/engineering/compose-custom-modifier/) on the RevenueCat engineering blog.
