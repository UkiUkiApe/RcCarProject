package com.example.rccarcontroller.Presentation.ViewModel.Factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rccarcontroller.Data.Bluetooth.BluetoothConfigRepository
import com.example.rccarcontroller.Data.Controller.ControllerRepositoryImpl
import com.example.rccarcontroller.Data.Logger.CompositeLogger
import com.example.rccarcontroller.Domain.UseCase.ControllerUseCaseImpl
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothDeviceResolver
import com.example.rccarcontroller.InfraStructure.Logger.FileLogger
import com.example.rccarcontroller.InfraStructure.Logger.LogcatLogger
import com.example.rccarcontroller.Presentation.ViewModel.ConnectionViewModel
import com.example.rccarcontroller.Presentation.ViewModel.ControllerViewModel

/**
 * アプリ全体で利用する ViewModel を生成するための Factory。
 *
 * Clean Architecture における Presentation 層と Data 層の境界として機能し、
 * ControllerRepository や Logger をアプリ内で 1 つだけ生成して共有することで、
 * BluetoothClient の二重生成や二重接続を防ぎ、通信状態を UI と正しく同期させる。
 *
 * <p>
 * 【役割】
 * - Logger（CompositeLogger）を 1 回だけ生成し、Logcat とファイル出力を統合する
 * - BluetoothConfigRepository と BluetoothDeviceResolver を初期化し、
 *   ControllerRepositoryImpl を Singleton として構築する
 * - Repository を利用して UseCase（ControllerUseCaseImpl）を生成する
 * - ControllerViewModel と ConnectionViewModel に同じ Repository / Logger を注入し、
 *   UI が一貫した通信状態を購読できるようにする
 *
 * <p>
 * 【設計意図】
 * - ViewModelFactory を統合し、通信基盤（Repository / BluetoothClient）を 1 つに集約する
 * - ControllerViewModel と ConnectionViewModel が同じ Repository を共有することで、
 *   接続状態・再接続状態・ログ出力がすべて一致するようにする
 * - Factory が依存性注入の中心となり、UI が Bluetooth の初期化手順を一切知らずに済むようにする
 *
 * <p>
 * 【生成される ViewModel】
 * - ControllerViewModel：RCカー操作（前進・後退・速度変更など）を担当
 * - ConnectionViewModel：接続状態・再接続状態を UI に公開する
 *
 * @see com.example.rccarcontroller.Presentation.ViewModel.ControllerViewModel
 * @see com.example.rccarcontroller.Presentation.ViewModel.ConnectionViewModel
 * @see com.example.rccarcontroller.Data.Controller.ControllerRepositoryImpl
 */
class AppViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    // ★ Repository と Logger を 1 回だけ生成する（Singleton）
    private val logger by lazy {
        CompositeLogger(
            listOf(
                LogcatLogger(),
                FileLogger(context)
            )
        )
    }

    private val repository by lazy {
        val configRepository = BluetoothConfigRepository(context, logger)
        val deviceResolver = BluetoothDeviceResolver(context, logger)

        ControllerRepositoryImpl(
            configRepository = configRepository,
            deviceResolver = deviceResolver,
            logger = logger
        )
    }

    val useCase = ControllerUseCaseImpl(repository, logger)


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {

            ControllerViewModel::class.java ->
                ControllerViewModel(useCase, logger) as T

            ConnectionViewModel::class.java ->
                ConnectionViewModel(repository, logger) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}