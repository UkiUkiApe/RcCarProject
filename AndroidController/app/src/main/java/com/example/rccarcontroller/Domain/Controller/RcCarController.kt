package com.example.rccarcontroller.Domain.Controller

import com.example.rccarcontroller.Domain.Model.Command

interface RcCarController {
    fun connect(): Boolean
    fun disconnect(): Boolean
    fun sendCommand(command: Command): Boolean
    fun setSpeed(speed: Int)
}