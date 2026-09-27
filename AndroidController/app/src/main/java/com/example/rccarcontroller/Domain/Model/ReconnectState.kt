package com.example.rccarcontroller.Domain.Model

/**
 * Bluetooth 接続の再接続処理における状態を表す列挙型。
 *
 * ControllerRepository が自動再接続戦略を実行する際に利用され、
 * UI（ViewModel / Compose）が現在の再接続状況を把握するための
 * 状態通知モデルとして機能する。
 *
 * <p>
 * 【役割】
 * - 再接続処理の開始・進行・失敗を UI に伝える
 * - Repository が内部で管理する再接続戦略の状態を抽象化する
 * - BluetoothClient の生々しい状態（ERROR / DISCONNECTED）とは独立した
 *   アプリケーションレベルの状態として扱う
 *
 * <p>
 * 【設計意図】
 * - UI が「再接続中なのか」「失敗したのか」を明確に判断できるようにする
 * - BluetoothClient の状態と再接続戦略の状態を分離し、責務を明確化する
 * - 再接続戦略を Repository に集約し、ViewModel は状態を購読するだけにする
 *
 * @property IDLE 通常状態。再接続処理が行われていない。
 * @property RECONNECTING 再接続処理を実行中。UI はローディングや通知を表示できる。
 * @property FAILED 再接続が最大試行回数に達して失敗した状態。UI はエラー通知を表示できる。
 */
enum class ReconnectState {
    IDLE,          // 通常
    RECONNECTING,  // 再接続中
    FAILED         // 再接続失敗
}