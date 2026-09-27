package com.example.rccarcontroller.Data.Bluetooth

import android.content.Context
import android.util.Log
import com.example.rccarcontroller.Domain.Model.BluetoothConfig
import com.example.rccarcontroller.InfraStructure.Logger.Logger
import org.json.JSONObject
import java.util.UUID

/**
 * Bluetooth 接続に必要な設定情報を外部ファイル（assets/config.json）から読み込む Repository。
 *
 * UI や Domain 層が Bluetooth 設定の保存場所や JSON 形式を意識しなくて済むように、
 * 設定読み込み処理を Data 層にカプセル化する役割を持つ。
 *
 * 主な責務:
 * - assets 配下の config.json を読み込み、文字列として取得する
 * - JSON を解析し、BluetoothConfig（deviceName / uuid）としてアプリ内部表現へ変換する
 * - 設定値をコードにハードコードせず、外部ファイル管理を可能にすることで保守性を向上させる
 *
 * この Repository は BluetoothDeviceResolver や Transport 実装と組み合わせて、
 * Bluetooth 接続処理の構成要素として利用される。
 *
 * @param context assets へのアクセスに必要な Android の Context
 */
class BluetoothConfigRepository(
    private val context: Context,
    private val logger: Logger
) {
    fun load(): BluetoothConfig {
        logger.info("Config: load() called")

        //Jsonファイルの読み込みを行う
        val json = context.assets.open("Config.json")
            .bufferedReader()
            .readText()

        logger.info("Config: config.json loaded: $json")

        return parse(json)
    }

    private fun parse(json: String): BluetoothConfig {
        // 読み込んだJsonファイルを解析する
        val obj = JSONObject(json)

        val deviceName = obj.getString("deviceName")
        val uuid = obj.getString("uuid")

        logger.info("Config: Parsed config: deviceName=$deviceName, uuid=$uuid")

        return BluetoothConfig(
            deviceName = deviceName,
            uuid = UUID.fromString(uuid)
        )
    }
}