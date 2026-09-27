package com.example.rccarcontroller.Domain.Controller

import com.example.rccarcontroller.Domain.Model.Command

/**
 * RCカーを制御するための抽象インターフェース。
 *
 * Clean Architecture における Domain 層の中心として機能し、
 * UI や ViewModel が Bluetooth や Transport の詳細を意識せずに
 * 車体の操作を行えるようにするための統一された制御 API を提供する。
 *
 * 主な責務:
 * - RCカーへの接続・切断処理を抽象化する
 * - 前進・後退・旋回・停止などのコマンド送信を抽象化する
 * - 速度変更を Command として送信するための setSpeed を提供する
 *
 * このインターフェースは、具体的な実装（RcCarControllerImpl）が
 * BluetoothTransport や CommandSerializer を用いて実際の通信処理を行うことで、
 * 上位層（UseCase / ViewModel / UI）が通信方式に依存しない設計を実現する。
 */
interface RcCarController {
    fun connect(): Boolean
    fun disconnect(): Boolean
    fun sendCommand(command: Command): Boolean
    fun setSpeed(speed: Int)
}