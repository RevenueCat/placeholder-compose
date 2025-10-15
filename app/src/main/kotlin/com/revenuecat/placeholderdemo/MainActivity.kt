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
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.revenuecat.placeholder.PlaceholderTransitions
import com.revenuecat.placeholder.placeholder
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
            ) {
                Item(
                    enabled = enabled,
                    highlight = PlaceholderDefaults.shimmer,
                    transitionSpec = PlaceholderTransitions.fast
                )

                Item(
                    enabled = enabled,
                    highlight = PlaceholderDefaults.fade,
                    transitionSpec = PlaceholderTransitions.smooth
                )

                Item(
                    enabled = enabled,
                    highlight = PlaceholderDefaults.pulse,
                    transitionSpec = PlaceholderTransitions.snappy
                )

                Item(
                    enabled = enabled,
                    highlight = PlaceholderDefaults.lightReveal,
                    transitionSpec = PlaceholderTransitions.bouncy
                )

                Item(
                    enabled = enabled,
                    highlight = PlaceholderDefaults.circularReveal,
                    transitionSpec = PlaceholderTransitions.tween(100)
                )
            }
        }
    }
}

@Composable
private fun Item(
    enabled: Boolean,
    highlight: PlaceholderHighlight,
    transitionSpec: () -> FiniteAnimationSpec<Float>
) {
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
                    contentFadeTransitionSpec = transitionSpec,
                    placeholderFadeTransitionSpec = transitionSpec
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
                        contentFadeTransitionSpec = transitionSpec,
                        placeholderFadeTransitionSpec = transitionSpec
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
