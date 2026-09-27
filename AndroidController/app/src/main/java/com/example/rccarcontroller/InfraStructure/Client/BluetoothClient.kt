package com.example.rccarcontroller.InfraStructure.Client

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.example.rccarcontroller.Activity.Components.ConnectionState
import com.example.rccarcontroller.InfraStructure.Logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

/**
 * Bluetooth RFCOMM/SPP 通信を行うためのクライアント実装。
 *
 * このクラスは {@link CommunicationClient} の Bluetooth 版具体実装であり、
 * 接続処理・切断処理・データ送信・データ受信・接続状態監視を担当する。
 * Android の BluetoothSocket を内部で管理し、非同期処理は CoroutineScope により実行される。
 *
 * <p>
 * 【主な責務】
 * - 指定された BluetoothDevice と UUID を用いて RFCOMM ソケットを生成・接続する
 * - InputStream / OutputStream を介してバイト列の送受信を行う
 * - 受信データを Flow<ByteArray> として公開する
 * - 接続状態を StateFlow<ConnectionState> として公開する
 * - 読み取りループ・タイムアウト監視・ソケット状態監視をバックグラウンドで実行する
 *
 * <p>
 * 【設計上の特徴】
 * - 非同期処理は CoroutineScope により管理され、UI スレッドをブロックしない
 * - Flow / StateFlow を用いることでリアクティブな状態監視・データ購読が可能
 * - Logger により接続処理・受信処理・異常検知などの詳細ログを出力し、
 *   デバッグ性を高めている
 *
 * <p>
 * 【Clean Architecture の観点】
 * - このクラスは Infrastructure 層に属し、Bluetooth の具体的な通信処理を担当する
 * - 上位レイヤー（UseCase / Repository）は CommunicationClient の抽象のみを参照し、
 *   Bluetooth の技術詳細に依存しない
 * - 他の通信方式（Wi-Fi, WebSocket, BLE など）を追加する場合も、
 *   CommunicationClient を実装するだけで拡張可能
 *
 * @param device 接続対象の BluetoothDevice（ペアリング済み）
 * @param uuid   RFCOMM/SPP 接続に使用する UUID
 * @param scope  非同期処理を実行するための CoroutineScope
 * @param logger ログ出力用の Logger 実装
 */
class BluetoothClient(
    val device: BluetoothDevice,
    val uuid: UUID,
    private val scope: CoroutineScope,
    private val logger: Logger
) : CommunicationClient {

    private val _state = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val state: StateFlow<ConnectionState> = _state

    private val _incomingData = MutableSharedFlow<ByteArray>()
    override val incomingData: Flow<ByteArray> = _incomingData

    private var socket: BluetoothSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    private var lastReceivedTime = System.currentTimeMillis()

    override suspend fun connect(target: ConnectionTarget) {
        logger.info("BluetoothClient: connect() called. device=${device}, uuid=$uuid")
        _state.value = ConnectionState.CONNECTING

        try {
            logger.info("BluetoothClient: Creating socket...")
            socket = device.createRfcommSocketToServiceRecord(uuid)
            logger.info("BluetoothClient: Connecting socket...")
            socket?.connect()

            input = socket?.inputStream
            output = socket?.outputStream

            logger.info("BluetoothClient: Connected successfully")
            _state.value = ConnectionState.CONNECTED

            startReading()
            startTimeoutWatcher()
            startSocketMonitor()

        } catch (e: IOException) {
            logger.error("BluetoothClient: Connection failed", e)
            _state.value = ConnectionState.ERROR
        }
    }
    override suspend fun disconnect() {
        logger.info("BluetoothClient: disconnect() called")
        _state.value = ConnectionState.DISCONNECTING

        try {
            socket?.close()
            logger.info("BluetoothClient: Socket closed")
        } catch (e: IOException) {
            logger.error("BluetoothClient: Error closing socket", e)
        }

        socket = null
        input = null
        output = null

        logger.info("BluetoothClient: Disconnected")
        _state.value = ConnectionState.DISCONNECTED
    }

    override suspend fun send(bytes: ByteArray) {
        try {
            output?.write(bytes)
        } catch (e: IOException) {
            _state.value = ConnectionState.ERROR
        }
    }

    private fun startReading() {
        logger.info("BluetoothClient: startReading() launched")
        scope.launch {
            try {
                val buffer = ByteArray(1024)

                while (true) {
                    val size = input?.read(buffer) ?: break
                    lastReceivedTime = System.currentTimeMillis()
                    logger.info("BluetoothClient: Received $size bytes")
                    _incomingData.emit(buffer.copyOf(size))
                }
                logger.info("BluetoothClient: Reading loop ended")
            } catch (e: IOException) {
                logger.error("BluetoothClient: Reading error", e)
                _state.value = ConnectionState.ERROR
            }
        }
    }

    private fun startTimeoutWatcher() {
        logger.info("BluetoothClient: startTimeoutWatcher() launched")
        scope.launch {
            while (true) {
                delay(1000.milliseconds)
                val elapsed = System.currentTimeMillis() - lastReceivedTime
                if (elapsed > 5000) {
                    logger.error("BluetoothClient: Timeout detected (elapsed=${elapsed}ms)")
                    _state.value = ConnectionState.ERROR
                }
            }
        }
    }
    private fun startSocketMonitor() {
        logger.info("BluetoothClient: startSocketMonitor() launched")
        scope.launch {
            while (true) {
                delay(1000.milliseconds)
                if (socket?.isConnected == false) {
                    logger.error("BluetoothClient: Socket disconnected")
                    _state.value = ConnectionState.ERROR
                }
            }
        }
    }
}