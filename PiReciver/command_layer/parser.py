import logging
from .command import Command, CommandType

class CommandParser:
    """
    文字列プロトコルを抽象コマンドへ変換する Parser。

    方式設計書の「CommandLayer → Parser」に対応し、
    正規化済みのコマンド文字列を Command オブジェクトへ変換する。

    対応プロトコル:
        - "F" / "B" / "L" / "R" / "S"
        - "V:10" のような速度指定コマンド
    """

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("CommandParser initialized")

    def parse(self, text: str) -> Command:
        """
        コマンド文字列を解析し、Command オブジェクトへ変換する。

        Args:
            text (str): 正規化済みのコマンド文字列（例: "F", "V:10"）

        Returns:
            Command: 抽象コマンドオブジェクト

        Raises:
            ValueError: 不正なコマンド文字列の場合
        """
        self.logger.debug(f"Parsing text: {text!r}")

        # 速度指定コマンド（例: "V:10"）
        if ":" in text:
            name, value = text.split(":", 1)
            self.logger.info(f"Parsed SPEED command: value={value}")
            return Command(CommandType.SPEED, int(value))

        # 単一コマンド（例: "F"）
        try:
            cmd_type = CommandType(text)
            self.logger.info(f"Parsed simple command: type={cmd_type}")
            return Command(cmd_type)
        except ValueError:
            self.logger.error(f"Unknown command type: {text!r}")
            raise