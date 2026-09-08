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
package com.revenuecat.placeholderdemo.shared

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.revenuecat.placeholder.CircularReveal
import com.revenuecat.placeholder.Fade
import com.revenuecat.placeholder.LightReveal
import com.revenuecat.placeholder.PlaceholderHighlight
import com.revenuecat.placeholder.PlaceholderSurface
import com.revenuecat.placeholder.Pulse
import com.revenuecat.placeholder.Shimmer
import com.revenuecat.placeholder.placeholder
import com.revenuecat.placeholder.placeholderText
import kotlin.math.roundToInt

/**
 * An interactive playground for every knob `Modifier.placeholder` exposes.
 *
 * Published to https://revenuecat.github.io/placeholder-compose/demo/ as a Wasm build of the
 * same `commonMain` code the library itself ships, so what you tweak here is what you get.
 */
@Composable
public fun PlaceholderPlayground() {
  val state = remember { PlaygroundState() }

  MaterialTheme(colorScheme = if (state.darkTheme) DarkScheme else LightScheme) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Header(state)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        PlaygroundBody(state, modifier = Modifier.weight(1f))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Footer()
      }
    }
  }
}

@Composable
private fun PlaygroundBody(state: PlaygroundState, modifier: Modifier = Modifier) {
  // Compose reports the viewport through BoxWithConstraints, so the same build serves a phone
  // browser and a desktop one without any platform-specific code.
  BoxWithConstraints(modifier = modifier) {
    if (maxWidth >= WideBreakpoint) {
      Row(modifier = Modifier.fillMaxSize()) {
        ControlsPanel(
          state = state,
          modifier = Modifier.width(340.dp).fillMaxHeight(),
        )
        VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ResultPanel(state = state, modifier = Modifier.weight(1f).fillMaxHeight())
      }
    } else {
      Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ResultPanel(state = state, modifier = Modifier.fillMaxWidth(), scrollable = false)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ControlsPanel(state = state, modifier = Modifier.fillMaxWidth(), scrollable = false)
      }
    }
  }
}

@Composable
private fun Header(state: PlaygroundState) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "Placeholder Playground",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
      )
      Text(
        text = "Tweak the controls and copy the Kotlin that produces it.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    LinkButton("GitHub", RepoUrl)
    TextButton(onClick = { state.darkTheme = !state.darkTheme }) {
      Text(if (state.darkTheme) "Light" else "Dark")
    }
    TextButton(onClick = state::reset) { Text("Reset") }
  }
}

