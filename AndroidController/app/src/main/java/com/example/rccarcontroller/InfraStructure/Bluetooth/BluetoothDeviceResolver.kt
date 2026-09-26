package com.example.rccarcontroller.InfraStructure.Bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class BluetoothDeviceResolver(
    private val context: Context
) {

    @SuppressLint("MissingPermission")
    fun resolveByName(name: String): BluetoothDevice? {
        val adapter = BluetoothAdapter.getDefaultAdapter()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return adapter.bondedDevices.firstOrNull { it.name == name }
    }
}