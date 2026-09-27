package com.example.rccarcontroller.InfraStructure.Client

import com.example.rccarcontroller.Activity.Components.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * 通信クライアントが共通して持つべきインターフェース。
 *
 * このインターフェースは、Bluetooth・Wi-Fi・WebSocket など、
 * さまざまな通信方式を統一的に扱うための抽象化レイヤーとして機能する。
 * 具体的な通信処理は Infrastructure 層の実装クラスが担当し、
 * 上位レイヤー（UseCase や Repository）は通信方式の違いを意識せずに利用できる。
 *
 * <p>
 * 【役割】
 * - 接続処理（connect）
 * - 切断処理（disconnect）
 * - バイト列の送信（send）
 * - 受信データのストリーム提供（incomingData）
 * - 接続状態の監視（state）
 *
 * <p>
 * 【Clean Architecture の観点】
 * - Domain 層はこの抽象インターフェースのみを参照し、
 *   Bluetooth やネットワーク API といった具体的技術に依存しない。
 * - Infrastructure 層が各通信方式の具体的な実装を提供する。
 * - 新しい通信方式（BLE、USB、HTTP など）を追加する場合も、
 *   このインターフェースを実装するだけで既存ロジックを壊さずに拡張可能。
 *
 * suspend 関数を採用しているため、接続・送信・切断処理は
 * コルーチン上で非同期的に実行されることを前提としている。
 */
interface CommunicationClient {
    /**
     * 指定された接続ターゲットへ接続を開始する。
     *
     * @param target 接続先（Bluetooth / Wi-Fi / WebSocket など）
     */
    suspend fun connect(target: ConnectionTarget)
    /**
     * 現在の接続を切断する。
     */
    suspend fun disconnect()
    /**
     * バイト列を送信する。
     *
     * @param bytes 送信するデータ
     */
    suspend fun send(bytes: ByteArray)
    /**
     * 受信したデータをストリームとして提供する Flow。
     * 非同期で複数回の受信が発生するため、Flow により逐次購読できる。
     */
    val incomingData: Flow<ByteArray>
    /**
     * 現在の接続状態を表す StateFlow。
     * CONNECTING / CONNECTED / ERROR / DISCONNECTED などの状態変化を購読できる。
     */
    val state: StateFlow<ConnectionState>
}