package com.example.rccarcontroller.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rccarcontroller.Activity.Components.ConnectionState
import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.UseCase.ControllerUseCase
import com.example.rccarcontroller.InfraStructure.Logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * RCカー制御に関する UI 状態を管理し、ControllerUseCase を介して
 * Domain 層の操作（接続・切断・コマンド送信）を実行する ViewModel。
 *
 * Clean Architecture における Presentation 層の中心として機能し、
 * UI が Bluetooth や Transport の詳細を一切意識せずに操作できるようにする。
 *
 * 主な責務:
 * - 接続状態、速度、バッテリー残量、温度、エラーコードなどの UI 状態を StateFlow で保持する
 * - connect / disconnect の進行状態を UI に反映しつつ、UseCase の結果に応じて状態を更新する
 * - moveForward / turnLeft などの操作を Command に変換し、UseCase に委譲する
 * - コマンド送信失敗時には errorCode を更新し、UI がエラー表示できるようにする
 *
 * 非同期処理:
 * - viewModelScope を用いて UseCase の suspend 関数を呼び出し、
 *   UI スレッドをブロックせずに状態更新を行う
 *
 * この ViewModel により、UI は Domain 層の複雑な処理を意識せず、
 * 単純な状態監視とイベント発行のみで RCカーを操作できる。
 *
 * @param useCase RCカー制御ロジックを提供する UseCase
 */
class ControllerViewModel(
    private val useCase: ControllerUseCase,
    private val logger: Logger
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
        logger.info("VM connect() called")
        _connectionState.value = ConnectionState.CONNECTING
        logger.info("VM connectionState = CONNECTING")

        viewModelScope.launch {
            val result = useCase.connect()
            logger.info("VM connect() result = $result")
            _connectionState.value =
                if (result.isSuccess) ConnectionState.CONNECTED
                else ConnectionState.ERROR
            logger.info("VM connectionState = ${_connectionState.value}")
        }
    }

    fun disconnect() {
        logger.info("VM disconnect() called")
        _connectionState.value = ConnectionState.DISCONNECTING
        logger.info("VM connectionState = DISCONNECTING")

        viewModelScope.launch {
            val result = useCase.disconnect()
            logger.info("VM disconnect() result = $result")

            _connectionState.value =
                if (result.isSuccess) ConnectionState.DISCONNECTED
                else ConnectionState.ERROR

            logger.info("VM connectionState = ${_connectionState.value}")
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
        logger.info("VM send() called: command=$command")
        viewModelScope.launch {
            val result = useCase.send(command)
            logger.info("VM send() result = $result")
            if (result.isFailure) {
                logger.error("VM send() failed: command=$command")
                _errorCode.value = -1 // 適当なエラーコード
            }
        }
    }
}
