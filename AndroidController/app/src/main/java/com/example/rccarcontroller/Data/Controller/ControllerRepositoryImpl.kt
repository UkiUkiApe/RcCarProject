package com.example.rccarcontroller.Data.Controller

import com.example.rccarcontroller.Activity.Components.ConnectionState
import com.example.rccarcontroller.Data.Bluetooth.BluetoothConfigRepository
import com.example.rccarcontroller.Domain.Controller.RcCarControllerImpl
import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.Model.ReconnectState
import com.example.rccarcontroller.Domain.Repository.ControllerRepository
import com.example.rccarcontroller.Domain.Serializer.SimpleCommandSerializer
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothDeviceResolver
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothTransportImpl
import com.example.rccarcontroller.InfraStructure.Client.BluetoothClient
import com.example.rccarcontroller.InfraStructure.Logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * RCカー制御のための Repository 実装。
 *
 * このクラスは Clean Architecture における Data 層の中心として機能し、
 * Bluetooth 接続の構築・制御ロジックの生成・再接続戦略の管理を担当する。
 * Domain 層の抽象である {@link ControllerRepository} を実装し、
 * 上位レイヤー（UseCase / ViewModel）が通信方式の詳細を意識せずに
 * connect / disconnect / sendCommand を利用できるようにする。
 *
 * <p>
 * 【主な責務】
 * - BluetoothConfigRepository から設定（deviceName / uuid / reconnect 設定）を読み込む
 * - BluetoothDeviceResolver により接続対象デバイスを解決する
 * - BluetoothClient / BluetoothTransportImpl / RcCarControllerImpl を構築する
 * - connect / disconnect / sendCommand を Domain 層に提供する
 * - BluetoothClient の接続状態を監視し、必要に応じて自動再接続を行う
 *
 * <p>
 * 【再接続ロジック】
 * - BluetoothClient.state を StateFlow として購読し、ERROR / DISCONNECTED を検知する
 * - config.json の reconnect 設定に基づき、再接続の有効/無効を判断する
 * - 最大試行回数・初期遅延・バックオフ係数を設定可能
 * - CoroutineScope により非同期で再接続処理を実行する
 *
 * <p>
 * 【設計上の特徴】
 * - Repository が通信戦略（再接続）を持ち、BluetoothClient は通信の生々しい処理のみ担当する
 * - Domain 層は ControllerRepository の抽象のみを参照し、通信方式に依存しない
 * - Infrastructure 層の責務が明確に分離され、拡張性が高い
 * - Logger により接続・切断・再接続・送受信の詳細ログを出力し、デバッグ性を向上
 *
 * <p>
 * 【拡張性】
 * - Wi-Fi や WebSocket など別通信方式を追加する場合も、
 *   CommunicationClient / Transport / Controller の差し替えで対応可能
 * - 再接続戦略は config.json により柔軟に変更できる
 *
 * @param configRepository Bluetooth 接続設定を読み込む Repository
 * @param deviceResolver ペアリング済み Bluetooth デバイスを解決する Resolver
 * @param logger ログ出力用の Logger 実装
 */
class ControllerRepositoryImpl(
    private val configRepository: BluetoothConfigRepository,
    private val deviceResolver: BluetoothDeviceResolver,
    private val logger: Logger
) : ControllerRepository {

    private val config = configRepository.load().also {
        logger.info("Repository: Loaded config: deviceName=${it.deviceName}, uuid=${it.uuid}")
    }

    private val device = deviceResolver.resolveByName(config.deviceName).also {
        if (it == null) {
            logger.error("Repository: Device not found or permission missing")
        } else {
            logger.info("Repository: Resolved device: ${it}")
        }
    } ?: throw IllegalStateException("Device not found or permission missing")

    private val client = BluetoothClient(
        device = device,
        uuid = config.uuid,
        scope = CoroutineScope(Dispatchers.IO),
        logger = logger
    )

    private val transport = BluetoothTransportImpl(client)
    private val controller = RcCarControllerImpl(
        transport,
        SimpleCommandSerializer()
    ).also {
        logger.info("Repository: RcCarControllerImpl created")
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _reconnectState = MutableStateFlow(ReconnectState.IDLE)
    // こちらは自身の持っている状態を公開する
    override val reconnectState: StateFlow<ReconnectState> = _reconnectState

    // こちらは下から上がってくる通知を公開する(ControllerRepositoryは転送者)
    override val clientState: StateFlow<ConnectionState>
        get() = client.state

    init {
        monitorConnectionState()
    }

    private fun monitorConnectionState() {
        scope.launch {
            client.state.collect { state ->
                if (state == ConnectionState.ERROR || state == ConnectionState.DISCONNECTED) {
                    if (config.reconnect.enabled) {
                        logger.error("Repository: Connection lost. Auto-reconnect enabled.")
                        attemptReconnect()
                    } else {
                        logger.error("Repository: Connection lost. Auto-reconnect disabled.")
                    }
                }
            }
        }
    }

    private suspend fun attemptReconnect() {
        _reconnectState.value = ReconnectState.RECONNECTING

        var retry = 0
        val max = config.reconnect.maxRetries
        var delayMs = config.reconnect.initialDelayMs

        while (retry < max) {
            retry++
            logger.info("Repository: Reconnect attempt $retry / $max (delay=${delayMs}ms)")
            delay(delayMs)

            val result = connect()
            if (result) {
                logger.info("Repository: Reconnected successfully")
                _reconnectState.value = ReconnectState.IDLE
                return
            }

            delayMs *= config.reconnect.backoffFactor
            logger.error("Repository: Reconnect attempt $retry failed")
        }

        logger.error("Repository: Max reconnect attempts reached. Giving up.")
        _reconnectState.value = ReconnectState.FAILED
    }

    override suspend fun connect(): Boolean {
        logger.info("Repository: connect() called")
        val result = controller.connect()
        logger.info("Repository: connect() result=$result")
        return result
    }

    override suspend fun disconnect(): Boolean {
        logger.info("Repository: disconnect() called")
        val result = controller.disconnect()
        logger.info("Repository: disconnect() result=$result")
        return result
    }

    override suspend fun send(command: Command): Boolean {
        logger.info("Repository: send() called: command=$command")
        val result = controller.sendCommand(command)
        logger.info("Repository: send() result=$result")
        return result
    }
}



