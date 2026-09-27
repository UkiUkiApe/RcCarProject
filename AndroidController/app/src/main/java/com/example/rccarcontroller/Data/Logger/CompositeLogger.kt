package com.example.rccarcontroller.Data.Logger

import com.example.rccarcontroller.InfraStructure.Logger.Logger

/**
 * 複数の Logger 実装をまとめて呼び出す集約ロガー（Composite パターン）。
 *
 * Clean Architecture における Data 層のコンポーネントとして機能し、
 * Domain や UseCase、Repository などの上位層が単一の Logger インターフェースを通して
 * 複数のログ出力先（Logcat / ファイル / リモート送信など）を同時に利用できるようにする。
 *
 * 主な責務:
 * - info / error のログ呼び出しを保持している全 Logger に対して一括で委譲する
 * - ログ出力先の追加・削除を柔軟に行えるようにし、アプリ全体のログ戦略を拡張可能にする
 * - 上位層がログ出力先の具体実装（LogcatLogger / FileLogger）を意識しなくて済むようにする
 *
 * このクラスは LogcatLogger や FileLogger と組み合わせて利用され、
 * Factory で構築されることでアプリ全体の統一的なログ出力基盤を形成する。
 *
 * @param loggers info / error 呼び出しを委譲する Logger 実装のリスト
 *
 * @see Logger ログ出力の抽象インターフェース
 * @see com.example.rccarcontroller.InfraStructure.Logger.LogcatLogger Logcat へのログ出力を担当する実装
 * @see com.example.rccarcontroller.InfraStructure.Logger.FileLogger ファイルへのログ書き込みを担当する実装
 */
class CompositeLogger(
    private val loggers: List<Logger>
) : Logger {

    override fun info(message: String) {
        loggers.forEach { it.info(message) }
    }

    override fun error(message: String, throwable: Throwable?) {
        loggers.forEach { it.error(message, throwable) }
    }
}