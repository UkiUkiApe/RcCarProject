package com.example.rccarcontroller.Domain.UseCase

import android.util.Log
import com.example.rccarcontroller.Domain.Model.Command
import com.example.rccarcontroller.Domain.Repository.ControllerRepository
import com.example.rccarcontroller.InfraStructure.Logger.Logger

/**
 * RCカー制御に関するアプリケーションロジックを実装する UseCase。
 *
 * Clean Architecture における Domain 層の「操作の意味づけ」を担い、
 * 上位層（ViewModel / UI）が Repository や Bluetooth の詳細を意識せずに
 * 接続・切断・コマンド送信を扱えるようにする役割を持つ。
 *
 * 主な責務:
 * - ControllerRepository の connect / disconnect / send を呼び出し、
 *   成功・失敗を Result<Unit> として返すことで上位層が安全に状態管理できるようにする
 * - 通信失敗時には Result.failure を返し、ViewModel が UI 状態を
 *   「接続エラー」「送信エラー」などに適切に反映できるようにする
 *
 * この実装により、UseCase は「操作の意味づけ」を提供し、
 * Repository は「データアクセス（Bluetooth通信）」に専念できるため、
 * 責務分離とレイヤー分離が明確な構造となる。
 *
 * @param repository RCカー制御のデータアクセスを担当する Repository
 */
class ControllerUseCaseImpl(
    private val repository: ControllerRepository,
    private val logger: Logger
) : ControllerUseCase {

    override suspend fun connect(): Result<Unit> {
        logger.info("UseCase connect() called")

        // Repositoryを介して通信を接続しにいく
        val success = repository.connect()
        logger.info("UseCase connect() repository result = $success")

        return if (success) Result.success(Unit)
        else Result.failure(Exception("Connection failed"))
    }

    override suspend fun disconnect(): Result<Unit> {
        Log.d("UseCase", "disconnect() called")

        // Repositoryを介して通信を切断しにいく
        val success = repository.disconnect()
        logger.info("UseCase disconnect() repository result = $success")

        return if (success) Result.success(Unit)
        else Result.failure(Exception("Disconnect failed"))
    }

    override suspend fun send(command: Command): Result<Unit> {
        logger.info("UseCase send() called: command=$command")

        // Repositoryを介してCommandを送信しにいく
        val success = repository.send(command)
        logger.info("UseCase send() repository result = $success")

        return if (success) Result.success(Unit)
        else Result.failure(Exception("Send failed"))
    }
}
