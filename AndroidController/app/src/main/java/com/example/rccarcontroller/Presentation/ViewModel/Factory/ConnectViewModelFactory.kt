package com.example.rccarcontroller.Presentation.ViewModel.Factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rccarcontroller.Data.Controller.ControllerRepositoryImpl
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothDeviceResolver
import com.example.rccarcontroller.Data.Bluetooth.BluetoothConfigRepository
import com.example.rccarcontroller.Data.Logger.CompositeLogger
import com.example.rccarcontroller.InfraStructure.Logger.FileLogger
import com.example.rccarcontroller.InfraStructure.Logger.LogcatLogger
import com.example.rccarcontroller.InfraStructure.Logger.Logger
import com.example.rccarcontroller.Presentation.ViewModel.ConnectionViewModel

/**
 * ConnectionViewModel を生成するための Factory。
 *
 * Clean Architecture における Presentation 層と Data 層の境界として機能し、
 * UI が Bluetooth の詳細や Repository の具体的な初期化手順を意識せずに
 * ConnectionViewModel を利用できるようにするための依存性注入ポイント。
 *
 * <p>
 * 【役割】
 * - Logger（CompositeLogger）を構築し、Logcat とファイル出力の両方を有効化する
 * - BluetoothConfigRepository を生成し、config.json から接続設定を読み込む
 * - BluetoothDeviceResolver を生成し、接続対象デバイスを解決する
 * - ControllerRepositoryImpl を組み立て、通信処理（接続・切断・再接続・送信）を担当させる
 * - 上記の依存関係を ConnectionViewModel に注入し、UI が通信状態を購読できるようにする
 *
 * <p>
 * 【設計意図】
 * - ViewModel が Android API や Bluetooth の具体的な初期化処理に依存しないようにする
 * - Repository の構築手順を Factory に集約し、責務を明確化する
 * - UI は Factory を通じて ViewModel を取得するだけで、通信基盤の詳細を知らずに済む
 *
 * @see com.example.rccarcontroller.Presentation.ViewModel.ConnectionViewModel
 * @see com.example.rccarcontroller.Data.Controller.ControllerRepositoryImpl
 */
class ConnectionViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val logger = CompositeLogger(
            listOf(
                LogcatLogger(),
                FileLogger(context)
            )
        )
        val configRepository = BluetoothConfigRepository(context, logger)
        val deviceResolver = BluetoothDeviceResolver(context, logger)

        val repository = ControllerRepositoryImpl(
            configRepository = configRepository,
            deviceResolver = deviceResolver,
            logger = logger
        )

        return ConnectionViewModel(
            repository = repository,
            logger = logger
        ) as T
    }
}