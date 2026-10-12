import logging

class CommandNormalizer:
    """
    受信した raw 文字列を Parser が扱いやすい形式へ正規化するクラス。

    正規化内容（方式設計書に対応）:
        - trim（前後の空白除去）
        - 改行コード統一（CRLF / CR → LF）
        - 制御文字除去（0x20 未満の非表示文字）
        - 最大長チェック（DoS 防止）
        - 複数コマンド分割（改行区切り）

    この処理は CommandLayer の最初のステップであり、
    以降の Validator / Parser が安全に動作するための前処理を担う。
    """

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("CommandNormalizer initialized")

    def normalize(self, raw: str) -> list[str]:
        """
        raw 文字列を正規化し、複数コマンドに分割して返す。

        Args:
            raw (str): BluetoothSPP から受信した生データ

        Returns:
            list[str]: 正規化済みのコマンド文字列リスト
                       不正なデータの場合は空リストを返す
        """
        self.logger.debug(f"Raw input: {raw!r}")

        # trim
        raw = raw.strip()
        self.logger.debug(f"After trim: {raw!r}")

        # 改行コード統一
        raw = raw.replace("\r\n", "\n").replace("\r", "\n")
        self.logger.debug(f"After newline normalization: {raw!r}")

        # 制御文字除去（0x20 未満）
        filtered = "".join(ch for ch in raw if ord(ch) >= 0x20)
        self.logger.debug(f"After control char removal: {filtered!r}")

        # 最大長チェック
        if len(filtered) > 1024:
            self.logger.warning("Input exceeded max length (1024). Dropping.")
            return []

        # 複数コマンド分割
        commands = filtered.split("\n")
        self.logger.debug(f"Normalized commands: {commands}")

        return commands