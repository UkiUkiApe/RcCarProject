package com.example.rccarcontroller.Domain.Repository

import com.example.rccarcontroller.Activity.Components.ConnectionState
import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.Model.ReconnectState
import kotlinx.coroutines.flow.StateFlow

/**
 * RCカー制御に必要な接続処理およびコマンド送信処理を抽象化する Repository インターフェース。
 *
 * Clean Architecture における Domain 層と Data 層の境界として機能し、
 * 上位層（UseCase / ViewModel / UI）が BluetoothClient や Transport の詳細に
 * 依存しないようにするための統一された制御 API を提供する。
 *
 * <p>
 * 【役割】
 * - RCカーへの接続・切断処理を抽象化し、通信方式に依存しない操作を提供する
 * - Command を受け取り、適切なシリアライザと Transport を用いて送信する処理を抽象化する
 * - BluetoothClient の接続状態（clientState）を UI に公開する
 * - 再接続戦略の状態（reconnectState）を UI に公開し、通信状態の変化を通知する
 *
 * <p>
 * 【設計意図】
 * - Domain 層が通信方式（Bluetooth / Wi-Fi / WebSocket など）に依存しないようにする
 * - Repository が通信戦略（接続・切断・再接続）を一元管理し、責務を明確化する
 * - ViewModel は Repository の抽象を介して状態を購読するだけにし、UI ロジックと通信ロジックを分離する
 *
 * <p>
 * 【実装】
 * - 具体的な実装は ControllerRepositoryImpl が担当し、
 *   BluetoothConfigRepository・BluetoothDeviceResolver・BluetoothClient を組み合わせて
 *   Transport や RcCarController を初期化する。
 *
 * @property clientState BluetoothClient の接続状態を表す StateFlow（CONNECTED / DISCONNECTED / ERROR など）
 * @property reconnectState 自動再接続戦略の状態を表す StateFlow（IDLE / RECONNECTING / FAILED）
 *
 * @see com.example.rccarcontroller.Domain.Controller.RcCarController
 * @see com.example.rccarcontroller.Data.Controller.ControllerRepositoryImpl
 */
interface ControllerRepository {
    suspend fun connect(): Boolean
    suspend fun disconnect(): Boolean
    suspend fun send(command: Command): Boolean

    val clientState: StateFlow<ConnectionState>
    val reconnectState: StateFlow<ReconnectState>
}