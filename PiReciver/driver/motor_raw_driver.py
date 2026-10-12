import logging

# Raspberry Pi 実機では RPi.GPIO を使う
try:
    import RPi.GPIO as GPIO
except ImportError:
    # 開発環境（Windows / Mac）では Mock を使う
    class MockGPIO:
        BCM = None
        OUT = None

        def setmode(self, *args):
            pass

        def setup(self, *args):
            pass

        class PWM:
            def __init__(self, *args):
                pass

            def start(self, *args):
                pass

            def ChangeDutyCycle(self, *args):
                pass

    GPIO = MockGPIO()


class MotorRawDriver:
    """
    モーターを PWM で直接制御する RawDriver。

    方式設計書の「Driver（RawDriver）層」に対応し、
    DriverWrapper から渡された抽象的な PWM 値を
    実際の GPIO / PWM 制御へ変換する役割を持つ。

    Raspberry Pi 実機では RPi.GPIO を使用し、
    開発環境では MockGPIO によって動作をエミュレートする。
    """

    def __init__(self, pwm_pin: int):
        """
        PWM ピンの初期化を行う。

        Args:
            pwm_pin (int): PWM 出力に使用する GPIO ピン番号（BCM）
        """
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug(f"MotorRawDriver initializing on pin {pwm_pin}")

        GPIO.setmode(GPIO.BCM)
        GPIO.setup(pwm_pin, GPIO.OUT)

        # PWM 周波数は 1000Hz（一般的な DC モーター向け）
        self.pwm = GPIO.PWM(pwm_pin, 1000)
        self.pwm.start(0)

        self.logger.info(f"MotorRawDriver initialized (pin={pwm_pin})")

    def set_pwm(self, value: float):
        """
        PWM デューティ比を設定する。

        Args:
            value (float): -1.0 ～ +1.0 の範囲で指定する速度値

        Notes:
            - 内部では 0～100% のデューティ比に変換する
            - 負値は絶対値に変換される（方向制御は DriverWrapper 側で行う）
        """
        duty = max(min(value * 100, 100), -100)
        abs_duty = abs(duty)

        self.logger.info(f"Setting PWM duty: raw={value}, duty={abs_duty}%")
        self.pwm.ChangeDutyCycle(abs_duty)

    def set_turn(self, value: float):
        """
        左右旋回用の PWM 制御（未実装）。

        Args:
            value (float): -1.0 ～ +1.0 の旋回値

        Notes:
            - 実際の左右差動制御はここに実装する予定
            - 現状はログのみ出力する
        """
        self.logger.warning(f"set_turn() called but not implemented: value={value}")
