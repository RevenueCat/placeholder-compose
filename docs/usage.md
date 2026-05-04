# Basic Usage

The `placeholder` modifier displays an animated loading overlay over your content while it's being loaded. Add it to any composable and control its visibility with a boolean state.

```kotlin
var isLoading by remember { mutableStateOf(true) }

Text(
    text = "User Name",
    modifier = Modifier.placeholder(enabled = isLoading),
)
```

When `enabled` is `true`, the placeholder overlay appears with a smooth animation. When it becomes `false`, the actual content is revealed with an elegant fade-in transition.

## Customizing Colors

You can customize the placeholder background color to match your app's theme. The `color` parameter accepts any Compose `Color` value.

```kotlin
Text(
    text = "User Name",
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .placeholder(
            enabled = isLoading,
            color = Color.LightGray.copy(alpha = 0.5f),
        ),
)
```

!!! tip "Theming"

    Instead of repeating the same `color` argument at every call site, set it once via `ProvidePlaceholderTheme`. See [Theming](theming.md).

## Customizing Shapes

The placeholder can take any shape you want. By default it uses `RectangleShape`, but you can swap in any other `Shape` to match your UI.

```kotlin
Image(
    painter = painterResource(id = R.drawable.profile),
    contentDescription = "Profile picture",
    modifier = Modifier
        .size(80.dp)
        .placeholder(
            enabled = isLoading,
            color = Color.Gray,
            shape = CircleShape,
        ),
)
```

You can use any shape from Material or create custom shapes:

```kotlin
Box(
    modifier = Modifier
        .size(200.dp, 100.dp)
        .placeholder(
            enabled = isLoading,
            shape = RoundedCornerShape(16.dp),
        ),
)
```

## Next Steps

- [Highlight Effects](highlights.md) — built-in animations like shimmer, fade, pulse
- [Theming](theming.md) — set color/shape/highlight defaults for an entire scope
- [Text Skeleton](text-skeleton.md) — multi-line bars for paragraph-style content
