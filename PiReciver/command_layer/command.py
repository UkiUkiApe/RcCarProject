from __future__ import annotations

import logging
from dataclasses import dataclass
from enum import Enum


class CommandType(Enum):
    """
    RCカー制御用のコマンド種別を表す Enum。

    Android 側の送信プロトコルと 1:1 対応しており、
    Raspberry Pi 側では抽象コマンドとして扱われる。

    Attributes:
        FORWARD ("F"): 前進
        BACKWARD ("B"): 後退
        LEFT ("L"): 左旋回
        RIGHT ("R"): 右旋回
        STOP ("S"): 停止
        SPEED ("V"): 速度変更（例: "V:10"）
        RESET ("RESET"): 復帰(コントローラ側未実装)
    """

    FORWARD = "F"
    BACKWARD = "B"
    LEFT = "L"
    RIGHT = "R"
    STOP = "S"
    SPEED = "V"
    RESET = "RESET"


@dataclass
class Command:
    """
    抽象コマンドモデル。

    CommandLayer にて文字列プロトコルを解析した結果として生成され、
    ControlLogic 層で具体的な物理操作へ変換される。

    Attributes:
        type (CommandType): コマンド種別
        value (int | None): パラメータ（速度など）。不要な場合は None。

    Example:
        Command(CommandType.FORWARD)
        Command(CommandType.SPEED, value=20)
    """

    type: CommandType
    value: int | None = None

    def __post_init__(self):
        """
        コマンド生成時にログを出力する。

        - デバッグ時に Command の生成状況を追跡できる
        - ControlLogic に渡る前の状態を確認できる
        """
        logger = logging.getLogger(self.__class__.__name__)
        logger.debug(f"Command created: type={self.type}, value={self.value}")
