package com.example.rccarcontroller.Activity.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
// 車体状態表示部(車体状態の中身を定義する)
fun CarStatusSection(
    speed: Int,
    battery: Int?,
    temperature: Float?
) {
    Column {
        Text(text = "速度: $speed")

        battery?.let {
            Text(text = "バッテリー: $it%")
        }

        temperature?.let {
            Text(text = "温度: $it ℃")
        }
    }
}