package com.example.rccarcontroller.InfraStructure.Logger

/**
 * アプリ内で利用されるログ出力の抽象インターフェース。
 *
 * Clean Architecture における Domain 層の「ログ出力ポイント」として機能し、
 * 下位層（Infrastructure）の具体的なログ出力方法（Logcat / ファイル書き込みなど）に
 * 上位層（UseCase / ViewModel / Repository / Controller）が依存しないようにするための抽象化を提供する。
 *
 * 主な責務:
 * - ログ出力の共通 API を定義し、実装クラスに出力方法を委譲する
 * - info / error の2種類のログレベルを提供し、用途に応じた出力を可能にする
 * - Domain や Data 層が Android API（Logcat）やファイル I/O に依存しないようにする
 *
 * このインターフェースは LogcatLogger や FileLogger、CompositeLogger と組み合わせて利用され、
 * アプリ全体のログ戦略を柔軟に構築できるようにする。
 *
 * @see LogcatLogger Android Logcat へのログ出力を担当する実装
 * @see FileLogger テキストファイルへのログ書き込みを担当する実装
 * @see com.example.rccarcontroller.Data.Logger.CompositeLogger 複数の Logger をまとめて呼び出す集約ロガー
 */
interface Logger {
    fun info(message: String)
    fun error(message: String, throwable: Throwable? = null)
}