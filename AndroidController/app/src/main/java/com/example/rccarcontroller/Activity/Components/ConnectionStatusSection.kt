package com.example.rccarcontroller.Activity.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
// 通信状態設定部(画面の中身を定義する)
fun ConnectionStatusSection(
    connectionState: ConnectionState,
    errorCode: Int?,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    Column {
        Text(text = "状態: $connectionState")

        if (errorCode != null) {
            Text(text = "エラーコード: $errorCode")
        }

        Button(onClick = onConnect) {
            Text("接続")
        }

        Button(onClick = onDisconnect) {
            Text("切断")
        }
    }
}
