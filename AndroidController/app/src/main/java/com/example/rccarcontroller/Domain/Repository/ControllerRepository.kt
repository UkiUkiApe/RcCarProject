package com.example.rccarcontroller.Domain.Repository

import com.example.rccarcontroller.Domain.Model.Command

/**
 * RCカー制御に必要な接続処理およびコマンド送信処理を抽象化する Repository インターフェース。
 *
 * Clean Architecture における Domain 層と Data 層の境界として機能し、
 * 上位層（UseCase / ViewModel / UI）が Bluetooth の詳細や Transport 実装に
 * 依存しないようにするための統一された制御 API を提供する。
 *
 * 主な責務:
 * - RCカーへの接続・切断処理を抽象化する
 * - Command を受け取り、適切な通信方式で送信する処理を抽象化する
 *
 * このインターフェースの具体的な実装（ControllerRepositoryImpl）は、
 * BluetoothConfigRepository や BluetoothDeviceResolver を利用して
 * Transport や Controller の初期化を行い、実際の通信処理を担当する。
 *
 * @see com.example.rccarcontroller.Domain.Controller.RcCarController
 * @see com.example.rccarcontroller.Data.Controller.ControllerRepositoryImpl
 */
interface ControllerRepository {
    fun connect(): Boolean
    fun disconnect(): Boolean
    fun send(command: Command): Boolean
}