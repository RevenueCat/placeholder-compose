# Theming

Instead of repeating `color`, `shape`, and `highlight` arguments at every call site, you can provide a `PlaceholderTheme` that supplies defaults for every `Modifier.placeholder` and `Modifier.placeholderText` call in its scope. Explicitly passed arguments always override the theme.

## ProvidePlaceholderTheme

```kotlin
ProvidePlaceholderTheme(
    PlaceholderTheme(
        color = Color.LightGray,
        shape = RoundedCornerShape(8.dp),
        highlight = PlaceholderDefaults.shimmer,
    ),
) {
    // Every placeholder call inside this scope picks up the theme defaults
    Text("...", modifier = Modifier.placeholder(enabled = isLoading))
    Box(modifier = Modifier.size(80.dp).placeholder(enabled = isLoading))
}
```

The full surface is:

| Property | Default |
|----------|---------|
| `color` | `Color.Gray.copy(alpha = 0.35f)` |
| `shape` | `RectangleShape` |
| `highlight` | `PlaceholderDefaults.fade` |
| `placeholderFadeTransitionSpec` | `{ spring() }` |
| `contentFadeTransitionSpec` | `{ spring() }` |

`PlaceholderTheme.Default` exposes these so you can derive a theme that overrides only some properties.

## Material Theme Integration

For Material 3 apps, `materialPlaceholderTheme()` builds a theme derived from the current `MaterialTheme.colorScheme`, so placeholders track the active light/dark scheme automatically.

```kotlin
ProvidePlaceholderTheme(materialPlaceholderTheme()) {
    // Background uses MaterialTheme.colorScheme.surfaceVariant
    // Highlight is a shimmer colored from MaterialTheme.colorScheme.onSurface
    YourScreen()
}
```

You can also override the specific colors:

```kotlin
ProvidePlaceholderTheme(
    materialPlaceholderTheme(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        highlightColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp),
    ),
) {
    YourScreen()
}
```

## Reusing a Theme

If you want to capture the theme once and reuse it across multiple `ProvidePlaceholderTheme` boundaries, use `rememberMaterialPlaceholderTheme()`:

```kotlin
val theme = rememberMaterialPlaceholderTheme()

ProvidePlaceholderTheme(theme) { /* … */ }
ProvidePlaceholderTheme(theme) { /* … */ }
```

## Reading the Active Theme

If you need to read the current theme from inside a composable (for example, to apply it to a non-modifier draw), use the `LocalPlaceholderTheme` `CompositionLocal`:

```kotlin
val theme = LocalPlaceholderTheme.current
val barColor = theme.color
```
