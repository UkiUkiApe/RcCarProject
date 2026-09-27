package com.example.rccarcontroller.Domain.UseCase

import com.example.rccarcontroller.Domain.Model.Command


/**
 * RCカー制御に関するアプリケーションロジックを提供する UseCase インターフェース。
 *
 * Clean Architecture における Domain 層の「ユースケース（操作の意味づけ）」として機能し、
 * 上位層（ViewModel / UI）が Repository や Bluetooth の詳細を意識せずに
 * 接続・切断・コマンド送信を実行できるようにするための抽象化を提供する。
 *
 * 主な責務:
 * - RCカーへの接続処理を意味づけし、結果を Result<Unit> として返す
 * - RCカーとの切断処理を意味づけし、結果を Result<Unit> として返す
 * - Command を受け取り、送信処理の成功・失敗を Result<Unit> として返す
 *
 * Result 型を返すことで、通信失敗や例外を上位層が安全に扱えるようにし、
 * ViewModel が UI 状態（接続中・エラーなど）を適切に更新できるようにする。
 *
 * このインターフェースの具体的な実装（ControllerUseCaseImpl）は、
 * ControllerRepository を利用して実際の通信処理を行う。
 *
 * @see com.example.rccarcontroller.Domain.Repository.ControllerRepository
 */
interface ControllerUseCase {
    suspend fun connect(): Result<Unit>
    suspend fun disconnect(): Result<Unit>
    suspend fun send(command: Command): Result<Unit>
}
