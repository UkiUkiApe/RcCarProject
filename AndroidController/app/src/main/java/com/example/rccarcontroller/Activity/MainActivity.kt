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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rccarcontroller.Activity.Components.CarStatusSection
import com.example.rccarcontroller.Activity.Components.ConnectionState
import com.example.rccarcontroller.ui.theme.RcCarControllerTheme
import com.example.rccarcontroller.Activity.Components.ConnectionStatusSection
import com.example.rccarcontroller.Domain.Model.ReconnectState
import com.example.rccarcontroller.Presentation.ViewModel.ConnectionViewModel
import com.example.rccarcontroller.Presentation.ViewModel.ControllerViewModel
import com.example.rccarcontroller.Presentation.ViewModel.Factory.AppViewModelFactory
import com.example.rccarcontroller.Presentation.ViewModel.Factory.ConnectionViewModelFactory
import com.example.rccarcontroller.Presentation.ViewModel.Factory.ControllerViewModelFactory
import com.example.rccarcontroller.ui.components.ControllerPadSection


/**
 * RCカー制御アプリケーションのエントリポイントとなる Activity。
 *
 * Clean Architecture における Presentation 層の初期化を担当し、
 * AppViewModelFactory を用いて ControllerViewModel と ConnectionViewModel を生成することで、
 * UI と通信基盤（Repository / UseCase / BluetoothClient）を接続する役割を持つ。
 *
 * <p>
 * 【役割】
 * - Bluetooth 接続に必要な権限（BLUETOOTH_CONNECT）の確認と要求を行う
 * - AppViewModelFactory を利用して依存性注入を行い、通信基盤をアプリ内で 1 つに統合する
 * - Compose による UI のルート画面（ControllerScreen）を構築する
 * - ViewModel が公開する StateFlow（接続状態・再接続状態・車体ステータス）を UI に反映する
 *
 * <p>
 * 【設計意図】
 * - Activity は UI 初期化と権限処理に限定し、通信ロジックを一切持たない
 * - ViewModelFactory に依存性注入を集約することで、MainActivity の責務を最小化する
 * - UI は ViewModel を通じて Repository の状態を購読し、Bluetooth の詳細を知らずに済む
 *
 * <p>
 * 【UI 構成】
 * - ConnectionStatusSection：接続状態と接続／切断ボタン
 * - CarStatusSection：速度・バッテリー・温度などの車体ステータス
 * - ControllerPadSection：前進・後退・左右・停止・速度変更の操作パッド
 * - Snackbar：再接続中／再接続失敗の通知
 *
 * @see com.example.rccarcontroller.Presentation.ViewModel.Factory.AppViewModelFactory
 * @see com.example.rccarcontroller.Presentation.ViewModel.ControllerViewModel
 * @see com.example.rccarcontroller.Presentation.ViewModel.ConnectionViewModel
 */
class MainActivity : ComponentActivity() {

    private val bluetoothPermissions = arrayOf(
        // UIから権限要求をかける必要がある
        android.Manifest.permission.BLUETOOTH_CONNECT
    )



    private val REQUEST_BLUETOOTH = 1001
    // この部分はMainActivity(エントリポイント)
    private val appFactory by lazy { AppViewModelFactory(this) }

    private val controllerViewModel: ControllerViewModel by viewModels { appFactory }
    private val connectionViewModel: ConnectionViewModel by viewModels { appFactory }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        // この部分はUIの初期化処理を行う箇所である(はじめに一回だけ呼ばれる)
        super.onCreate(savedInstanceState)
        ensureBluetoothPermission()
        enableEdgeToEdge()

        setContent {
            // ここで実際にUIの初期化処理を行う
            RcCarControllerTheme {

                val reconnectState by connectionViewModel.reconnectState.collectAsState()
                val connectionState by connectionViewModel.connectionState.collectAsState()
                val speed by controllerViewModel.speed.collectAsState()
                val battery by controllerViewModel.batteryLevel.collectAsState()
                val temperature by controllerViewModel.temperature.collectAsState()
                val errorCode by controllerViewModel.errorCode.collectAsState()

                ControllerScreen(
                    connectionState = connectionState,
                    speed = speed,
                    battery = battery,
                    temperature = temperature,
                    errorCode = errorCode,
                    reconnectState = reconnectState,
                    onConnect = { controllerViewModel.connect() },
                    onDisconnect = { controllerViewModel.disconnect() },
                    onForward = { controllerViewModel.moveForward() },
                    onBackward = { controllerViewModel.moveBackward() },
                    onLeft = { controllerViewModel.turnLeft() },
                    onRight = { controllerViewModel.turnRight() },
                    onStop = { controllerViewModel.stop() },
                    onSpeedChange = { controllerViewModel.setSpeed(it) }
                )
            }
        }
    }
    // Bluetoothの権限を取得しにいく
    private fun ensureBluetoothPermission(): Boolean {
        val granted = bluetoothPermissions.all {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }

        if (!granted) {
            requestPermissions(bluetoothPermissions, REQUEST_BLUETOOTH)
        }

        return granted
    }

    // 権限確認を行ったのち確認を行う
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
    // この部分で画面を組み立てる
    connectionState: ConnectionState,
    speed: Int,
    battery: Int?,
    temperature: Float?,
    errorCode: Int?,
    reconnectState: ReconnectState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onStop: () -> Unit,
    onSpeedChange: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(reconnectState) {
        when (reconnectState) {
            ReconnectState.RECONNECTING ->
                snackbarHostState.showSnackbar("再接続中です…")

            ReconnectState.FAILED ->
                snackbarHostState.showSnackbar("再接続に失敗しました")

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SnackbarHost(hostState = snackbarHostState)

        // 通信状態表示部の組み立て
        ConnectionStatusSection(
            connectionState = connectionState,
            errorCode = errorCode,
            onConnect = onConnect,
            onDisconnect = onDisconnect
        )

        // 車体状態表示部の組み立て
        CarStatusSection(
            speed = speed,
            battery = battery,
            temperature = temperature
        )

        // コントローラー部の組み立て
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
// ここではプレビューを組み立てる
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
            reconnectState = ReconnectState.IDLE,
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

@Preview
@Composable
fun SnackbarPreview() {
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        hostState.showSnackbar("プレビュー用 Snackbar")
    }

    SnackbarHost(hostState = hostState)
}