package com.example.rccarcontroller.Data.Bluetooth

import android.content.Context
import com.example.rccarcontroller.Domain.Model.BluetoothConfig
import org.json.JSONObject
import java.util.UUID

class BluetoothConfigRepository(
    private val context: Context
) {
    fun load(): BluetoothConfig {
        val json = context.assets.open("config.json")
            .bufferedReader()
            .readText()

        return parse(json)
    }

    private fun parse(json: String): BluetoothConfig {
        val obj = JSONObject(json)
        return BluetoothConfig(
            deviceName = obj.getString("deviceName"),
            uuid = UUID.fromString(obj.getString("uuid"))
        )
    }
}