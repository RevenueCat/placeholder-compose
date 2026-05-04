# Highlight Effects

The library provides five pre-built highlight animations in `PlaceholderDefaults`. Each effect creates a different visual indication that content is loading. Pass any of them via the `highlight` parameter.

## Shimmer

The shimmer effect sweeps a bright gradient across the placeholder — the most common loading indicator in modern UIs.

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .placeholder(
            enabled = isLoading,
            highlight = PlaceholderDefaults.shimmer,
        ),
) {
    // Your content here
}
```

## Fade

A subtle breathing animation — the highlight fades in and out repeatedly.

```kotlin
Text(
    text = "Loading...",
    modifier = Modifier.placeholder(
        enabled = isLoading,
        highlight = PlaceholderDefaults.fade,
    ),
)
```

## Pulse

The pulse effect rhythmically brightens and dims the highlight, creating a pulsating feel.

```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .placeholder(
            enabled = isLoading,
            highlight = PlaceholderDefaults.pulse,
        ),
)
```

## Circular Reveal

A ripple effect that expands from the center of the placeholder.

```kotlin
Image(
    painter = placeholderPainter,
    contentDescription = null,
    modifier = Modifier
        .size(150.dp)
        .placeholder(
            enabled = isLoading,
            highlight = PlaceholderDefaults.circularReveal,
            shape = CircleShape,
        ),
)
```

## Light Reveal

A liquid, flowing wave of light moving across the placeholder.

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .placeholder(
            enabled = isLoading,
            highlight = PlaceholderDefaults.lightReveal,
        ),
)
```

## Disabling Animation

If you prefer a static placeholder with no animation, pass `null` for `highlight`.

```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .placeholder(
            enabled = isLoading,
            highlight = null,
        ),
)
```

## Next Steps

- [Custom Highlights](custom-highlights.md) — tweak built-in highlight parameters or implement `PlaceholderHighlight` from scratch
- [Coordinated Shimmer](coordinated.md) — synchronize highlight phase across multiple placeholders
