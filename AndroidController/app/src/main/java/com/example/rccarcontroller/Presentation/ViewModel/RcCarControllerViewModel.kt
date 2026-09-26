package com.example.rccarcontroller.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import com.example.rccarcontroller.Activity.Components.ConnectionState
import com.example.rccarcontroller.Domain.Controller.RcCarController
import com.example.rccarcontroller.Domain.Model.Command
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RcCarControllerViewModel {
    class ControllerViewModel(
        private val controller: RcCarController
    ) : ViewModel() {

        // --- 状態管理（AsIsからそのまま持ってくる） ---
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

        // --- Domain 呼び出し ---
        fun connect() {
            _connectionState.value = ConnectionState.CONNECTING
            controller.connect()
        }

        fun disconnect() {
            _connectionState.value = ConnectionState.DISCONNECTING
            controller.disconnect()
        }

        fun moveForward() = controller.sendCommand(Command.Forward)
        fun moveBackward() = controller.sendCommand(Command.Backward)
        fun turnLeft() = controller.sendCommand(Command.Left)
        fun turnRight() = controller.sendCommand(Command.Right)
        fun stop() = controller.sendCommand(Command.Stop)

        fun setSpeed(value: Int) {
            _speed.value = value
            controller.setSpeed(value)
        }
    }

}