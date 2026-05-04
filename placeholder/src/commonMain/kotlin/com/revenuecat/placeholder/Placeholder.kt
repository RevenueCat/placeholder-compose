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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.LayoutDirection

/**
 * Internal placeholder data class for holding placeholder relevant data.
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
@Stable
internal data class Placeholder(
  private val visible: Boolean,
  private val color: Color,
  private val shape: Shape = RectangleShape,
  private val highlight: PlaceholderHighlight? = null,
  private val coordinator: PlaceholderCoordinator? = null,
  private val placeholderFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = { spring() },
  private val contentFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = { spring() },
) {
  private var lastSize: Size? = null
  private var lastLayoutDirection: LayoutDirection? = null
  private var lastOutline: Outline? = null

  private val placeholderAlpha = Animatable(if (visible) 1f else 0f)
  private val contentAlpha = Animatable(if (visible) 0f else 1f)
  private val highlightProgress = Animatable(0f)
  private val paint = Paint().apply {
    isAntiAlias = true
    style = PaintingStyle.Fill
    blendMode = this.blendMode
  }

  internal suspend fun startAnimation() {
    placeholderAlpha.animateTo(
      targetValue = if (visible) 1f else 0f,
      animationSpec = placeholderFadeTransitionSpec(),
    )
    contentAlpha.animateTo(
      targetValue = if (visible) 0f else 1f,
      animationSpec = contentFadeTransitionSpec(),
    )

    // Coroutine for the infinite highlight (shimmer) animation. Skipped when a
    // PlaceholderCoordinator is driving progress for the whole scope.
    val shouldAnimateHighlight =
      visible && highlight?.animationSpec != null && coordinator == null
    highlightProgress.stop()
    if (shouldAnimateHighlight) {
      highlightProgress.animateTo(
        targetValue = 1f,
        animationSpec = highlight.animationSpec!!,
      )
    } else {
      highlightProgress.snapTo(0f)
    }
  }

  internal suspend fun stopAnimation() {
    placeholderAlpha.stop()
    contentAlpha.stop()
    highlightProgress.stop()
  }

  internal fun ContentDrawScope.draw() {
    val placeholderAlpha = this@Placeholder.placeholderAlpha.value
    val contentAlpha = this@Placeholder.contentAlpha.value
    val highlightProgressValue =
      coordinator?.progress?.value ?: this@Placeholder.highlightProgress.value

    // Draw content
    if (contentAlpha > 0.01f) {
      paint.alpha = contentAlpha
      withLayer(paint) {
        with(this@draw) {
          drawContent()
        }
      }
    }

    // Draw placeholder
    if (placeholderAlpha > 0.01f) {
      paint.alpha = placeholderAlpha
      withLayer(paint) {
        lastOutline = drawPlaceholder(
          shape = shape,
          color = color,
          highlight = highlight,
          progress = highlightProgressValue,
          lastOutline = lastOutline,
          lastLayoutDirection = lastLayoutDirection,
          lastSize = lastSize,
        )
      }
    }

    // Cache size and direction
    lastSize = size
    lastLayoutDirection = layoutDirection
  }
}

internal class PlaceholderNode(
  var placeholder: Placeholder,
) : Modifier.Node(), DrawModifierNode {

  override fun ContentDrawScope.draw() {
    with(placeholder) {
      draw()
    }
  }
}

// The factory for our PlaceholderNode
internal data class PlaceholderElement(
  var placeholder: Placeholder,
) : ModifierNodeElement<PlaceholderNode>() {
  override fun create(): PlaceholderNode {
    return PlaceholderNode(placeholder = placeholder)
  }

  override fun update(node: PlaceholderNode) {
    node.placeholder = placeholder
  }

  override fun InspectorInfo.inspectableProperties() {
    name = "placeholder"
    properties["placeholder"] = placeholder
    properties["loadingDescription"] = "Loading.."
  }
}

private fun DrawScope.drawPlaceholder(
  shape: Shape,
  color: Color,
  highlight: PlaceholderHighlight?,
  progress: Float,
  lastOutline: Outline?,
  lastLayoutDirection: LayoutDirection?,
  lastSize: Size?,
): Outline? {
  if (shape === RectangleShape) {
    drawRect(color = color)
    if (highlight != null) {
      drawRect(
        brush = highlight.brush(progress, size),
        alpha = highlight.alpha(progress),
      )
    }
    return null
  }

  val outline = lastOutline.takeIf {
    size == lastSize && layoutDirection == lastLayoutDirection
  } ?: shape.createOutline(size, layoutDirection, this)

  drawOutline(outline = outline, color = color)
  if (highlight != null) {
    drawOutline(
      outline = outline,
      brush = highlight.brush(progress, size),
      alpha = highlight.alpha(progress),
    )
  }
  return outline
}

private inline fun DrawScope.withLayer(
  paint: Paint,
  drawBlock: DrawScope.() -> Unit,
) = drawIntoCanvas { canvas ->
  canvas.saveLayer(size.toRect(), paint)
  drawBlock()
  canvas.restore()
}
