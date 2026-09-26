package com.example.rccarcontroller.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rccarcontroller.Activity.Components.ConnectionState
import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.UseCase.ControllerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ControllerViewModel(
    private val useCase: ControllerUseCase
) : ViewModel() {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _speed = MutableStateFlow(0)
    val speed: StateFlow<Int> = _speed

    private val _batteryLevel = MutableStateFlow<Int?>(null)
    val batteryLevel: StateFlow<Int?> = _batteryLevel

    private val _temperature = MutableStateFlow<Float?>(null)
    val temperature: StateFlow<Float?> = _temperature

    private val _errorCode = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _errorCode

    // --- UseCase 呼び出し ---
    fun connect() {
        _connectionState.value = ConnectionState.CONNECTING

        viewModelScope.launch {
            val result = useCase.connect()
            _connectionState.value =
                if (result.isSuccess) ConnectionState.CONNECTED
                else ConnectionState.ERROR
        }
    }

    fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTING

        viewModelScope.launch {
            val result = useCase.disconnect()
            _connectionState.value =
                if (result.isSuccess) ConnectionState.DISCONNECTED
                else ConnectionState.ERROR
        }
    }

    fun moveForward() = send(Command.Forward)
    fun moveBackward() = send(Command.Backward)
    fun turnLeft() = send(Command.Left)
    fun turnRight() = send(Command.Right)
    fun stop() = send(Command.Stop)

    fun setSpeed(value: Int) {
        _speed.value = value
        send(Command.Speed(value))
    }

    private fun send(command: Command) {
        viewModelScope.launch {
            val result = useCase.send(command)
            if (result.isFailure) {
                _errorCode.value = -1 // 適当なエラーコード
            }
        }
    }
}
