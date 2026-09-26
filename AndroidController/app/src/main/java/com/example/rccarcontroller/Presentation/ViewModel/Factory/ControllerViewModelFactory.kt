package com.example.rccarcontroller.Presentation.ViewModel.Factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rccarcontroller.Data.Bluetooth.BluetoothConfigRepository
import com.example.rccarcontroller.Data.Controller.ControllerRepositoryImpl
import com.example.rccarcontroller.Presentation.ViewModel.ControllerViewModel
import com.example.rccarcontroller.Domain.UseCase.ControllerUseCase
import com.example.rccarcontroller.Domain.UseCase.ControllerUseCaseImpl
import com.example.rccarcontroller.InfraStructure.Bluetooth.BluetoothDeviceResolver

class ControllerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val configRepository = BluetoothConfigRepository(context)
        val deviceResolver = BluetoothDeviceResolver(context)

        val repository = ControllerRepositoryImpl(
            configRepository,
            deviceResolver
        )

        val useCase = ControllerUseCaseImpl(repository)

        return ControllerViewModel(useCase) as T
    }
}

