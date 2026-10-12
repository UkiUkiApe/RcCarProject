import logging

class CommandValidator:
    """
    プロトコルバリデーションを行うクラス。

    方式設計書の「CommandLayer → Validator」に対応し、
    正規化済みのコマンド文字列がプロトコル仕様に合致しているかを判定する。

    バリデーション内容:
        - 空文字チェック
        - 制御文字チェック（0x20 未満）
        - 区切り文字チェック（速度コマンド "V:10"）
        - コマンド名チェック（F/B/L/R/S）
        - 数値チェック（速度コマンドの value が数字か）
    """

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("CommandValidator initialized")

    def validate(self, text: str) -> bool:
        """
        コマンド文字列がプロトコル仕様に合致しているかを判定する。

        Args:
            text (str): 正規化済みのコマンド文字列

        Returns:
            bool: 有効なコマンドなら True、不正なら False
        """
        self.logger.debug(f"Validating text: {text!r}")

        # 空文字チェック
        if not text:
            self.logger.warning("Validation failed: empty text")
            return False

        # 制御文字チェック（0x20 未満）
        if text[0] < " ":
            self.logger.warning(f"Validation failed: control char detected in {text!r}")
            return False

        # 速度コマンド "V:10"
        if ":" in text:
            name, value = text.split(":", 1)
            valid = (name == "V" and value.isdigit())
            if valid:
                self.logger.info(f"Valid SPEED command: value={value}")
            else:
                self.logger.warning(f"Invalid SPEED command: {text!r}")
            return valid

        # 単一コマンド
        valid_simple = text in ["F", "B", "L", "R", "S"]
        if valid_simple:
            self.logger.info(f"Valid simple command: {text}")
        else:
            self.logger.warning(f"Invalid simple command: {text!r}")

        return valid_simple