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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalInspectionMode

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
