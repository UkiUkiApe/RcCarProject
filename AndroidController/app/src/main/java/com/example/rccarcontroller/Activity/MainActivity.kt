package com.example.rccarcontroller.Activity

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rccarcontroller.Activity.Components.CarStatusSection
import com.example.rccarcontroller.Activity.Components.ConnectionState
import com.example.rccarcontroller.ui.theme.RcCarControllerTheme
import com.example.rccarcontroller.Activity.Components.ConnectionStatusSection
import com.example.rccarcontroller.Presentation.ViewModel.ControllerViewModel
import com.example.rccarcontroller.Presentation.ViewModel.Factory.ControllerViewModelFactory
import com.example.rccarcontroller.ui.components.ControllerPadSection

class MainActivity : ComponentActivity() {

    private val bluetoothPermissions = arrayOf(
        // UIから権限要求をかける必要がある
        android.Manifest.permission.BLUETOOTH_CONNECT
    )

    private val REQUEST_BLUETOOTH = 1001
    // この部分はMainActivity(エントリポイント)
    private val viewModel: ControllerViewModel by viewModels {
        // UI ⇔ ViewModel の境界面(依存性を注入している)
        ControllerViewModelFactory(this)
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        // この部分はUIの初期化処理を行う箇所である(はじめに一回だけ呼ばれる)
        super.onCreate(savedInstanceState)
        ensureBluetoothPermission()
        enableEdgeToEdge()

        setContent {
            // ここで実際にUIの初期化処理を行う
            RcCarControllerTheme {

                val connectionState by viewModel.connectionState.collectAsState()
                val speed by viewModel.speed.collectAsState()
                val battery by viewModel.batteryLevel.collectAsState()
                val temperature by viewModel.temperature.collectAsState()
                val errorCode by viewModel.errorCode.collectAsState()

                ControllerScreen(
                    connectionState = connectionState,
                    speed = speed,
                    battery = battery,
                    temperature = temperature,
                    errorCode = errorCode,
                    onConnect = { viewModel.connect() },
                    onDisconnect = { viewModel.disconnect() },
                    onForward = { viewModel.moveForward() },
                    onBackward = { viewModel.moveBackward() },
                    onLeft = { viewModel.turnLeft() },
                    onRight = { viewModel.turnRight() },
                    onStop = { viewModel.stop() },
                    onSpeedChange = { viewModel.setSpeed(it) }
                )
            }
        }
    }
    // ③ ensureBluetoothPermission はフィールドを使う
    private fun ensureBluetoothPermission(): Boolean {
        val granted = bluetoothPermissions.all {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }

        if (!granted) {
            requestPermissions(bluetoothPermissions, REQUEST_BLUETOOTH)
        }

        return granted
    }

    // ④ 権限結果の受け取り
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_BLUETOOTH) {
            val granted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (!granted) {
                // 権限がないと Bluetooth が使えないので UI に通知する
            }
        }
    }
}



@Composable
fun ControllerScreen(
    connectionState: ConnectionState,
    speed: Int,
    battery: Int?,
    temperature: Float?,
    errorCode: Int?,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onStop: () -> Unit,
    onSpeedChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // ① 通信状態表示部
        ConnectionStatusSection(
            connectionState = connectionState,
            errorCode = errorCode,
            onConnect = onConnect,
            onDisconnect = onDisconnect
        )

        // ② 車体状態表示部
        CarStatusSection(
            speed = speed,
            battery = battery,
            temperature = temperature
        )

        // ③ コントローラー部
        ControllerPadSection(
            onForward = onForward,
            onBackward = onBackward,
            onLeft = onLeft,
            onRight = onRight,
            onStop = onStop,
            speed = speed,
            onSpeedChange = onSpeedChange
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ControllerScreenPreview() {
    RcCarControllerTheme {
        ControllerScreen(
            connectionState = ConnectionState.DISCONNECTED,
            speed = 0,
            battery = 80,
            temperature = 32.5f,
            errorCode = null,
            onConnect = {},
            onDisconnect = {},
            onForward = {},
            onBackward = {},
            onLeft = {},
            onRight = {},
            onStop = {},
            onSpeedChange = {}
        )
    }
}