package com.example.rccarcontroller.InfraStructure.Bluetooth

/**
 * RCカーへの Bluetooth 通信を抽象化するための Transport インターフェース。
 *
 * Clean Architecture における Infrastructure 層の通信ポイントとして機能し、
 * 上位層（Repository / UseCase / ViewModel / UI）が BluetoothSocket や
 * Android の Bluetooth API に直接依存しないようにするための抽象化を提供する。
 *
 * 主な責務:
 * - RCカーとの Bluetooth 接続を開始する connect()
 * - 接続を安全に終了する disconnect()
 * - シリアライズ済みのバイト列を RCカーへ送信する send()
 *
 * このインターフェースの具体的な実装（BluetoothTransportImpl）は、
 * RFCOMM/SPP を用いた BluetoothSocket 通信を行い、
 * RcCarControllerImpl や ControllerRepositoryImpl から利用される。
 *
 * Transport を抽象化することで、通信方式（Bluetooth / Wi-Fi / USB など）の変更が
 * 上位層に影響しない柔軟な設計を実現している。
 */
interface BluetoothTransport {
    suspend fun connect()
    suspend fun disconnect()
    suspend fun send(bytes: ByteArray)
}