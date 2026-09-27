package com.example.rccarcontroller.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import com.example.rccarcontroller.Activity.Components.ConnectionState
import com.example.rccarcontroller.Domain.Model.ReconnectState
import com.example.rccarcontroller.Domain.Repository.ControllerRepository
import com.example.rccarcontroller.InfraStructure.Logger.Logger
import kotlinx.coroutines.flow.StateFlow

/**
 * Bluetooth 接続状態および再接続状態を UI に公開するための ViewModel。
 *
 * Clean Architecture における Presentation 層として機能し、
 * Repository が発行する通信状態（clientState / reconnectState）を
 * UI（Compose）が購読できる形で提供する役割を持つ。
 *
 * <p>
 * 【役割】
 * - BluetoothClient の接続状態（CONNECTED / DISCONNECTED / ERROR など）を UI に流す
 * - 自動再接続戦略の状態（IDLE / RECONNECTING / FAILED）を UI に流す
 * - UI はこの ViewModel を通じて通信状態を監視し、Snackbar や表示制御を行う
 *
 * <p>
 * 【設計意図】
 * - 操作用 ViewModel（ControllerViewModel）と通信状態用 ViewModel を分離し、
 *   責務を明確化することで保守性と拡張性を高める
 * - Repository が通信戦略を管理し、ViewModel は状態を UI に橋渡しするだけにする
 * - UI は Repository の実装詳細（BluetoothClient や Transport）を一切知らずに済む
 *
 * @property connectionState BluetoothClient の接続状態を表す StateFlow
 * @property reconnectState 自動再接続戦略の状態を表す StateFlow
 *
 * @see com.example.rccarcontroller.Domain.Repository.ControllerRepository
 * @see com.example.rccarcontroller.Data.Controller.ControllerRepositoryImpl
 */
class ConnectionViewModel(
    private val repository: ControllerRepository,
    private val logger: Logger
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = repository.clientState
    val reconnectState: StateFlow<ReconnectState> = repository.reconnectState
}