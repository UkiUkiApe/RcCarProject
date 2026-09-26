package com.example.rccarcontroller.Domain.UseCase

import com.example.rccarcontroller.Domain.Model.Command

interface ControllerUseCase {
    fun connect(): Result<Unit>
    fun disconnect(): Result<Unit>
    fun send(command: Command): Result<Unit>
}
