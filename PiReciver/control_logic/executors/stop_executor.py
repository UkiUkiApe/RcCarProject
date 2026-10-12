import logging

class StopExecutor:
    """
    停止操作を担当する Executor。

    方式設計書の「ControlLogic → Executor」に対応し、
    抽象コマンド（STOP）を DriverWrapper の stop() 呼び出しへ変換する。

    例:
        "S" → Command(type=STOP)
        → driver.stop()
    """

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("StopExecutor initialized")

    def execute(self, command, driver):
        """
        抽象コマンドを停止操作へ変換する。

        Args:
            command (Command): 抽象コマンド（STOP）
            driver (MotorDriverWrapper): モーター制御ラッパー

        Returns:
            None
        """
        self.logger.info("Executing STOP")
        driver.stop()