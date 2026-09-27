package com.example.rccarcontroller.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ControllerPadSection(
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onStop: () -> Unit,
    speed: Int,
    onSpeedChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        // 十字キー（上）
        Button(onClick = onForward) {
            Text("↑")
        }

        // 左・停止・右
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onLeft) { Text("←") }
            Button(onClick = onStop) { Text("■") }
            Button(onClick = onRight) { Text("→") }
        }

        // 下
        Button(onClick = onBackward) {
            Text("↓")
        }

        // 速度スライダー
        Text(text = "速度: $speed")
        Slider(
            value = speed.toFloat(),
            onValueChange = { onSpeedChange(it.toInt()) },
            valueRange = 0f..100f
        )
    }
}
