import logging

class SpeedExecutor:
    """
    速度変更の物理操作を担当する Executor。

    方式設計書の「ControlLogic → Executor」に対応し、
    抽象コマンド（SPEED）を DriverWrapper の set_speed() 呼び出しへ変換する。

    例:
        "V:30" → Command(type=SPEED, value=30)
        → driver.set_speed(30)
    """

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("SpeedExecutor initialized")

    def execute(self, command, driver):
        """
        抽象コマンドの速度値を物理操作へ変換する。

        Args:
            command (Command): 抽象コマンド（SPEED）
            driver (MotorDriverWrapper): モーター制御ラッパー

        Returns:
            None
        """
        value = command.value
        self.logger.info(f"Executing SPEED: value={value}")
        driver.set_speed(value)