@Composable
private fun LinkButton(label: String, url: String) {
  val uriHandler = LocalUriHandler.current
  TextButton(onClick = { uriHandler.openUri(url) }) { Text(label) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Footer() {
  val uriHandler = LocalUriHandler.current
  FlowRow(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(18.dp),
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = "Placeholder by RevenueCat",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(vertical = 6.dp),
    )
    FooterLink("GitHub", RepoUrl) { uriHandler.openUri(it) }
    FooterLink("Documentation", DocsUrl) { uriHandler.openUri(it) }
    FooterLink("API reference", ApiUrl) { uriHandler.openUri(it) }
  }
}

@Composable
private fun FooterLink(label: String, url: String, onClick: (String) -> Unit) {
  Text(
    text = label,
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.primary,
    textDecoration = TextDecoration.Underline,
    modifier = Modifier.clickable { onClick(url) }.padding(vertical = 6.dp),
  )
}

// -- Controls -----------------------------------------------------------------------------------

@Composable
private fun ControlsPanel(
  state: PlaygroundState,
  modifier: Modifier = Modifier,
  scrollable: Boolean = true,
) {
  Column(
    modifier = modifier
      .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
      .padding(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp),
  ) {
    Section("State")
    SwitchRow("enabled", state.enabled) { state.enabled = it }
    SwitchRow("PlaceholderSurface", state.coordinated) { state.coordinated = it }
    if (state.coordinated) {
      IntSliderRow("surface duration", state.surfaceDurationMillis, 200..4000, "ms") {
        state.surfaceDurationMillis = it
      }
      Hint("Inside a surface every highlight shares one clock, and each highlight's own spec is ignored.")
    }

    Section("Preview")
    ChipRow(PreviewLayout.entries, state.layout) { state.layout = it }
    if (state.layout == PreviewLayout.TextSkeleton) {
      IntSliderRow("lines", state.lines, 1..6) { state.lines = it }
      FloatSliderRow("lastLineFraction", state.lastLineFraction, 0.1f..1f) {
        state.lastLineFraction = it
      }
    }

    Section("Highlight")
    ChipRow(HighlightKind.entries, state.highlight) { state.highlight = it }
    if (state.highlight != HighlightKind.None) {
      SwatchRow("highlightColor", state.highlightSwatch) { state.highlightSwatch = it }
      FloatSliderRow("highlight alpha", state.highlightAlpha, 0f..1f) { state.highlightAlpha = it }
    }
    if (state.highlight == HighlightKind.Shimmer) {
      FloatSliderRow("intensity", state.intensity, 0f..1f) { state.intensity = it }
      FloatSliderRow("dropOff", state.dropOff, 0f..1f) { state.dropOff = it }
      FloatSliderRow("tilt", state.tilt, 0f..90f, decimals = 0) { state.tilt = it }
    }

    Section("Placeholder")
    SwatchRow("color", state.colorSwatch) { state.colorSwatch = it }
    FloatSliderRow("color alpha", state.colorAlpha, 0f..1f) { state.colorAlpha = it }
    ChipRow(ShapeKind.entries, state.shape) { state.shape = it }
    if (state.shape == ShapeKind.RoundedCorner) {
      IntSliderRow("corner radius", state.cornerRadius, 0..48, "dp") { state.cornerRadius = it }
    }

    Section("Timing")
    IntSliderRow("durationMillis", state.durationMillis, 100..4000, "ms") {
      state.durationMillis = it
    }
    IntSliderRow("delayMillis", state.delayMillis, 0..2000, "ms") { state.delayMillis = it }
    ChipRow(listOf(RepeatMode.Restart, RepeatMode.Reverse), state.repeatMode, { it.name }) {
      state.repeatMode = it
    }
  }
}

@Composable
private fun Section(title: String) {
  Spacer(Modifier.height(18.dp))
  Text(
    text = title.uppercase(),
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.primary,
  )
  Spacer(Modifier.height(6.dp))
}

@Composable
private fun Hint(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(bottom = 4.dp),
  )
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
    )
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> ChipRow(
  options: List<T>,
  selected: T,
  label: (T) -> String = { it.toString() },
  onSelect: (T) -> Unit,
) {
  FlowRow(
    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    options.forEach { option ->
      FilterChip(
        selected = option == selected,
        onClick = { onSelect(option) },
        label = { Text(label(option), style = MaterialTheme.typography.labelMedium) },
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SwatchRow(label: String, selected: Swatch, onSelect: (Swatch) -> Unit) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    RowLabel(label, selected.label)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Swatch.entries.forEach { swatch ->
        Box(
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(swatch.color)
            .border(
              width = if (swatch == selected) 3.dp else 1.dp,
              color = if (swatch == selected) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.outlineVariant
              },
              shape = CircleShape,
            )
            .clickable { onSelect(swatch) },
        )
      }
    }
  }
}

@Composable
private fun RowLabel(label: String, value: String) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = label,
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun IntSliderRow(
  label: String,
  value: Int,
  range: IntRange,
  unit: String = "",
  onValueChange: (Int) -> Unit,
) {
  Column(modifier = Modifier.padding(vertical = 2.dp)) {
    RowLabel(label, "$value$unit")
    Slider(
      value = value.toFloat(),
      onValueChange = { onValueChange(it.roundToInt()) },
      valueRange = range.first.toFloat()..range.last.toFloat(),
    )
  }
}

@Composable
private fun FloatSliderRow(
  label: String,
  value: Float,
  range: ClosedFloatingPointRange<Float>,
  decimals: Int = 2,
  onValueChange: (Float) -> Unit,
) {
  Column(modifier = Modifier.padding(vertical = 2.dp)) {
    RowLabel(label, value.format(decimals))
    Slider(value = value, onValueChange = onValueChange, valueRange = range)
  }
}

// -- Preview + generated code -------------------------------------------------------------------

