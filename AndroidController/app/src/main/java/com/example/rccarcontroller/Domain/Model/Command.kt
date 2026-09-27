package com.example.rccarcontroller.Domain.Model

/**
 * RCカーに送信する操作指令を表す Domain モデル。
 *
 * Clean Architecture における Domain 層の「操作イベント」として機能し、
 * UI や ViewModel が Bluetooth の送信形式やバイト列を意識せずに
 * 車体の操作を抽象化された Command として扱えるようにする。
 *
 * 主な責務:
 * - 前進・後退・左旋回・右旋回・停止などの基本操作を表現する
 * - Speed コマンドにより、速度変更を値付きイベントとして表現する
 * - CommandSerializer によって送信可能なバイト列へ変換され、
 *   BluetoothTransport を通じて RCカーへ送信される
 *
 * この sealed class により、操作指令の種類が型安全に管理され、
 * when 分岐やシリアライズ処理が網羅性チェックを受けられる。
 */
sealed class Command {
    object Forward : Command()
    object Backward : Command()
    object Left : Command()
    object Right : Command()
    object Stop : Command()
    data class Speed(val value: Int) : Command()
}
