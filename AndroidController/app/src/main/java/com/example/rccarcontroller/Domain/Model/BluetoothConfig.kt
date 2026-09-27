package com.example.rccarcontroller.Domain.Model

import java.util.UUID

/**
 * Bluetooth 接続に必要な設定情報を保持する Domain モデル。
 *
 * 主な責務:
 * - RCカーと接続するための Bluetooth デバイス名を保持する
 * - 通信に使用する UUID（RFCOMM/SPP など）を保持する
 *
 * このモデルは Data 層の BluetoothConfigRepository によって
 * 外部ファイル（assets/config.json）から読み込まれ、
 * Repository → Transport → Controller の初期化処理に利用される。
 *
 * UI や ViewModel が Bluetooth の詳細設定を意識せずに済むように、
 * 接続設定をひとまとめにした抽象化された構造体として機能する。
 *
 * @param deviceName ペアリング済み Bluetooth デバイスの名前
 * @param uuid 通信に使用する Bluetooth UUID（通常は SPP の UUID）
 */
data class BluetoothConfig(
    val deviceName: String,
    val uuid: UUID
)