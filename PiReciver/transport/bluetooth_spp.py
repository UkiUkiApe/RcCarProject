import logging
import serial

class BluetoothSPP:
    """
    Bluetooth SPP (Serial Port Profile) を UART 経由で扱うクラス。

    方式設計書の「Transport 層」に対応し、
    Android 側から送信された電文を /dev/rfcomm0 経由で受信する。

    特徴:
        - SPP は「無線の UART」として動作する
        - RasPi 側では /dev/rfcomm0 が仮想 UART として生成される
        - readline() により 1 行単位で電文を取得する
    """

    def __init__(self, device="/dev/rfcomm0", baud=9600):
        """
        SPP 接続の初期化。

        Args:
            device (str): SPP により生成される仮想 UART デバイス
            baud (int): UART のボーレート（SPP では 9600 が一般的）
        """
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug(f"Initializing BluetoothSPP (device={device}, baud={baud})")

        try:
            self.ser = serial.Serial(device, baud)
            self.logger.info(f"BluetoothSPP connected to {device} at {baud} baud")
        except Exception as e:
            self.logger.error(f"Failed to open SPP device {device}: {e}")
            raise

    def read(self) -> str:
        """
        SPP 経由で 1 行の電文を読み取る。

        Returns:
            str: UTF-8 デコード済みの受信文字列（改行含む）

        Notes:
            - readline() は改行までブロッキングする
            - 正規化処理は CommandNormalizer が担当する
        """
        try:
            raw = self.ser.readline()
            text = raw.decode("utf-8")
            self.logger.debug(f"Received raw SPP data: {text!r}")
            return text
        except Exception as e:
            self.logger.error(f"SPP read error: {e}")
            return ""
