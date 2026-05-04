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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density

/**
 * Draws a placeholder effect over content while it is loading.
 *
 * This modifier displays an animated placeholder overlay that covers the content
 * when [enabled] is true, and smoothly transitions to reveal the actual content
 * when [enabled] becomes false. It's commonly used to indicate loading states
 * for images, text, and other UI elements.
 *
 * The placeholder can be customized with different colors, shapes, and highlight
 * animations. Several pre-built highlight effects are available in [PlaceholderDefaults],
 * including shimmer, fade, pulse, circular reveal, and light reveal.
 *
 * Example usage:
 * ```kotlin
 * // Basic placeholder with default fade effect
 * Text(
 *   text = userName,
 *   modifier = Modifier.placeholder(visible = isLoading)
 * )
 *
 * // Customized placeholder with shimmer effect
 * Image(
 *   painter = profileImage,
 *   contentDescription = "Profile",
 *   modifier = Modifier
 *     .size(100.dp)
 *     .placeholder(
 *       visible = isLoading,
 *       color = Color.LightGray,
 *       shape = CircleShape,
 *       highlight = PlaceholderDefaults.shimmer
 *     )
 * )
 *
 * // Placeholder without highlight animation
 * Card(
 *   modifier = Modifier.placeholder(
 *     visible = isLoading,
 *     highlight = null
 *   )
 * )
 * ```
 *
 * @param enabled Whether the placeholder should be visible. When false, the actual content is shown.
 * @param color The background color of the placeholder. Defaults to a semi-transparent gray.
 * @param shape The shape of the placeholder. Defaults to [RectangleShape].
 * @param highlight The highlight animation effect to apply. Defaults to [PlaceholderDefaults.fade].
 *   Set to null to disable highlight animations.
 * @param placeholderFadeTransitionSpec Animation spec for fading the placeholder in/out.
 *   Defaults to a spring animation.
 * @param contentFadeTransitionSpec Animation spec for fading the content in/out.
 *   Defaults to a spring animation.
 * @return A [Modifier] that draws the placeholder effect
 * @see PlaceholderDefaults for pre-configured highlight effects
 * @see PlaceholderHighlight for creating custom highlight effects
 */
@Composable
public fun Modifier.placeholder(
  enabled: Boolean = true,
  color: Color = LocalPlaceholderTheme.current.color,
  shape: Shape = LocalPlaceholderTheme.current.shape,
  highlight: PlaceholderHighlight? = LocalPlaceholderTheme.current.highlight,
  placeholderFadeTransitionSpec: () -> FiniteAnimationSpec<Float> =
    LocalPlaceholderTheme.current.placeholderFadeTransitionSpec,
  contentFadeTransitionSpec: () -> FiniteAnimationSpec<Float> =
    LocalPlaceholderTheme.current.contentFadeTransitionSpec,
): Modifier {
  val coordinator = LocalPlaceholderCoordinator.current
  val placeholder = rememberPlaceholder(
    visible = enabled,
    color = color,
    shape = shape,
    highlight = highlight,
    coordinator = coordinator,
    placeholderFadeTransitionSpec = placeholderFadeTransitionSpec,
    contentFadeTransitionSpec = contentFadeTransitionSpec,
  )

  return this then PlaceholderElement(placeholder = placeholder)
}

/**
 * Internal placeholder systems to remember placeholder and running & stopping the placeholder.
 *
 * @param visible whether the placeholder should be visible or not.
 * @param color the color used to draw the placeholder UI.
 * @param shape desired shape of the placeholder. Defaults to [RectangleShape].
 * @param highlight optional highlight animation.
 * @param placeholderFadeTransitionSpec The transition spec to use when fading the placeholder
 * on/off screen. The boolean parameter defined for the transition is [visible].
 * @param contentFadeTransitionSpec The transition spec to use when fading the content
 * on/off screen. The boolean parameter defined for the transition is [visible].
 */
@Composable
internal fun rememberPlaceholder(
  visible: Boolean,
  color: Color,
  shape: Shape = RectangleShape,
  highlight: PlaceholderHighlight? = null,
  coordinator: PlaceholderCoordinator? = null,
  placeholderFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = PlaceholderTheme.Default.placeholderFadeTransitionSpec,
  contentFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = PlaceholderTheme.Default.contentFadeTransitionSpec,
): Placeholder {
  val placeholder: Placeholder = remember(
    keys = arrayOf(
      visible,
      color,
      shape,
      highlight,
      coordinator,
      placeholderFadeTransitionSpec,
      contentFadeTransitionSpec,
    ),
  ) {
    Placeholder(
      visible = visible,
      color = color,
      shape = shape,
      highlight = highlight,
      coordinator = coordinator,
      placeholderFadeTransitionSpec = placeholderFadeTransitionSpec,
      contentFadeTransitionSpec = contentFadeTransitionSpec,
    )
  }

  val inPreviewMode = LocalInspectionMode.current
  LaunchedEffect(key1 = placeholder) {
    if (visible && !inPreviewMode) {
      placeholder.startAnimation()
    } else {
      placeholder.stopAnimation()
    }
  }

  return placeholder
}

