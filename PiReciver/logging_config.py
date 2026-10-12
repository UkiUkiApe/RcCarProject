import logging

def setup_logging():
    """
    ログ設定を初期化するユーティリティ関数。

    本アプリケーション全体で共通のログフォーマットと出力先を定義する。
    以下の2つのハンドラを設定する：

        - FileHandler: すべてのログを rpi_controller.log に UTF-8 で保存
        - StreamHandler: コンソールへログを出力（開発時の確認用）

    ログフォーマット:
        %(asctime)s [%(levelname)s] %(name)s - %(message)s

    ログレベル:
        DEBUG（最も詳細なログを出力）

    Notes:
        - setup_logging() はアプリ起動時に一度だけ呼び出すことを想定
        - 各クラスは logging.getLogger(__name__) によりこの設定を継承する
    """
    logging.basicConfig(
        level=logging.DEBUG,
        format="%(asctime)s [%(levelname)s] %(name)s - %(message)s",
        handlers=[
            logging.FileHandler("rpi_controller.log", encoding="utf-8"),
            logging.StreamHandler()
        ]
    )
