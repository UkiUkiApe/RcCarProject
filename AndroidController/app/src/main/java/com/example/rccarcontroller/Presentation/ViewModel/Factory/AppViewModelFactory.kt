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