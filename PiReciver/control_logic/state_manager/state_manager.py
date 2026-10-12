import logging
from enum import Enum, auto

class State(Enum):
    IDLE = auto()
    MOVING_FORWARD = auto()
    MOVING_BACKWARD = auto()
    TURNING_LEFT = auto()
    TURNING_RIGHT = auto()
    STOPPED = auto()
    ERROR = auto()

class StateManager:
    """
    ラジコンの動作状態を管理する StateManager。

    ControlLogic から通知される抽象コマンドに応じて状態を更新し、
    現在の状態を他レイヤー（SafetyManager, UI, Logger 等）が参照できるようにする。

    最低限の状態遷移のみを実装したひな型。
    """

    def __init__(self):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.state = State.IDLE
        self.logger.debug(f"StateManager initialized (state={self.state})")

    def update(self, command_type):
        """
        抽象コマンドに応じて状態を更新する。

        Args:
            command_type (CommandType): ControlLogic から渡されるコマンド種別
        """
        prev = self.state

        if command_type.name == "FORWARD":
            self.state = State.MOVING_FORWARD

        elif command_type.name == "BACKWARD":
            self.state = State.MOVING_BACKWARD

        elif command_type.name == "LEFT":
            self.state = State.TURNING_LEFT

        elif command_type.name == "RIGHT":
            self.state = State.TURNING_RIGHT

        elif command_type.name == "STOP":
            self.state = State.STOPPED

        elif command_type.name == "SPEED":
            # SPEED は状態を変えない（前進中か後退中かは変わらない）
            pass

        else:
            self.state = State.ERROR

        self.logger.info(f"State changed: {prev.name} → {self.state.name}")

    def get_state(self) -> State:
        """現在の状態を返す。"""
        return self.state

    # ★ 許可判定ロジック（最重要）
    def is_allowed(self, command_type):
        # ERROR 状態ではすべて拒否
        if self.state == State.ERROR:
            self.logger.error(f"Command rejected in ERROR state: {command_type}")
            return False

        # STOP 状態では SPEED など一部拒否したい場合はここに追加
        if self.state == State.STOPPED and command_type != "STOP":
            self.logger.warning(f"Command rejected in STOPPED state: {command_type}")
            return False

        return True

    def fail_safe_trigger(self):
        prev = self.state
        self.state = State.ERROR
        self.logger.error(f"FailSafe triggered: {prev.name} → ERROR")

    def reset(self):
        prev = self.state
        self.state = State.IDLE
        self.logger.info(f"Reset: {prev.name} → IDLE")