package com.example.rccarcontroller.Domain.Controller

import android.util.Log
import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.Serializer.CommandSerializer
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothTransport


/**
 * RcCarController の具体的な実装クラス。
 *
 * BluetoothTransport を用いて RCカーへバイト列を送信し、
 * CommandSerializer により Command を送信可能な形式へ変換することで、
 * 車体の制御処理を実現する Domain 層の中心的コンポーネント。
 *
 * 主な責務:
 * - transport.connect() / disconnect() を呼び出し、RCカーとの接続状態を管理する
 * - CommandSerializer により Command をバイト列へ変換し、transport.send() で送信する
 * - setSpeed() を Command.Speed に変換して sendCommand() に委譲することで、
 *   UI や UseCase が速度変更の具体的なコマンド形式を意識しなくて済むようにする
 *
 * この実装により、上位層（UseCase / ViewModel / UI）は
 * Bluetooth の送信処理やシリアライズ形式に依存せず、
 * 抽象化された RcCarController API を通して RCカーを操作できる。
 *
 * @param transport RCカーへの通信を担当する BluetoothTransport 実装
 * @param serializer Command を送信可能なバイト列へ変換するための Serializer
 */
class RcCarControllerImpl(
    private val transport: BluetoothTransport,
    private val serializer: CommandSerializer
) : RcCarController {

    override fun connect(): Boolean {
        Log.d("Controller", "connect() called")
        transport.connect()
        Log.d("Controller", "connect() finished")
        return true
    }

    override fun disconnect(): Boolean {
        Log.d("Controller", "disconnect() called")
        transport.disconnect()
        Log.d("Controller", "disconnect() finished")
        return true
    }
    override fun sendCommand(command: Command): Boolean {
        Log.d("Controller", "sendCommand() called: command=$command")

        val bytes = serializer.serialize(command)
        Log.d("Controller", "serialized bytes=${bytes.decodeToString()}")

        transport.send(bytes)
        Log.d("Controller", "sendCommand() finished")

        return true
    }

    override fun setSpeed(speed: Int) {
        Log.d("Controller", "setSpeed() called: speed=$speed")
        sendCommand(Command.Speed(speed))
    }
}