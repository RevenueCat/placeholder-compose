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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/**
 * Multi-line text-shaped placeholder. Renders [lines] horizontal bars sized to match a
 * [lineHeightPx]/[barHeightPx] pair derived from a [androidx.compose.ui.text.TextStyle],
 * with the last bar narrowed to [lastLineFraction] for the typical "ragged ending" feel.
 *
 * Drawing reuses the same fade-in / fade-out animation pattern as [Placeholder] so the
 * `Modifier.placeholderText` modifier behaves consistently with `Modifier.placeholder`.
 *
 * When a [PlaceholderCoordinator] is present, all bars share the coordinator's progress —
 * inside a [PlaceholderSurface] every text-skeleton bar shimmers in lockstep with the
 * surrounding placeholders.
 */
@Stable
internal data class TextPlaceholder(
  private val visible: Boolean,
  private val lines: Int,
  private val lastLineFraction: Float,
  private val lineHeightPx: Float,
  private val barHeightPx: Float,
  private val color: Color,
  private val shape: Shape = RectangleShape,
  private val highlight: PlaceholderHighlight? = null,
  private val coordinator: PlaceholderCoordinator? = null,
  private val placeholderFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = { spring() },
  private val contentFadeTransitionSpec: () -> FiniteAnimationSpec<Float> = { spring() },
) {
  private val placeholderAlpha = Animatable(if (visible) 1f else 0f)
  private val contentAlpha = Animatable(if (visible) 0f else 1f)
  private val highlightProgress = Animatable(0f)
  private val paint = Paint().apply {
    isAntiAlias = true
    style = PaintingStyle.Fill
  }

  /** Minimum height needed to render [lines] of skeleton, in pixels. */
  internal val minHeightPx: Int
    get() = if (visible) (lines * lineHeightPx).roundToInt() else 0

  internal suspend fun startAnimation() {
    placeholderAlpha.animateTo(
      targetValue = if (visible) 1f else 0f,
      animationSpec = placeholderFadeTransitionSpec(),
    )
    contentAlpha.animateTo(
      targetValue = if (visible) 0f else 1f,
      animationSpec = contentFadeTransitionSpec(),
    )

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
    val pAlpha = placeholderAlpha.value
    val cAlpha = contentAlpha.value
    val progress = coordinator?.progress?.value ?: highlightProgress.value

    if (cAlpha > 0.01f) {
      paint.alpha = cAlpha
      withLayer(paint) {
        with(this@draw) {
          drawContent()
        }
      }
    }

    if (pAlpha > 0.01f) {
      paint.alpha = pAlpha
      withLayer(paint) {
        drawBars(progress)
      }
    }
  }

  private fun DrawScope.drawBars(progress: Float) {
    val width = size.width
    val verticalPadding = (lineHeightPx - barHeightPx) / 2f
    for (i in 0 until lines) {
      val isLast = i == lines - 1
      val barWidth = if (isLast) width * lastLineFraction else width
      val barTop = i * lineHeightPx + verticalPadding
      drawBar(
        topLeft = Offset(0f, barTop),
        rectSize = Size(barWidth, barHeightPx),
        progress = progress,
      )
    }
  }

  private fun DrawScope.drawBar(
    topLeft: Offset,
    rectSize: Size,
    progress: Float,
  ) {
    if (shape === RectangleShape) {
      drawRect(color = color, topLeft = topLeft, size = rectSize)
      if (highlight != null) {
        drawRect(
          brush = highlight.brush(progress, rectSize),
          topLeft = topLeft,
          size = rectSize,
          alpha = highlight.alpha(progress),
        )
      }
      return
    }

    val outline = shape.createOutline(rectSize, layoutDirection, this)
    withTransform({ translate(topLeft.x, topLeft.y) }) {
      drawOutline(outline = outline, color = color)
      if (highlight != null) {
        drawOutline(
          outline = outline,
          brush = highlight.brush(progress, rectSize),
          alpha = highlight.alpha(progress),
        )
      }
    }
  }
}

internal class TextPlaceholderNode(
  var textPlaceholder: TextPlaceholder,
) : Modifier.Node(), LayoutModifierNode, DrawModifierNode {

  override fun MeasureScope.measure(
    measurable: Measurable,
    constraints: Constraints,
  ): MeasureResult {
    val minH = textPlaceholder.minHeightPx
    val effectiveMin = maxOf(constraints.minHeight, minH).coerceAtMost(constraints.maxHeight)
    val adjusted = constraints.copy(minHeight = effectiveMin)
    val placeable = measurable.measure(adjusted)
    return layout(placeable.width, placeable.height) {
      placeable.place(0, 0)
    }
  }

  override fun ContentDrawScope.draw() {
    with(textPlaceholder) { draw() }
  }
}

internal data class TextPlaceholderElement(
  var textPlaceholder: TextPlaceholder,
) : ModifierNodeElement<TextPlaceholderNode>() {
  override fun create(): TextPlaceholderNode = TextPlaceholderNode(textPlaceholder)
  override fun update(node: TextPlaceholderNode) {
    node.textPlaceholder = textPlaceholder
  }
  override fun InspectorInfo.inspectableProperties() {
    name = "placeholderText"
    properties["textPlaceholder"] = textPlaceholder
    properties["loadingDescription"] = "Loading.."
  }
}

private inline fun DrawScope.withLayer(
  paint: Paint,
  drawBlock: DrawScope.() -> Unit,
) = drawIntoCanvas { canvas ->
  canvas.saveLayer(size.toRect(), paint)
  drawBlock()
  canvas.restore()
}
