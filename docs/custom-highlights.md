# Custom Highlights

For complete control over the highlight animation, you can either tune the built-in highlight implementations or implement the `PlaceholderHighlight` interface from scratch.

## Tuning the Built-in Shimmer

The `Shimmer` data class exposes parameters for color, animation timing, intensity, drop-off, and tilt:

```kotlin
val customShimmer = Shimmer(
    highlightColor = Color.Blue.copy(alpha = 0.6f),
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 1000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart,
    ),
    intensity = 0.2f, // brightness at the highlight center
    dropOff = 0.7f,   // size of the fading edge
    tilt = 30f,       // angle of the shimmer band, in degrees
)

Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .placeholder(
            enabled = isLoading,
            highlight = customShimmer,
        ),
)
```

## Tuning the Built-in Fade

Similarly, `Fade` can be customized via color and animation spec:

```kotlin
val customFade = Fade(
    highlightColor = Color.White.copy(alpha = 0.8f),
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 1000),
        repeatMode = RepeatMode.Reverse,
    ),
)

Text(
    text = "Custom fade effect",
    modifier = Modifier.placeholder(
        enabled = isLoading,
        highlight = customFade,
    ),
)
```

`Pulse`, `LightReveal`, and `CircularReveal` accept similar parameters — see [their KDoc](https://revenuecat.github.io/placeholder-compose/api/) for the full list.

## Fully Custom Highlight

For total customization, implement the `PlaceholderHighlight` interface. You decide the brush and alpha at every point in the animation cycle.

```kotlin
@Stable
private class RainbowShimmer : PlaceholderHighlight {
    override val animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 2000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart,
    )

    override fun brush(progress: Float, size: Size): Brush {
        val colors = listOf(
            Color.Red, Color.Yellow, Color.Green,
            Color.Cyan, Color.Blue, Color.Magenta, Color.Red,
        )
        val startX = -size.width + (size.width * 2 * progress)
        return Brush.horizontalGradient(
            colors = colors,
            startX = startX,
            endX = startX + size.width,
        )
    }

    override fun alpha(progress: Float): Float = 1.0f
}

// Usage
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .placeholder(
            enabled = isLoading,
            highlight = RainbowShimmer(),
        ),
)
```

!!! note "Coordinator and animationSpec"

    When a custom highlight is used inside a [`PlaceholderSurface`](coordinated.md), the highlight's own `animationSpec` is ignored — the surface's spec drives every placeholder. The `brush` and `alpha` functions are still called normally with the shared progress.
