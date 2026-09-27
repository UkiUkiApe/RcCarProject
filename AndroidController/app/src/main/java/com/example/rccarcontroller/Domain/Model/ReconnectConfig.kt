package com.example.rccarcontroller.Domain.Model

/**
 * Bluetooth 接続の再接続戦略を定義する設定モデル。
 *
 * ControllerRepository が自動再接続を行う際に参照するパラメータ群であり、
 * config.json から読み込まれ、アプリケーションの通信戦略を外部設定として
 * 切り替え可能にするためのデータクラス。
 *
 * <p>
 * 【役割】
 * - 再接続機能の ON/OFF を制御する
 * - 再接続試行回数の上限を設定する
 * - 再接続試行間隔の初期値を設定する
 * - バックオフ係数により試行ごとに遅延を増加させる
 *
 * <p>
 * 【設計意図】
 * - 再接続戦略をコードではなく設定ファイルで管理することで、
 *   通信環境やデバイス特性に応じた柔軟な調整を可能にする
 * - Repository がこの設定を参照し、接続ロジックと戦略ロジックの責務を分離する
 * - Clean Architecture における「戦略の外部化」を実現する
 *
 * @property enabled 再接続機能を有効にするかどうか（true なら自動再接続を行う）
 * @property maxRetries 再接続を試行する最大回数
 * @property initialDelayMs 再接続試行の初回遅延（ミリ秒）
 * @property backoffFactor 遅延を増加させる倍率（指数バックオフ）
 */
data class ReconnectConfig(
    val enabled: Boolean,
    val maxRetries: Int,
    val initialDelayMs: Long,
    val backoffFactor: Int
)

