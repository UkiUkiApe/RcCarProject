package com.example.rccarcontroller.Data.Controller

import android.util.Log
import com.example.rccarcontroller.Data.Bluetooth.BluetoothConfigRepository
import com.example.rccarcontroller.Domain.Controller.RcCarControllerImpl
import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.Repository.ControllerRepository
import com.example.rccarcontroller.Domain.Serializer.SimpleCommandSerializer
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothDeviceResolver
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothTransportImpl
import com.example.rccarcontroller.InfraStructure.Logger.Logger

/**
 * Bluetooth 接続およびコマンド送信を行うための Repository 実装。
 *
 * Clean Architecture における Data 層の中心として機能し、
 * 設定読み込み（BluetoothConfigRepository）とデバイス解決（BluetoothDeviceResolver）を統合し、
 * RcCarControllerImpl（Domain 層の制御ロジック）を構築する役割を持つ。
 *
 * 主な責務:
 * - BluetoothConfigRepository から接続設定（deviceName / uuid）を読み込む
 * - BluetoothDeviceResolver により、設定された deviceName に対応するペアリング済みデバイスを解決する
 * - 解決した BluetoothDevice と UUID を用いて BluetoothTransportImpl を生成する
 * - Transport と Serializer を組み合わせて RcCarControllerImpl を構築し、
 *   connect / disconnect / sendCommand の操作を提供する
 *
 * この Repository により、UI や UseCase 層は Bluetooth の詳細（デバイス探索、UUID、Transport 実装）
 * を一切意識せずに制御操作を行えるようになる。
 *
 * デバイスが見つからない場合や BLUETOOTH_CONNECT 権限が不足している場合は、
 * IllegalStateException を投げて初期化失敗を明確化する。
 *
 * @param configRepository Bluetooth 接続設定を読み込むための Repository
 * @param deviceResolver ペアリング済み Bluetooth デバイスを解決するための Resolver
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

    private val controller = RcCarControllerImpl(
        BluetoothTransportImpl(device, config.uuid),
        SimpleCommandSerializer()
    ).also {
        logger.info("Repository: RcCarControllerImpl created")
    }

    override fun connect(): Boolean {
        logger.info("Repository: connect() called")
        val result = controller.connect()
        logger.info("Repository: connect() result=$result")
        return result
    }

    override fun disconnect(): Boolean {
        logger.info("Repository: disconnect() called")
        val result = controller.disconnect()
        logger.info("Repository: disconnect() result=$result")
        return result
    }

    override fun send(command: Command): Boolean {
        logger.info("Repository: send() called: command=$command")
        val result = controller.sendCommand(command)
        logger.info("Repository: send() result=$result")
        return result
    }
}



