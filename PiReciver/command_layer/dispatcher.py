import logging

class CommandDispatcher:
    """
    ControlLogic へのコマンド振り分けを担当するクラス。

    CommandLayer にて生成された抽象コマンドを受け取り、
    ControlLogic に処理を委譲する役割を持つ。

    方式設計書における「Dispatcher層」に対応し、
    コマンドの流れを明確にするための中継レイヤー。
    """

    def __init__(self, control_logic):
        """
        CommandDispatcher の初期化。

        Args:
            control_logic (ControlLogic): 抽象コマンドを物理操作へ変換する制御ロジック層。
        """
        self.control_logic = control_logic
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.debug("CommandDispatcher initialized")

    def dispatch(self, command):
        """
        抽象コマンドを ControlLogic に渡す。

        Args:
            command (Command): CommandLayer にて生成された抽象コマンド。

        Returns:
            None
        """
        self.logger.info(f"Dispatching command: type={command.type}, value={command.value}")
        self.control_logic.dispatch(command)
