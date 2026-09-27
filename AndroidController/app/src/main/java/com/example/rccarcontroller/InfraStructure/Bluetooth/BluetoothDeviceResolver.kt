package com.example.rccarcontroller.InfraStructure.Bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.rccarcontroller.InfraStructure.Logger.Logger

/**
 * ペアリング済み Bluetooth デバイスを名前で検索し、対応する BluetoothDevice を解決するためのクラス。
 *
 * Android の BluetoothAdapter や BLUETOOTH_CONNECT 権限チェックといった
 * OS 依存の処理を Infrastructure 層に隔離することで、
 * Repository や Domain 層が Android API に直接依存しないようにする役割を持つ。
 *
 * 主な責務:
 * - BluetoothAdapter からペアリング済みデバイス一覧（bondedDevices）を取得する
 * - BLUETOOTH_CONNECT 権限が未許可の場合は安全に null を返す
 * - デバイス名で一致する BluetoothDevice を検索し、最初に一致したものを返す
 *
 * この Resolver は ControllerRepositoryImpl などから利用され、
 * 設定ファイルで指定された deviceName に対応する実デバイスを解決するために使われる。
 *
 * @param context 権限チェックに必要な Android の Context
 */
class BluetoothDeviceResolver(
    private val context: Context,
    private val logger: Logger
) {

    @SuppressLint("MissingPermission")
    fun resolveByName(name: String): BluetoothDevice? {
        logger.info("BT-Resolver: resolveByName() called: name=$name")
        val adapter = BluetoothAdapter.getDefaultAdapter()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.error("BT-Resolver: BLUETOOTH_CONNECT permission not granted")
            return null
        }

        val device = adapter.bondedDevices.firstOrNull { it.name == name }

        logger.info("BT-Resolver: resolve result: ${device?.name ?: "not found"}")
        return device
    }
}