/**
 * Draws a multi-line text-shaped placeholder over content while it is loading.
 *
 * Unlike [placeholder] which draws a single rectangle covering the host's bounds, this
 * modifier renders [lines] horizontal bars sized to match the supplied [TextStyle]'s
 * `lineHeight` / `fontSize`, with the last bar narrowed to [lastLineFraction] for a
 * realistic "ragged ending" feel. While [enabled] is true the host is forced to have at
 * least `lines × lineHeight` of vertical space so empty / null Text composables still
 * reserve room for the skeleton.
 *
 * Defaults for [color], [shape], [highlight], and the fade specs are sourced from
 * [LocalPlaceholderTheme]. When wrapped in a [PlaceholderSurface] every bar shares the
 * scope's coordinator and shimmers in lockstep with surrounding placeholders.
 *
 * Example:
 * ```kotlin
 * Text(
 *   text = body ?: "",
 *   style = MaterialTheme.typography.bodyLarge,
 *   modifier = Modifier.placeholderText(
 *     enabled = body == null,
 *     lines = 3,
 *     style = MaterialTheme.typography.bodyLarge,
 *   ),
 * )
 * ```
 *
 * @param enabled Whether the skeleton should be visible. When false, the actual content is shown.
 * @param lines Number of skeleton bars to draw.
 * @param lastLineFraction Width of the final bar relative to the host's width, in `0f..1f`.
 * @param style Text style used to derive line height and bar height. Defaults to
 *   [LocalTextStyle].
 */
@Composable
public fun Modifier.placeholderText(
  enabled: Boolean = true,
  lines: Int = 3,
  lastLineFraction: Float = 0.6f,
  style: TextStyle = LocalTextStyle.current,
  color: Color = LocalPlaceholderTheme.current.color,
  shape: Shape = LocalPlaceholderTheme.current.shape,
  highlight: PlaceholderHighlight? = LocalPlaceholderTheme.current.highlight,
  placeholderFadeTransitionSpec: () -> FiniteAnimationSpec<Float> =
    LocalPlaceholderTheme.current.placeholderFadeTransitionSpec,
  contentFadeTransitionSpec: () -> FiniteAnimationSpec<Float> =
    LocalPlaceholderTheme.current.contentFadeTransitionSpec,
): Modifier {
  val density = LocalDensity.current
  val lineHeightPx = density.resolveLineHeightPx(style)
  val barHeightPx = density.resolveBarHeightPx(style, lineHeightPx)
  val coordinator = LocalPlaceholderCoordinator.current
  val textPlaceholder = rememberTextPlaceholder(
    visible = enabled,
    lines = lines,
    lastLineFraction = lastLineFraction,
    lineHeightPx = lineHeightPx,
    barHeightPx = barHeightPx,
    color = color,
    shape = shape,
    highlight = highlight,
    coordinator = coordinator,
    placeholderFadeTransitionSpec = placeholderFadeTransitionSpec,
    contentFadeTransitionSpec = contentFadeTransitionSpec,
  )
  return this then TextPlaceholderElement(textPlaceholder = textPlaceholder)
}

@Composable
internal fun rememberTextPlaceholder(
  visible: Boolean,
  lines: Int,
  lastLineFraction: Float,
  lineHeightPx: Float,
  barHeightPx: Float,
  color: Color,
  shape: Shape = RectangleShape,
  highlight: PlaceholderHighlight? = null,
  coordinator: PlaceholderCoordinator? = null,
  placeholderFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = PlaceholderTheme.Default.placeholderFadeTransitionSpec,
  contentFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = PlaceholderTheme.Default.contentFadeTransitionSpec,
): TextPlaceholder {
  val textPlaceholder: TextPlaceholder = remember(
    keys = arrayOf(
      visible,
      lines,
      lastLineFraction,
      lineHeightPx,
      barHeightPx,
      color,
      shape,
      highlight,
      coordinator,
      placeholderFadeTransitionSpec,
      contentFadeTransitionSpec,
    ),
  ) {
    TextPlaceholder(
      visible = visible,
      lines = lines,
      lastLineFraction = lastLineFraction,
      lineHeightPx = lineHeightPx,
      barHeightPx = barHeightPx,
      color = color,
      shape = shape,
      highlight = highlight,
      coordinator = coordinator,
      placeholderFadeTransitionSpec = placeholderFadeTransitionSpec,
      contentFadeTransitionSpec = contentFadeTransitionSpec,
    )
  }

  val inPreviewMode = LocalInspectionMode.current
  LaunchedEffect(key1 = textPlaceholder) {
    if (visible && !inPreviewMode) {
      textPlaceholder.startAnimation()
    } else {
      textPlaceholder.stopAnimation()
    }
  }
  return textPlaceholder
}

/**
 * Resolves [TextStyle.lineHeight] to pixels. Falls back to `fontSize × 1.4` when
 * line height is unspecified — same multiplier most font systems use as a reasonable
 * default.
 */
private fun Density.resolveLineHeightPx(style: TextStyle): Float {
  val lineHeight = style.lineHeight
  return when {
    lineHeight.isSp -> with(this) { lineHeight.toPx() }
    lineHeight.isEm && style.fontSize.isSp -> with(this) { style.fontSize.toPx() } * lineHeight.value
    style.fontSize.isSp -> with(this) { style.fontSize.toPx() } * 1.4f
    else -> 14f * fontScale * density * 1.4f // last-ditch fallback (~14sp × 1.4)
  }
}

/**
 * Resolves the bar thickness from [TextStyle.fontSize]. Bars are rendered at the font
 * size's pixel height so they visually align with where glyphs would sit.
 */
private fun Density.resolveBarHeightPx(style: TextStyle, lineHeightPx: Float): Float {
  val fontSize = style.fontSize
  return if (fontSize.isSp) with(this) { fontSize.toPx() } else lineHeightPx * 0.7f
}