@Composable
private fun ResultPanel(
  state: PlaygroundState,
  modifier: Modifier = Modifier,
  scrollable: Boolean = true,
) {
  Column(
    modifier = modifier
      .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
      .padding(20.dp),
  ) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
      Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        if (state.coordinated) {
          PlaceholderSurface(animationSpec = state.surfaceSpec()) { Preview(state) }
        } else {
          Preview(state)
        }
      }
    }
    Spacer(Modifier.height(20.dp))
    CodeCard(state.asKotlin())
  }
}

@Composable
private fun Preview(state: PlaygroundState) {
  when (state.layout) {
    PreviewLayout.List -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
      repeat(4) { AvatarRow(state) }
    }

    PreviewLayout.Row -> AvatarRow(state)

    PreviewLayout.Card -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
          .placeholder(
            enabled = state.enabled,
            color = state.color(),
            shape = state.shapeValue(),
            highlight = state.highlightValue(),
          ),
      )
      Text(
        text = "Weekly digest",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
          .fillMaxWidth(0.5f)
          .placeholder(
            enabled = state.enabled,
            color = state.color(),
            shape = state.shapeValue(),
            highlight = state.highlightValue(),
          ),
      )
      Text(
        text = "Everything that shipped this week, summarised in one place.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
          .fillMaxWidth()
          .placeholder(
            enabled = state.enabled,
            color = state.color(),
            shape = state.shapeValue(),
            highlight = state.highlightValue(),
          ),
      )
    }

    PreviewLayout.TextSkeleton -> Text(
      text = if (state.enabled) {
        ""
      } else {
        "Modifier.placeholderText draws bars sized from your TextStyle, so an empty or " +
          "null Text still reserves the right amount of room while it loads."
      },
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier
        .fillMaxWidth()
        .placeholderText(
          enabled = state.enabled,
          lines = state.lines,
          lastLineFraction = state.lastLineFraction,
          style = MaterialTheme.typography.bodyLarge,
          color = state.color(),
          shape = state.shapeValue(),
          highlight = state.highlightValue(),
        ),
    )
  }
}

@Composable
private fun AvatarRow(state: PlaygroundState) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(56.dp)
        .placeholder(
          enabled = state.enabled,
          color = state.color(),
          shape = state.shapeValue(),
          highlight = state.highlightValue(),
        ),
    )
    Spacer(Modifier.width(14.dp))
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "Jane Appleseed",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
          .fillMaxWidth(0.55f)
          .placeholder(
            enabled = state.enabled,
            color = state.color(),
            shape = state.shapeValue(),
            highlight = state.highlightValue(),
          ),
      )
      Text(
        text = "Subscribed 3 days ago",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
          .fillMaxWidth(0.8f)
          .placeholder(
            enabled = state.enabled,
            color = state.color(),
            shape = state.shapeValue(),
            highlight = state.highlightValue(),
          ),
      )
    }
  }
}

@Composable
private fun CodeCard(code: String) {
  val clipboard = LocalClipboardManager.current
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Kotlin",
          modifier = Modifier.weight(1f),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
        )
        TextButton(onClick = { clipboard.setText(AnnotatedString(code)) }) { Text("Copy") }
      }
      Spacer(Modifier.height(4.dp))
      Text(
        text = code,
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        style = MaterialTheme.typography.bodySmall,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Start,
      )
    }
  }
}

// -- State --------------------------------------------------------------------------------------

private enum class PreviewLayout(private val label: String) {
  List("List"),
  Row("Single row"),
  Card("Card"),
  TextSkeleton("Text skeleton"),
  ;

  override fun toString(): String = label
}

private enum class HighlightKind(private val label: String) {
  None("None"),
  Shimmer("Shimmer"),
  Fade("Fade"),
  Pulse("Pulse"),
  LightReveal("LightReveal"),
  CircularReveal("CircularReveal"),
  ;

  override fun toString(): String = label
}

private enum class ShapeKind(private val label: String) {
  Rectangle("Rectangle"),
  RoundedCorner("Rounded"),
  Circle("Circle"),
  ;

  override fun toString(): String = label
}

