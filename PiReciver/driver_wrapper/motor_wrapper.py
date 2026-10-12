import logging

class MotorDriverWrapper:
    """
    MotorRawDriver を抽象化し、より高レベルなモーター操作を提供する DriverWrapper。

    方式設計書の「DriverWrapper 層」に対応し、
    ControlLogic から渡された抽象的な操作（前進・後退・旋回・停止・速度変更）を
    RawDriver の低レベル PWM 制御へ変換する役割を持つ。

    特徴:
        - RawDriver の set_pwm / set_turn を組み合わせて抽象化された操作を提供
        - ControlLogic → DriverWrapper → RawDriver → GPIO の流れを構築
    """

    def __init__(self, raw):
        """
        DriverWrapper の初期化。

        Args:
            raw (MotorRawDriver): 低レベル PWM 制御を担当する RawDriver
        """
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("MotorDriverWrapper initialized")

        self.raw = raw

    def forward(self):
        """
        モーターを前進方向へ回転させる。

        Notes:
            - PWM 0.8（80% duty）で前進
        """
        self.logger.info("Executing FORWARD (PWM=0.8)")
        self.raw.set_pwm(0.8)

    def backward(self):
        """
        モーターを後退方向へ回転させる。

        Notes:
            - PWM -0.8（80% duty, 逆方向）で後退
        """
        self.logger.info("Executing BACKWARD (PWM=-0.8)")
        self.raw.set_pwm(-0.8)

    def turn_left(self):
        """
        左旋回を行う。

        Notes:
            - set_turn(-0.5) により左方向へ旋回
            - 実際の左右差動制御は RawDriver 側で実装予定
        """
        self.logger.info("Executing TURN LEFT (value=-0.5)")
        self.raw.set_turn(-0.5)

    def turn_right(self):
        """
        右旋回を行う。

        Notes:
            - set_turn(0.5) により右方向へ旋回
        """
        self.logger.info("Executing TURN RIGHT (value=0.5)")
        self.raw.set_turn(0.5)

    def stop(self):
        """
        モーターを停止させる。

        Notes:
            - PWM 0 により停止
        """
        self.logger.info("Executing STOP (PWM=0)")
        self.raw.set_pwm(0)

    def set_speed(self, value):
        """
        モーターの速度を設定する。

        Args:
            value (int): 0〜100 の速度値（百分率）

        Notes:
            - 内部では 0.0〜1.0 の PWM に変換して RawDriver に渡す
        """
        pwm = value / 100.0
        self.logger.info(f"Executing SPEED: value={value}, pwm={pwm}")
        self.raw.set_pwm(pwm)
