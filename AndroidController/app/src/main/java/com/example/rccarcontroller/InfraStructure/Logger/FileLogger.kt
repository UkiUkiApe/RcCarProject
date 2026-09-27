package com.example.rccarcontroller.InfraStructure.Logger

import android.content.Context
import java.io.File

/**
 * ログメッセージをアプリ内部ストレージのテキストファイルへ書き込む Logger 実装。
 *
 * Clean Architecture における Infrastructure 層のコンポーネントとして機能し、
 * Domain 層や Data 層がファイル I/O に直接依存しないようにするための抽象化ポイントを提供する。
 *
 * 主な責務:
 * - info / error ログをアプリ内部ストレージ（filesDir）配下の app_log.txt に追記する
 * - ログの永続化を担当し、LogcatLogger では取得できない運用時のログ収集を可能にする
 * - ログ出力先を「ファイル」に限定し、複数出力の統合は CompositeLogger に委譲する
 *
 * このクラスは LogcatLogger と組み合わせて利用されることが多く、
 * CompositeLogger によって Logcat とファイルの両方へ同時にログを出力する構成を実現できる。
 *
 * @param context アプリ内部ストレージへのアクセスに必要な Android の Context
 *
 * @see Logger ログ出力の抽象インターフェース
 * @see LogcatLogger Android Logcat へのログ出力を担当する実装
 * @see com.example.rccarcontroller.Data.Logger.CompositeLogger 複数 Logger をまとめて呼び出す集約ロガー
 */
class FileLogger(private val context: Context) : Logger {

    private val file = File(context.filesDir, "app_log.txt")

    override fun info(message: String) {
        file.appendText("[INFO] $message\n")
    }

    override fun error(message: String, throwable: Throwable?) {
        file.appendText("[ERROR] $message ${throwable?.stackTraceToString()}\n")
    }
}