import logging

class MotorRawDriverDummy:
    """
    モーター制御のダミー実装。
    実機の GPIO / PWM を使わず、ログ出力のみを行う。

    DriverWrapper から呼び出されるインターフェースを完全に模倣する。
    """

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.info("MotorRawDriverDummy initialized (no actual GPIO/PWM)")

    def set_pwm(self, value: float):
        """
        PWM を設定するダミー処理。
        実機では value は -1.0 ～ +1.0 の範囲。
        """
        self.logger.info(f"[DUMMY] set_pwm called with value={value}")

    def set_turn(self, value: float):
        """
        旋回用の PWM を設定するダミー処理。
        実機では value は -1.0 ～ +1.0 の範囲。
        """
        self.logger.info(f"[DUMMY] set_turn called with value={value}")

    def stop(self):
        """
        モーター停止のダミー処理。
        """
        self.logger.info("[DUMMY] stop called")
