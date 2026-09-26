package com.example.rccarcontroller.InfraStructure.Bluetooth

interface BluetoothTransport {
    fun connect()
    fun disconnect()
    fun send(bytes: ByteArray)
}