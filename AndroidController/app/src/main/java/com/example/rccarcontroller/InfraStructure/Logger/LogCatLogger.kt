package com.example.rccarcontroller.InfraStructure.Logger

import android.util.Log

/**
 * Android の Logcat へログを出力する Logger 実装。
 *
 * Clean Architecture における Infrastructure 層のコンポーネントとして機能し、
 * Domain 層が Android API（Logcat）に依存しないようにするための抽象化ポイントを提供する。
 *
 * 主な責務:
 * - Logger インターフェースの info / error を Logcat の Log.d / Log.e に委譲する
 * - ログ出力先を Logcat に限定し、ファイル出力や複数出力は他の Logger 実装に委譲する
 *
 * このクラスは Logcat への出力のみを担当し、アプリ全体のログ戦略は
 * CompositeLogger（複数 Logger の集約）によって構築される。
 *
 * @see Logger ログ出力の抽象インターフェース
 * @see com.example.rccarcontroller.Data.Logger.CompositeLogger 複数 Logger をまとめて呼び出す集約ロガー
 */
class LogcatLogger : Logger {
    override fun info(message: String) {
        Log.d("AppLog", message)
    }

    override fun error(message: String, throwable: Throwable?) {
        Log.e("AppLog", message, throwable)
    }
}