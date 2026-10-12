import logging

class MoveExecutor:
    """
    前進・後退の物理操作を担当する Executor。

    方式設計書の「ControlLogic → Executor」に対応し、
    抽象コマンド（FORWARD / BACKWARD）を DriverWrapper の具体的な
    forward() / backward() 呼び出しへ変換する役割を持つ。
    """

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("MoveExecutor initialized")

    def execute(self, command, driver):
        """
        抽象コマンドを前進／後退の物理操作へ変換する。

        Args:
            command (Command): 抽象コマンド（FORWARD / BACKWARD）
            driver (MotorDriverWrapper): モーター制御ラッパー

        Returns:
            None
        """
        cmd_type = command.type.value
        self.logger.debug(f"MoveExecutor received command: {cmd_type}")

        if cmd_type == "F":
            self.logger.info("Executing FORWARD")
            driver.forward()

        elif cmd_type == "B":
            self.logger.info("Executing BACKWARD")
            driver.backward()

        else:
            self.logger.warning(f"MoveExecutor received unsupported command: {cmd_type}")
