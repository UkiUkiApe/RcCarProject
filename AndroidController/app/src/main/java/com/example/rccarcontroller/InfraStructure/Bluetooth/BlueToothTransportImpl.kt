package com.example.rccarcontroller.InfraStructure.Bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.util.UUID

class BluetoothTransportImpl(
    private val device: BluetoothDevice,
    private val uuid: UUID
) : BluetoothTransport {

    private var socket: BluetoothSocket? = null

    override fun connect() {
        socket = device.createRfcommSocketToServiceRecord(uuid)
        socket?.connect()
    }

    override fun disconnect() {
        socket?.close()
    }

    override fun send(bytes: ByteArray) {
        socket?.outputStream?.write(bytes)
    }
}