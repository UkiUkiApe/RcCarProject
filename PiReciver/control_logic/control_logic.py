import logging
from .executors.move_executor import MoveExecutor
from .executors.turn_executor import TurnExecutor
from .executors.stop_executor import StopExecutor
from .executors.speed_executor import SpeedExecutor
from ..command_layer.command import CommandType
from .state_manager.state_manager import StateManager


class ControlLogic:
    """
    抽象コマンド → 物理操作
    状態遷移(StateManager)を組み込み、現在の動作状態を管理する。
    """

    def __init__(self, driver):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("ControlLogic initialized")

        self.driver = driver
        self.move = MoveExecutor()
        self.turn = TurnExecutor()
        self.stop = StopExecutor()
        self.speed = SpeedExecutor()

        # ★ 状態管理を追加
        self.state = StateManager()

    def dispatch(self, command):
        """
        抽象コマンドを Executor に振り分け、
        同時に StateManager に状態更新を通知する。
        """
        cmd_type = command.type
        self.logger.info(f"Dispatching command: type={cmd_type}, value={command.value}")

        # ★ 許可判定（最初に行う）
        if not self.state.is_allowed(cmd_type):
            self.logger.warning(f"Command not allowed: {cmd_type}")
            return

        # ★ 状態更新（ControlLogic の最初の仕事）
        self.state.update(cmd_type)

        # 前進・後退
        if cmd_type in (CommandType.FORWARD, CommandType.BACKWARD):
            self.logger.debug("Selected MoveExecutor")
            self.move.execute(command, self.driver)

        # 左右旋回
        elif cmd_type in (CommandType.LEFT, CommandType.RIGHT):
            self.logger.debug("Selected TurnExecutor")
            self.turn.execute(command, self.driver)

        # 停止
        elif cmd_type == CommandType.STOP:
            self.logger.debug("Selected StopExecutor")
            self.stop.execute(command, self.driver)

        # 速度変更
        elif cmd_type == CommandType.SPEED:
            self.logger.debug("Selected SpeedExecutor")
            self.speed.execute(command, self.driver)

        elif cmd_type == CommandType.RESET:
            self.state.reset()
            return

        else:
            self.logger.warning(f"Unknown command type received: {cmd_type}")
