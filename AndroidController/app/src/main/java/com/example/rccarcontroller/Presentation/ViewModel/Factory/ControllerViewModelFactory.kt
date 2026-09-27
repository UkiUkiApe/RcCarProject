package com.example.rccarcontroller.Presentation.ViewModel.Factory

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rccarcontroller.Data.Bluetooth.BluetoothConfigRepository
import com.example.rccarcontroller.Data.Controller.ControllerRepositoryImpl
import com.example.rccarcontroller.Data.Logger.CompositeLogger
import com.example.rccarcontroller.Presentation.ViewModel.ControllerViewModel
import com.example.rccarcontroller.Domain.UseCase.ControllerUseCaseImpl
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothDeviceResolver
import com.example.rccarcontroller.InfraStructure.Logger.FileLogger
import com.example.rccarcontroller.InfraStructure.Logger.LogcatLogger

/**
 * ControllerViewModel を生成するための Factory。
 *
 * MVVM + Clean Architecture の依存性注入ポイントとして機能し、
 * ViewModel が直接 Android API や具体的な Repository 実装に依存しないようにする。
 *
 * この Factory は以下のレイヤー構造を組み立てる役割を持つ：
 *
 * - Infrastructure 層:
 *   - BluetoothConfigRepository: Bluetooth 設定（デバイス名・UUID）を読み込む
 *   - BluetoothDeviceResolver: ペアリング済み Bluetooth デバイスを解決する
 *
 * - Data 層:
 *   - ControllerRepositoryImpl: ConfigRepository と Resolver を統合し、
 *     Bluetooth 接続やデバイス操作のための抽象化されたデータアクセスを提供する
 *
 * - Domain 層:
 *   - ControllerUseCaseImpl: connect / sendCommand などのアプリケーションロジックを提供し、
 *     UI 層が Bluetooth の詳細を意識せず操作できるようにする
 *
 * これらの依存関係を組み立てた上で、UI 層が利用する ControllerViewModel を生成する。
 *
 * @param context Android の Context。Repository や Resolver の生成に必要。
 * @return ControllerViewModel のインスタンス
 */
class ControllerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    // RepositoryとUseCaseを組み立てる部分
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("VM-Factory", "create() called for ${modelClass.simpleName}")

        val logger = CompositeLogger(
            listOf(
                LogcatLogger(),
                FileLogger(context)
            )
        )

        logger.info("VM-Factory Creating BluetoothConfigRepository")
        // BluetoothのRepositoryとResolverを作成する
        val configRepository = BluetoothConfigRepository(context, logger)
        logger.info("VM-Factory Creating BluetoothDeviceResolver")
        val deviceResolver = BluetoothDeviceResolver(context, logger)

        // Controller関連のRepositoryを作成(Data層)
        logger.info("VM-Factory Creating ControllerRepositoryImpl")
        val repository = ControllerRepositoryImpl(
            configRepository,
            deviceResolver,
            logger
        )
        // UseCaseのController作成(業務ロジック)
        logger.info("VM-Factory Creating ControllerUseCaseImpl")
        val useCase = ControllerUseCaseImpl(repository, logger)
        logger.info("VM-Factory ControllerViewModel created")
        return ControllerViewModel(useCase) as T
    }
}

