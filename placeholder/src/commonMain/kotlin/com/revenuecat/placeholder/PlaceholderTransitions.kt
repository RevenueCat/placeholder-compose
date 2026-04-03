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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

/**
 * Provides preset animation specifications for placeholder fade-in/fade-out transitions.
 *
 * These presets control how smoothly the placeholder appears and disappears, separate from
 * the highlight animation effects (like shimmer or fade). Use these with the
 * [placeholderFadeTransitionSpec] and [contentFadeTransitionSpec] parameters.
 *
 * Example usage:
 * ```kotlin
 * Box(
 *   modifier = Modifier
 *     .size(200.dp, 50.dp)
 *     .placeholder(
 *       visible = isLoading,
 *       highlight = PlaceholderDefaults.shimmer,
 *       placeholderFadeTransitionSpec = PlaceholderTransitions.fast,
 *       contentFadeTransitionSpec = PlaceholderTransitions.smooth
 *     )
 * )
 * ```
 *
 * The difference between these and [PlaceholderDefaults]:
 * - **PlaceholderDefaults** (shimmer, fade, pulse) = The animated effect shown **on** the placeholder
 * - **PlaceholderTransitions** = How the placeholder itself appears/disappears
 *
 * @see PlaceholderDefaults for highlight effect presets
 */
public object PlaceholderTransitions {

  /**
   * Fast transition preset for quick fade in/out (200ms).
   *
   * Uses a tween animation, ideal for scenarios where content loads quickly
   * and you want minimal delay.
   *
   * **Best for:**
   * - Quick API responses
   * - Cached content
   * - Minimal loading states
   */
  public val fast: () -> FiniteAnimationSpec<Float> = {
    androidx.compose.animation.core.tween(durationMillis = 200)
  }

  /**
   * Normal transition preset with balanced spring physics.
   *
   * Uses default spring animation, providing natural and responsive motion.
   * This is the library's default behavior.
   *
   * **Best for:**
   * - Standard loading scenarios
   * - General-purpose placeholders
   * - Most use cases
   */
  public val normal: () -> FiniteAnimationSpec<Float> = { spring() }

  /**
   * Smooth transition preset with gentle, elegant motion.
   *
   * Uses low stiffness spring for gradual transitions that feel refined.
   *
   * **Best for:**
   * - Premium app experiences
   * - Image-heavy content
   * - When aesthetics matter
   */
  public val smooth: () -> FiniteAnimationSpec<Float> = {
    spring(stiffness = Spring.StiffnessLow)
  }

  /**
   * Slow transition preset for deliberate fade in/out (800ms).
   *
   * Uses a tween animation, creating a pronounced loading effect.
   *
   * **Best for:**
   * - Long-running operations
   * - Educational or onboarding flows
   * - Emphasizing loading state
   */
  public val slow: () -> FiniteAnimationSpec<Float> = {
    androidx.compose.animation.core.tween(durationMillis = 800)
  }

  /**
   * Snappy transition preset with instant, energetic motion.
   *
   * Uses high stiffness spring for quick, responsive feel.
   *
   * **Best for:**
   * - Interactions needing instant feedback
   * - Small UI elements
   * - When responsiveness is critical
   */
  public val snappy: () -> FiniteAnimationSpec<Float> = {
    spring(stiffness = Spring.StiffnessHigh)
  }

  /**
   * Bouncy transition preset with playful overshoot.
   *
   * Uses low damping spring that bounces slightly before settling.
   * Adds personality to transitions.
   *
   * **Best for:**
   * - Playful or casual apps
   * - Gaming or entertainment
   * - Adding character to loading
   */
  public val bouncy: () -> FiniteAnimationSpec<Float> = {
    spring(
      dampingRatio = Spring.DampingRatioLowBouncy,
      stiffness = Spring.StiffnessMedium,
    )
  }

  /**
   * Creates a custom spring transition with specific damping and stiffness.
   *
   * @param dampingRatio Controls oscillation (higher = less bounce)
   * @param stiffness Controls speed (higher = faster)
   */
  public fun custom(
    dampingRatio: Float = Spring.DampingRatioNoBouncy,
    stiffness: Float = Spring.StiffnessMedium,
  ): () -> FiniteAnimationSpec<Float> = {
    spring(dampingRatio = dampingRatio, stiffness = stiffness)
  }

  /**
   * Creates a custom tween transition with specific duration.
   *
   * @param durationMillis Duration of the transition in milliseconds
   */
  public fun tween(durationMillis: Int): () -> FiniteAnimationSpec<Float> = {
    androidx.compose.animation.core.tween(durationMillis = durationMillis)
  }
}
