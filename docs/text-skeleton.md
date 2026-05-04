# Text Skeleton

`Modifier.placeholder` always renders a single rectangle — fine for icons and avatars, but for paragraph-style text it produces one large block instead of separate line bars. `Modifier.placeholderText` solves this by drawing `lines` skeleton bars sized to the supplied `TextStyle`, with the last bar narrowed by `lastLineFraction` for a natural ragged ending.

## Basic Usage

```kotlin
Text(
    text = body ?: "",
    style = MaterialTheme.typography.bodyLarge,
    modifier = Modifier
        .fillMaxWidth()
        .placeholderText(
            enabled = body == null,
            lines = 3,
            lastLineFraction = 0.6f,
            style = MaterialTheme.typography.bodyLarge,
        ),
)
```

## Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `enabled` | `true` | When `true`, draws skeleton bars; when `false`, the host's content is shown. |
| `lines` | `3` | Number of bars to draw. |
| `lastLineFraction` | `0.6f` | Width of the final bar relative to the host width, in `0f..1f`. |
| `style` | `LocalTextStyle.current` | Text style used to derive line height and bar height. |
| `color` / `shape` / `highlight` | `LocalPlaceholderTheme.current.*` | Same theme-driven defaults as `Modifier.placeholder`. |

## Layout Behavior

While `enabled` is `true`, the host is forced to reserve at least `lines × lineHeight` of vertical space. This is what lets you attach the modifier to an empty `Text("")` or a `null`-content composable and still display the full skeleton.

When `enabled` flips to `false`, the minimum-height enforcement is dropped and the host collapses to its natural size — the bars fade away and the actual content takes over.

## Resolving Line Height

The modifier reads `style.lineHeight` and `style.fontSize` and converts them to pixels via `LocalDensity`. Specifically:

- If `style.lineHeight` is in `sp`, it's used directly.
- If `style.lineHeight` is in `em` and `style.fontSize` is in `sp`, line height is computed as `fontSize × emValue`.
- If `style.lineHeight` is unspecified, it falls back to `fontSize × 1.4` — the multiplier most font systems use as a sensible default.

## Composing with Theming and Coordination

`placeholderText` reads the same defaults from `LocalPlaceholderTheme` and participates in `PlaceholderSurface` coordination, so it composes cleanly with the rest of the API:

```kotlin
ProvidePlaceholderTheme(materialPlaceholderTheme()) {
    PlaceholderSurface {
        Column {
            Text(
                text = title ?: "",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.placeholderText(
                    enabled = title == null,
                    lines = 1,
                    style = MaterialTheme.typography.titleMedium,
                ),
            )
            Text(
                text = body ?: "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.placeholderText(
                    enabled = body == null,
                    lines = 3,
                    lastLineFraction = 0.5f,
                    style = MaterialTheme.typography.bodyMedium,
                ),
            )
        }
    }
}
```

In this example every bar — title and body, across both `Text` composables — shimmers in lockstep with surrounding placeholders, using the active Material color scheme.
