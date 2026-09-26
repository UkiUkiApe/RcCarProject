package com.example.rccarcontroller.Domain.Repository

import com.example.rccarcontroller.Domain.Model.Command

interface ControllerRepository {
    fun connect(): Boolean
    fun disconnect(): Boolean
    fun send(command: Command): Boolean
}