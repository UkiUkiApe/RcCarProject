import logging

class TurnExecutor:
    """
    左右旋回の物理操作を担当する Executor。

    方式設計書の「ControlLogic → Executor」に対応し、
    抽象コマンド（LEFT / RIGHT）を DriverWrapper の turn_left() /
    turn_right() 呼び出しへ変換する役割を持つ。

    例:
        "L" → Command(type=LEFT)
        → driver.turn_left()

        "R" → Command(type=RIGHT)
        → driver.turn_right()
    """

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("TurnExecutor initialized")

    def execute(self, command, driver):
        """
        抽象コマンドを左右旋回の物理操作へ変換する。

        Args:
            command (Command): 抽象コマンド（LEFT / RIGHT）
            driver (MotorDriverWrapper): モーター制御ラッパー

        Returns:
            None
        """
        cmd_type = command.type.value
        self.logger.debug(f"TurnExecutor received command: {cmd_type}")

        if cmd_type == "L":
            self.logger.info("Executing TURN LEFT")
            driver.turn_left()

        elif cmd_type == "R":
            self.logger.info("Executing TURN RIGHT")
            driver.turn_right()

        else:
            self.logger.warning(f"TurnExecutor received unsupported command: {cmd_type}")