/** Named colors so the generated snippet can print `Color.White` rather than a raw hex literal. */
private enum class Swatch(val label: String, val code: String, val color: Color) {
  White("White", "Color.White", Color.White),
  LightGray("LightGray", "Color.LightGray", Color.LightGray),
  Gray("Gray", "Color.Gray", Color.Gray),
  DarkGray("DarkGray", "Color.DarkGray", Color.DarkGray),
  Blue("Blue", "Color(0xFF4C8DFF)", Color(0xFF4C8DFF)),
  Purple("Purple", "Color(0xFF9B6BFF)", Color(0xFF9B6BFF)),
  Green("Green", "Color(0xFF3DD68C)", Color(0xFF3DD68C)),
  Amber("Amber", "Color(0xFFFFB020)", Color(0xFFFFB020)),
}

@Stable
private class PlaygroundState {
  var darkTheme by mutableStateOf(true)
  var enabled by mutableStateOf(true)
  var coordinated by mutableStateOf(false)
  var surfaceDurationMillis by mutableIntStateOf(1700)
  var layout by mutableStateOf(PreviewLayout.List)
  var lines by mutableIntStateOf(3)
  var lastLineFraction by mutableFloatStateOf(0.6f)
  var highlight by mutableStateOf(HighlightKind.Shimmer)
  var highlightSwatch by mutableStateOf(Swatch.White)
  var highlightAlpha by mutableFloatStateOf(0.5f)
  var intensity by mutableFloatStateOf(0f)
  var dropOff by mutableFloatStateOf(0.5f)
  var tilt by mutableFloatStateOf(20f)
  var colorSwatch by mutableStateOf(Swatch.Gray)
  var colorAlpha by mutableFloatStateOf(0.35f)
  var shape by mutableStateOf(ShapeKind.RoundedCorner)
  var cornerRadius by mutableIntStateOf(8)
  var durationMillis by mutableIntStateOf(1700)
  var delayMillis by mutableIntStateOf(200)
  var repeatMode by mutableStateOf(RepeatMode.Restart)

  fun reset() {
    val fresh = PlaygroundState()
    darkTheme = fresh.darkTheme
    enabled = fresh.enabled
    coordinated = fresh.coordinated
    surfaceDurationMillis = fresh.surfaceDurationMillis
    layout = fresh.layout
    lines = fresh.lines
    lastLineFraction = fresh.lastLineFraction
    highlight = fresh.highlight
    highlightSwatch = fresh.highlightSwatch
    highlightAlpha = fresh.highlightAlpha
    intensity = fresh.intensity
    dropOff = fresh.dropOff
    tilt = fresh.tilt
    colorSwatch = fresh.colorSwatch
    colorAlpha = fresh.colorAlpha
    shape = fresh.shape
    cornerRadius = fresh.cornerRadius
    durationMillis = fresh.durationMillis
    delayMillis = fresh.delayMillis
    repeatMode = fresh.repeatMode
  }

  fun color(): Color = colorSwatch.color.copy(alpha = colorAlpha)

  fun shapeValue(): Shape = when (shape) {
    ShapeKind.Rectangle -> RectangleShape
    ShapeKind.RoundedCorner -> RoundedCornerShape(cornerRadius.dp)
    ShapeKind.Circle -> CircleShape
  }

  fun spec(): InfiniteRepeatableSpec<Float> = infiniteRepeatable(
    animation = tween(durationMillis = durationMillis, delayMillis = delayMillis),
    repeatMode = repeatMode,
  )

  fun surfaceSpec(): InfiniteRepeatableSpec<Float> = infiniteRepeatable(
    animation = tween(durationMillis = surfaceDurationMillis, easing = LinearEasing),
    repeatMode = RepeatMode.Restart,
  )

  fun highlightValue(): PlaceholderHighlight? {
    val tint = highlightSwatch.color.copy(alpha = highlightAlpha)
    return when (highlight) {
      HighlightKind.None -> null
      HighlightKind.Shimmer -> Shimmer(tint, spec(), intensity, dropOff, tilt)
      HighlightKind.Fade -> Fade(tint, spec())
      HighlightKind.Pulse -> Pulse(tint, spec())
      HighlightKind.LightReveal -> LightReveal(tint, spec())
      HighlightKind.CircularReveal -> CircularReveal(tint, spec())
    }
  }

