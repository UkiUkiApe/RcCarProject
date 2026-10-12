"""
Raspberry Pi RC Car Controller - Main Entry Point

このモジュールは、Raspberry Pi 上で動作する RCカー制御アプリケーションの
エントリポイントである。

主な処理フロー:
1. Bluetooth SPP (RFCOMM) により Android からコマンド文字列を受信する
2. CommandLayer にて正規化・バリデーション・抽象コマンド変換を行う
3. ControlLogic にて抽象コマンドを具体的な物理操作へ変換する
4. DriverWrapper → Driver にて GPIO / PWM を用いてモーターを制御する
"""

import os
import logging

# ★ 環境変数によるドライバ切り替え
USE_DUMMY = os.getenv("USE_DUMMY_DRIVER", "false").lower() == "true"

if USE_DUMMY:
    from driver.motor_raw_driver_dummy import MotorRawDriverDummy as MotorRawDriver
else:
    from driver.motor_raw_driver import MotorRawDriver

from command_layer.command import CommandType, Command
from logging_config import setup_logging
from safety.fail_safe_monitor import FailSafeMonitor

from transport.bluetooth_spp import BluetoothSPP
from command_layer.normalizer import CommandNormalizer
from command_layer.validator import CommandValidator
from command_layer.parser import CommandParser
from command_layer.dispatcher import CommandDispatcher
from control_logic.control_logic import ControlLogic
from driver_wrapper.motor_wrapper import MotorDriverWrapper


def main():
    """
    RCカー制御アプリケーションのメインループ。
    """

    # ログ初期化
    setup_logging()
    logger = logging.getLogger("Main")
    logger.info("Raspberry Pi Controller started")
    logger.info(f"USE_DUMMY_DRIVER = {USE_DUMMY}")

    # 通信層
    spp = BluetoothSPP()
    normalizer = CommandNormalizer()
    validator = CommandValidator()
    parser = CommandParser()

    # Driver 層（★ダミー／本物を切り替え）
    if USE_DUMMY:
        raw_driver = MotorRawDriver()
    else:
        raw_driver = MotorRawDriver(pwm_pin=18)

    driver = MotorDriverWrapper(raw_driver)

    # ControlLogic 層
    logic = ControlLogic(driver)
    dispatcher = CommandDispatcher(logic)

    # フェイルセーフ
    failsafe = FailSafeMonitor(timeout_sec=2.0)

    logger.info("Initialization completed. Waiting for commands...")

    # メインループ
    while True:
        raw = spp.read()
        logger.debug(f"Raw received: {raw!r}")

        # フェイルセーフ：コマンドが来たら通知
        if raw:
            failsafe.notify_command_received()

        texts = normalizer.normalize(raw)
        logger.debug(f"Normalized texts: {texts}")

        for text in texts:
            if validator.validate(text):
                logger.info(f"Valid command received: {text}")
                cmd = parser.parse(text)
                logger.debug(f"Parsed command: {cmd}")
                dispatcher.dispatch(cmd)
            else:
                logger.warning(f"Invalid command ignored: {text}")

        # フェイルセーフチェック
        if failsafe.check():
            logger.warning("Failsafe STOP triggered")
            dispatcher.dispatch(Command(CommandType.STOP))
            logic.state.fail_safe_trigger()


if __name__ == "__main__":
    main()
