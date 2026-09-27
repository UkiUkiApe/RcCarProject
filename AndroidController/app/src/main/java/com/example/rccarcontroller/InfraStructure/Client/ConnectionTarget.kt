package com.example.rccarcontroller.InfraStructure.Client

import android.bluetooth.BluetoothDevice

/**
 * 通信クライアントが接続先を抽象的に扱うためのターゲット定義。
 *
 * この sealed class は、Bluetooth・Wi-Fi（TCP）・WebSocket など、
 * 異なる通信方式を単一の抽象として統一する役割を持つ。
 * これにより、上位レイヤー（UseCase や Repository）は
 * 具体的な通信方式に依存せずに接続処理を記述できる。
 *
 * <p>
 * 【Clean Architecture の観点】
 * - Domain 層は ConnectionTarget の抽象のみを参照し、
 *   Android API やネットワーク詳細に依存しない。
 * - Infrastructure 層が各通信方式の具体的な接続処理を提供する。
 * - 新しい通信方式（BLE、USB、HTTP など）を追加する場合も、
 *   ConnectionTarget を拡張するだけで既存ロジックを壊さずに対応可能。
 * </p>
 *
 * 各ターゲットの説明:
 * - Bluetooth: ペアリング済み BluetoothDevice を用いた RFCOMM/SPP 接続。
 * - Wifi: IP アドレスとポート番号を用いた TCP 通信。
 * - WebSocket: WebSocket サーバーへの接続 URL を指定する。
 *
 * この抽象化により、柔軟で拡張性の高い通信アーキテクチャを構築できる。
 */
sealed class ConnectionTarget {
    // 拡張性を考えてWifiとSocket通信を追加する
    /**
     * Bluetooth RFCOMM/SPP 接続を表すターゲット。
     *
     * @param device ペアリング済みの BluetoothDevice
     */
    data class Bluetooth(val device: BluetoothDevice) : ConnectionTarget()
    /**
     * TCP/IP を用いた Wi-Fi 接続を表すターゲット。
     *
     * @param ip   接続先デバイスの IP アドレス
     * @param port TCP ポート番号
     */
    data class Wifi(val ip: String, val port: Int) : ConnectionTarget()

    /**
     * WebSocket 接続を表すターゲット。
     *
     * @param url WebSocket の接続 URL（例: ws://192.168.0.10:8080/ws）
     */
    data class WebSocket(val url: String) : ConnectionTarget()
}