/*
 * Copyright (c) 2025 RevenueCat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.revenuecat.placeholder

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

/**
 * Theme-level defaults for the [placeholder] modifier.
 *
 * Provide a [PlaceholderTheme] via [ProvidePlaceholderTheme] (or by writing to
 * [LocalPlaceholderTheme] directly) so that any descendant `Modifier.placeholder(...)`
 * call without explicit arguments picks up these values. Explicitly passed arguments
 * always win over the theme.
 *
 * @property color Default background color of the placeholder.
 * @property shape Default shape of the placeholder.
 * @property highlight Default highlight animation. `null` disables the highlight.
 * @property placeholderFadeTransitionSpec Default fade-in/out spec for the placeholder layer.
 * @property contentFadeTransitionSpec Default fade-in/out spec for the wrapped content.
 */
@Immutable
public class PlaceholderTheme(
  public val color: Color = Color.Gray.copy(alpha = 0.35f),
  public val shape: Shape = RectangleShape,
  public val highlight: PlaceholderHighlight? = PlaceholderDefaults.fade,
  public val placeholderFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = { spring() },
  public val contentFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = { spring() },
) {
  public companion object {
    /** The neutral default used when no theme is provided. Matches the legacy modifier defaults. */
    public val Default: PlaceholderTheme = PlaceholderTheme()
  }
}

/**
 * CompositionLocal that supplies the active [PlaceholderTheme] to descendant
 * `Modifier.placeholder` calls. Defaults to [PlaceholderTheme.Default].
 */
public val LocalPlaceholderTheme: ProvidableCompositionLocal<PlaceholderTheme> =
  staticCompositionLocalOf { PlaceholderTheme.Default }

/**
 * Provides [theme] to all `Modifier.placeholder(...)` calls inside [content].
 *
 * Example:
 * ```kotlin
 * ProvidePlaceholderTheme(materialPlaceholderTheme()) {
 *   // every placeholder under here picks up Material colors automatically
 *   MyScreen()
 * }
 * ```
 */
@Composable
public fun ProvidePlaceholderTheme(
  theme: PlaceholderTheme,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(LocalPlaceholderTheme provides theme, content = content)
}

/**
 * Builds a [PlaceholderTheme] derived from the current [MaterialTheme].
 *
 * Background uses [MaterialTheme.colorScheme.surfaceVariant] and the default highlight
 * is a [Shimmer] colored with [MaterialTheme.colorScheme.onSurface] at 30% opacity, so
 * placeholders track the active light/dark color scheme automatically.
 */
@Composable
@ReadOnlyComposable
public fun materialPlaceholderTheme(
  color: Color = MaterialTheme.colorScheme.surfaceVariant,
  highlightColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
  shape: Shape = RectangleShape,
): PlaceholderTheme = PlaceholderTheme(
  color = color,
  shape = shape,
  highlight = Shimmer(highlightColor = highlightColor),
)

/**
 * Remembered variant of [materialPlaceholderTheme] for cases where you want to capture
 * the theme once and reuse it (e.g. across multiple `ProvidePlaceholderTheme` boundaries).
 */
@Composable
public fun rememberMaterialPlaceholderTheme(
  color: Color = MaterialTheme.colorScheme.surfaceVariant,
  highlightColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
  shape: Shape = RectangleShape,
): PlaceholderTheme = remember(color, highlightColor, shape) {
  PlaceholderTheme(
    color = color,
    shape = shape,
    highlight = Shimmer(highlightColor = highlightColor),
  )
}
