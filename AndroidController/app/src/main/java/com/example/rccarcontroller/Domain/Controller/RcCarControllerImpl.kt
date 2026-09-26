package com.example.rccarcontroller.Domain.Controller

import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.Serializer.CommandSerializer
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothTransport


class RcCarControllerImpl(
    private val transport: BluetoothTransport,
    private val serializer: CommandSerializer
) : RcCarController {

    override fun connect(): Boolean {
        transport.connect()
        return true
    }

    override fun disconnect(): Boolean {
        transport.disconnect()
        return true
    }

    override fun sendCommand(command: Command): Boolean {
        val bytes = serializer.serialize(command)
        transport.send(bytes)
        return true
    }

    override fun setSpeed(speed: Int) {
        sendCommand(Command.Speed(speed))
    }
}