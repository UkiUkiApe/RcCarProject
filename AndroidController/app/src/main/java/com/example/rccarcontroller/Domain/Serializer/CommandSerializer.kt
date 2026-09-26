package com.example.rccarcontroller.Domain.Serializer

import com.example.rccarcontroller.Domain.Model.Command

interface CommandSerializer {
    fun serialize(command: Command): ByteArray
}

class SimpleCommandSerializer : CommandSerializer {
    override fun serialize(command: Command): ByteArray {
        return when (command) {
            Command.Forward -> "F".toByteArray()
            Command.Backward -> "B".toByteArray()
            Command.Left -> "L".toByteArray()
            Command.Right -> "R".toByteArray()
            Command.Stop -> "S".toByteArray()
            is Command.Speed -> "V:${command.value}".toByteArray()
        }
    }
}
