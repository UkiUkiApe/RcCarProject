package com.example.rccarcontroller.Domain.Controller

import com.example.rccarcontroller.Domain.Model.Command

interface RcCarController {
    fun connect()
    fun disconnect()
    fun sendCommand(command: Command)
    fun setSpeed(speed: Int)
}