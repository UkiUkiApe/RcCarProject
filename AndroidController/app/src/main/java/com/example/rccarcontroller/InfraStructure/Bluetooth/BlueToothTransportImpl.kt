package com.example.rccarcontroller.InfraStructure.Bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.util.UUID

/**
 * BluetoothTransport の具体的な実装クラス。
 *
 * Android の BluetoothDevice / BluetoothSocket を利用して
 * RFCOMM（SPP）による Bluetooth 通信を行う Infrastructure 層のコンポーネント。
 *
 * 主な責務:
 * - 指定された BluetoothDevice と UUID を用いて RFCOMM ソケットを生成する
 * - connect() によりソケット接続を開始し、RCカーとの通信チャネルを確立する
 * - disconnect() によりソケットを安全に閉じる
 * - send() により、シリアライズ済みのバイト列を RCカーへ送信する
 *
 * この実装は RcCarControllerImpl（Domain 層）から利用され、
 * CommandSerializer によって生成されたバイト列を実際の Bluetooth 通信として送信する。
 *
 * Transport を抽象化することで、上位層は Bluetooth の API やソケット処理に依存せず、
 * 通信方式の変更（Bluetooth → Wi-Fi など）にも柔軟に対応できる設計となっている。
 *
 * @param device ペアリング済み Bluetooth デバイス
 * @param uuid 通信に使用する RFCOMM/SPP の UUID
 */
class BluetoothTransportImpl(
    private val device: BluetoothDevice,
    private val uuid: UUID
) : BluetoothTransport {

    private var socket: BluetoothSocket? = null

    override fun connect() {
        Log.d("BT", "connect() called: device=${device}, uuid=$uuid")
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid)
            Log.d("BT", "Socket created")
            socket?.connect()
            Log.d("BT", "Connected successfully")
        } catch (e: Exception) {
            Log.e("BT", "Connection failed", e)
            throw e
        }
    }

    override fun disconnect() {
        Log.d("BT", "disconnect() called")
        try {
            socket?.close()
            Log.d("BT", "Disconnected")
        } catch (e: Exception) {
            Log.e("BT", "Disconnect failed", e)
        }
    }

    override fun send(bytes: ByteArray) {
        Log.d("BT", "send() called: ${bytes.decodeToString()}")
        try {
            socket?.outputStream?.write(bytes)
            Log.d("BT", "Send success")
        } catch (e: Exception) {
            Log.e("BT", "Send failed", e)
            throw e
        }
    }
}