  /** Renders the current state as the Kotlin that would reproduce it. */
  fun asKotlin(): String {
    val indent = if (coordinated) "  " else ""
    val modifier = buildString {
      append(indent)
      if (layout == PreviewLayout.TextSkeleton) {
        appendLine("Modifier.placeholderText(")
        append(indent).appendLine("  enabled = $enabled,")
        append(indent).appendLine("  lines = $lines,")
        append(indent).appendLine("  lastLineFraction = ${lastLineFraction.format(2)}f,")
      } else {
        appendLine("Modifier.placeholder(")
        append(indent).appendLine("  enabled = $enabled,")
      }
      append(indent).appendLine("  color = ${colorSwatch.code}.copy(alpha = ${colorAlpha.format(2)}f),")
      append(indent).appendLine("  shape = ${shapeCode()},")
      append(indent).append("  highlight = ").appendLine(highlightCode(indent + "  "))
      append(indent).append(")")
    }

    if (!coordinated) return modifier

    return buildString {
      appendLine("PlaceholderSurface(")
      appendLine("  animationSpec = infiniteRepeatable(")
      appendLine("    animation = tween(durationMillis = $surfaceDurationMillis, easing = LinearEasing),")
      appendLine("    repeatMode = RepeatMode.Restart,")
      appendLine("  ),")
      appendLine(") {")
      appendLine("  // every placeholder below shares this one clock")
      appendLine(modifier)
      append("}")
    }
  }

  private fun shapeCode(): String = when (shape) {
    ShapeKind.Rectangle -> "RectangleShape"
    ShapeKind.RoundedCorner -> "RoundedCornerShape($cornerRadius.dp)"
    ShapeKind.Circle -> "CircleShape"
  }

  private fun highlightCode(indent: String): String {
    if (highlight == HighlightKind.None) return "null,"
    val tint = "${highlightSwatch.code}.copy(alpha = ${highlightAlpha.format(2)}f)"
    return buildString {
      appendLine("$highlight(")
      append(indent).appendLine("  highlightColor = $tint,")
      if (coordinated) {
        append(indent).appendLine("  // animationSpec is ignored inside a PlaceholderSurface")
      }
      append(indent).appendLine("  animationSpec = infiniteRepeatable(")
      append(indent)
        .appendLine("    animation = tween(durationMillis = $durationMillis, delayMillis = $delayMillis),")
      append(indent).appendLine("    repeatMode = RepeatMode.$repeatMode,")
      append(indent).appendLine("  ),")
      if (highlight == HighlightKind.Shimmer) {
        append(indent).appendLine("  intensity = ${intensity.format(2)}f,")
        append(indent).appendLine("  dropOff = ${dropOff.format(2)}f,")
        append(indent).appendLine("  tilt = ${tilt.format(0)}f,")
      }
      append(indent).append("),")
    }
  }
}

private const val RepoUrl = "https://github.com/RevenueCat/placeholder-compose"
private const val DocsUrl = "https://revenuecat.github.io/placeholder-compose/"
private const val ApiUrl = "https://revenuecat.github.io/placeholder-compose/api/"

private val WideBreakpoint = 900.dp

private val DarkScheme = darkColorScheme(
  primary = Color(0xFFB79BFF),
  background = Color(0xFF16151A),
  surface = Color(0xFF1F1E25),
  surfaceVariant = Color(0xFF2A2833),
  onSurfaceVariant = Color(0xFFCFCADB),
  outlineVariant = Color(0xFF3A3745),
)

private val LightScheme = lightColorScheme(
  primary = Color(0xFF6535C9),
  background = Color(0xFFFBFAFF),
  surface = Color(0xFFFFFFFF),
  surfaceVariant = Color(0xFFF1EEF9),
  onSurfaceVariant = Color(0xFF4A4458),
  outlineVariant = Color(0xFFDDD8EA),
)

/** Small `toFixed`, since `kotlin.text.format` is not available on every KMP target. */
private fun Float.format(decimals: Int): String {
  if (decimals == 0) return roundToInt().toString()
  var factor = 1
  repeat(decimals) { factor *= 10 }
  val scaled = (this * factor).roundToInt()
  val whole = scaled / factor
  val frac = (if (scaled < 0) -scaled else scaled) % factor
  return "$whole.${frac.toString().padStart(decimals, '0')}"
}
