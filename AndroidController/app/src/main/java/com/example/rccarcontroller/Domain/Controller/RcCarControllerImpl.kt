package com.example.rccarcontroller.Domain.Controller

import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.Serializer.CommandSerializer
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothTransport


class RcCarControllerImpl(
    private val transport: BluetoothTransport,
    private val serializer: CommandSerializer
) : RcCarController {

    override fun connect() {
        transport.connect()
    }

    override fun disconnect() {
        transport.disconnect()
    }

    override fun sendCommand(command: Command) {
        val bytes = serializer.serialize(command)
        transport.send(bytes)
    }

    override fun setSpeed(speed: Int) {
        sendCommand(Command.Speed(speed))
    }
}