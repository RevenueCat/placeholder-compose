# Transitions

The `placeholder` modifier crossfades between the placeholder overlay and the actual content when `enabled` flips. You can customize both fades independently.

```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .placeholder(
            enabled = isLoading,
            placeholderFadeTransitionSpec = {
                tween(durationMillis = 500)
            },
            contentFadeTransitionSpec = {
                tween(durationMillis = 800, easing = FastOutSlowInEasing)
            },
        ),
)
```

| Parameter | Controls |
|-----------|----------|
| `placeholderFadeTransitionSpec` | How the placeholder layer fades in/out |
| `contentFadeTransitionSpec` | How the actual content fades in/out |

Both default to a `spring()` animation. Pass `tween`, `snap`, or any other `FiniteAnimationSpec<Float>` to customize.

!!! tip "Theming"

    Both transition specs can also be set as defaults via [`PlaceholderTheme`](theming.md), so you don't have to repeat them at every call site.
