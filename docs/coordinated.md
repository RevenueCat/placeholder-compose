# Coordinated Shimmer

By default, every placeholder runs its own animation clock. In a list of skeleton items the highlights drift out of phase, and the screen reads as a noisy patchwork of independently-shimmering boxes.

`PlaceholderSurface` shares a single `PlaceholderCoordinator` with every descendant so the shimmer reads as one coordinated wave across the entire region.

## PlaceholderSurface

```kotlin
PlaceholderSurface {
    LazyColumn {
        items(20) {
            UserListItem(user = null, isLoading = true) // every row shimmers in sync
        }
    }
}
```

While inside a `PlaceholderSurface`, each highlight's own `animationSpec` is ignored — the surface's spec drives every placeholder. You can override the shared spec via the `animationSpec` parameter:

```kotlin
PlaceholderSurface(
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 2400, easing = LinearEasing),
        repeatMode = RepeatMode.Restart,
    ),
) {
    // ... content ...
}
```

## How It Works

A `PlaceholderSurface` does three things:

1. Creates a [`PlaceholderCoordinator`](https://revenuecat.github.io/placeholder-compose/api/) that owns a single `Animatable<Float>` running the supplied `animationSpec` indefinitely.
2. Publishes that coordinator via `LocalPlaceholderCoordinator`.
3. Each `Modifier.placeholder` (or `Modifier.placeholderText`) inside the scope reads the coordinator and uses its progress instead of starting a per-instance animation.

The result: every placeholder in the scope reads exactly the same `progress` at any frame, and per-instance highlight animations are suppressed entirely (saving CPU).

!!! note "Default spec"

    `DefaultPlaceholderCoordinatorSpec` is a 1700ms linear restart cycle that loosely matches the visual feel of `PlaceholderDefaults.shimmer`. Override it whenever your design calls for a different cadence.

## Composing with Theming

`PlaceholderSurface` composes cleanly with `ProvidePlaceholderTheme` — wrap them in either order to get themed, coordinated placeholders for an entire screen:

```kotlin
ProvidePlaceholderTheme(materialPlaceholderTheme()) {
    PlaceholderSurface {
        // Themed defaults + synchronized shimmer
        UserList()
    }
}
```
