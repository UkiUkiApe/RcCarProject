package com.example.rccarcontroller.Domain.Model

import java.util.UUID

data class BluetoothConfig(
    val deviceName: String,
    val uuid: UUID
)