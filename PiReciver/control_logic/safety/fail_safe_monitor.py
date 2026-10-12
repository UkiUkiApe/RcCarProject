import time
import logging

class FailSafeMonitor:
    """
    BluetoothSPP の通信断を検知し、一定時間コマンドが来なければ STOP を発行する。
    """

    def __init__(self, timeout_sec=2.0):
        self.logger = logging.getLogger(self.__class__.__name__)
        self.timeout = timeout_sec
        self.last_received_time = time.time()

    def notify_command_received(self):
        """コマンドを受信したときに呼ばれる。"""
        self.last_received_time = time.time()

    def check(self):
        """
        通信断をチェックする。
        一定時間コマンドが来ていなければ True（＝STOPすべき）を返す。
        """
        elapsed = time.time() - self.last_received_time
        if elapsed > self.timeout:
            self.logger.warning(f"FailSafe triggered: no command for {elapsed:.2f} sec")
            return True
        return False