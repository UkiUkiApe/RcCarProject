package com.example.rccarcontroller.InfraStructure.Bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.rccarcontroller.InfraStructure.Client.BluetoothClient
import com.example.rccarcontroller.InfraStructure.Client.ConnectionTarget
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
    private val client: BluetoothClient
) : BluetoothTransport {

    override suspend fun connect() {
        client.connect(
            ConnectionTarget.Bluetooth(client.device)
        )
    }

    override suspend fun disconnect() {
        client.disconnect()
    }

    override suspend fun send(bytes: ByteArray) {
        client.send(bytes)
    }
}