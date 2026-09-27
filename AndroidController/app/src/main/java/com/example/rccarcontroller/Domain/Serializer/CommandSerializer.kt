package com.example.rccarcontroller.Domain.Serializer

import com.example.rccarcontroller.Domain.Model.Command

/**
 * RCカーに送信する Command を、BluetoothTransport が扱える ByteArray へ
 * 変換するためのシリアライザインターフェース。
 *
 * Clean Architecture における Domain 層の「コマンド変換ポイント」として機能し、
 * 上位層（UseCase / ViewModel / UI）が送信形式（文字列・バイト列）を
 * 一切意識せずに操作できるようにするための抽象化を提供する。
 *
 * 主な責務:
 * - Command を送信可能な形式（ByteArray）へ変換する
 * - 具体的な変換方式（プロトコル）は実装クラスに委譲する
 *
 * このインターフェースの実装（SimpleCommandSerializer）は、
 * RCカー制御用のシンプルな文字列プロトコル（例: "F", "B", "L", "R", "S", "V:10"）を採用し、
 * BluetoothTransport による送信処理と組み合わせて利用される。
 */
interface CommandSerializer {
    fun serialize(command: Command): ByteArray
}

class SimpleCommandSerializer : CommandSerializer {
    override fun serialize(command: Command): ByteArray {
        return when (command) {
            Command.Forward -> "F".toByteArray()
            Command.Backward -> "B".toByteArray()
            Command.Left -> "L".toByteArray()
            Command.Right -> "R".toByteArray()
            Command.Stop -> "S".toByteArray()
            is Command.Speed -> "V:${command.value}".toByteArray()
        }
    }
}
