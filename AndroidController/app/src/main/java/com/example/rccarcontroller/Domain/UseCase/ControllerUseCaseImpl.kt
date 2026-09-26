package com.example.rccarcontroller.Domain.UseCase

import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.Repository.ControllerRepository

class ControllerUseCaseImpl(
    private val repository: ControllerRepository
) : ControllerUseCase {

    override fun connect(): Result<Unit> =
        if (repository.connect()) Result.success(Unit)
        else Result.failure(Exception("Connection failed"))

    override fun disconnect(): Result<Unit> =
        if (repository.disconnect()) Result.success(Unit)
        else Result.failure(Exception("Disconnect failed"))

    override fun send(command: Command): Result<Unit> =
        if (repository.send(command)) Result.success(Unit)
        else Result.failure(Exception("Send failed"))
}
