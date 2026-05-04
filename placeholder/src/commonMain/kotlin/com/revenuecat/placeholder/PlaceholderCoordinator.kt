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
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalInspectionMode

/**
 * Drives a single shared `0f..1f` highlight animation that every descendant
 * placeholder reads from, so a list of skeleton items shimmers as one
 * coordinated wave instead of each cell ticking on its own clock.
 *
 * Obtain one via [PlaceholderSurface] (which also publishes it to
 * [LocalPlaceholderCoordinator]). When a coordinator is active, the per-highlight
 * `animationSpec` is ignored — the coordinator's spec governs every placeholder
 * inside the scope.
 */
@Stable
public class PlaceholderCoordinator internal constructor(
  internal val animationSpec: InfiniteRepeatableSpec<Float>,
) {
  internal val progress: Animatable<Float, AnimationVector1D> = Animatable(0f)

  internal suspend fun run() {
    progress.snapTo(0f)
    progress.animateTo(targetValue = 1f, animationSpec = animationSpec)
  }
}

/**
 * The default coordinator spec — a 1700ms linear cycle that loosely matches
 * [PlaceholderDefaults.shimmer]'s feel.
 */
public val DefaultPlaceholderCoordinatorSpec: InfiniteRepeatableSpec<Float> =
  infiniteRepeatable(
    animation = tween(durationMillis = 1700, easing = LinearEasing),
    repeatMode = RepeatMode.Restart,
  )

/**
 * Wraps [content] in a scope where every `Modifier.placeholder(...)` call shares
 * a single [PlaceholderCoordinator]. Use this around a list, card grid, or any
 * region where you want the shimmer to read as one synchronized motion.
 *
 * Example:
 * ```kotlin
 * PlaceholderSurface {
 *   LazyColumn {
 *     items(20) { SkeletonRow() } // all rows shimmer in sync
 *   }
 * }
 * ```
 *
 * @param animationSpec The shared infinite spec driving every placeholder in the scope.
 *   Per-highlight specs are ignored while inside this surface.
 */
@Composable
public fun PlaceholderSurface(
  animationSpec: InfiniteRepeatableSpec<Float> = DefaultPlaceholderCoordinatorSpec,
  content: @Composable () -> Unit,
) {
  val coordinator = remember(animationSpec) { PlaceholderCoordinator(animationSpec) }
  val inPreview = LocalInspectionMode.current
  LaunchedEffect(coordinator) {
    if (!inPreview) coordinator.run()
  }
  CompositionLocalProvider(LocalPlaceholderCoordinator provides coordinator, content = content)
}

/**
 * CompositionLocal carrying the active [PlaceholderCoordinator], or `null` when no
 * [PlaceholderSurface] wraps the current call site.
 */
public val LocalPlaceholderCoordinator: ProvidableCompositionLocal<PlaceholderCoordinator?> =
  staticCompositionLocalOf { null }
