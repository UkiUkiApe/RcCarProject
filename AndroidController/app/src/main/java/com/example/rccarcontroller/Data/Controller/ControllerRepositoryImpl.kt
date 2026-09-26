package com.example.rccarcontroller.Data.Controller

import com.example.rccarcontroller.Data.Bluetooth.BluetoothConfigRepository
import com.example.rccarcontroller.Domain.Controller.RcCarControllerImpl
import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.Repository.ControllerRepository
import com.example.rccarcontroller.Domain.Serializer.SimpleCommandSerializer
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothDeviceResolver
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothTransportImpl

class ControllerRepositoryImpl(
    private val configRepository: BluetoothConfigRepository,
    private val deviceResolver: BluetoothDeviceResolver
) : ControllerRepository {

    private val config = configRepository.load()

    private val device = deviceResolver.resolveByName(config.deviceName)
        ?: throw IllegalStateException("Device not found or permission missing")

    private val controller = RcCarControllerImpl(
        BluetoothTransportImpl(device, config.uuid),
        SimpleCommandSerializer()
    )
    override fun connect(): Boolean = controller.connect()
    override fun disconnect(): Boolean = controller.disconnect()
    override fun send(command: Command): Boolean = controller.sendCommand(command)
}


