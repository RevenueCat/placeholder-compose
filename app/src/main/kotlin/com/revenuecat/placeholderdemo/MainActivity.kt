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
package com.revenuecat.placeholderdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.revenuecat.placeholder.PlaceholderDefaults
import com.revenuecat.placeholder.PlaceholderHighlight
import com.revenuecat.placeholder.PlaceholderSurface
import com.revenuecat.placeholder.ProvidePlaceholderTheme
import com.revenuecat.placeholder.materialPlaceholderTheme
import com.revenuecat.placeholder.placeholder
import com.revenuecat.placeholder.placeholderText
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      var enabled by remember { mutableStateOf(true) }

      LaunchedEffect(Unit) {
        delay(5000)
        enabled = false
      }

      MaterialTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(top = 10.dp),
          ) {
            Text(
              text = "Per-item highlights (independent timing)",
              modifier = Modifier.padding(12.dp),
              style = MaterialTheme.typography.titleMedium,
            )

            Item(enabled = enabled, highlight = PlaceholderDefaults.shimmer)
            Item(enabled = enabled, highlight = PlaceholderDefaults.fade)
            Item(enabled = enabled, highlight = PlaceholderDefaults.pulse)
            Item(enabled = enabled, highlight = PlaceholderDefaults.lightReveal)
            Item(enabled = enabled, highlight = PlaceholderDefaults.circularReveal)

            Spacer(Modifier.height(16.dp))

            Text(
              text = "Coordinated surface + Material theme",
              modifier = Modifier.padding(12.dp),
              style = MaterialTheme.typography.titleMedium,
            )

            ProvidePlaceholderTheme(materialPlaceholderTheme()) {
              PlaceholderSurface {
                Column {
                  repeat(5) { CoordinatedRow(enabled = enabled) }
                }
              }
            }

            Spacer(Modifier.height(16.dp))

            Text(
              text = "Modifier.placeholderText (multi-line)",
              modifier = Modifier.padding(12.dp),
              style = MaterialTheme.typography.titleMedium,
            )

            ProvidePlaceholderTheme(materialPlaceholderTheme()) {
              Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                  text = "",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier
                    .fillMaxWidth()
                    .placeholderText(
                      enabled = enabled,
                      lines = 3,
                      style = MaterialTheme.typography.bodyLarge,
                      shape = RoundedCornerShape(4.dp),
                    ),
                )
                Spacer(Modifier.height(12.dp))
                Text(
                  text = if (enabled) "" else "Real subtitle text after loading completes.",
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier
                    .fillMaxWidth()
                    .placeholderText(
                      enabled = enabled,
                      lines = 2,
                      lastLineFraction = 0.4f,
                      style = MaterialTheme.typography.bodySmall,
                      shape = RoundedCornerShape(4.dp),
                    ),
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CoordinatedRow(enabled: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp),
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .placeholder(enabled = enabled, shape = CircleShape),
    )
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
      Text(
        text = "Title placeholder",
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp)
          .placeholder(enabled = enabled, shape = RoundedCornerShape(4.dp)),
      )
      Text(
        text = "Subtitle placeholder line two\nand a third line",
        modifier = Modifier
          .fillMaxWidth()
          .placeholder(enabled = enabled, shape = RoundedCornerShape(4.dp)),
      )
    }
  }
}

@Composable
private fun Item(enabled: Boolean, highlight: PlaceholderHighlight) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(12.dp),
  ) {
    Box(
      modifier = Modifier
        .placeholder(
          enabled = enabled,
          highlight = highlight,
        )
        .background(Color.Green)
        .size(64.dp)
        .clip(CircleShape),
    )

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp)
          .placeholder(
            enabled = enabled,
            highlight = highlight,
          ),
        text = "Hello",
      )

      Text(
        modifier = Modifier
          .fillMaxWidth()
          .placeholder(
            enabled = enabled,
            highlight = highlight,
          ),
        text = "Hello1\nHello2\nHello3\n",
      )
    }
  }
}
