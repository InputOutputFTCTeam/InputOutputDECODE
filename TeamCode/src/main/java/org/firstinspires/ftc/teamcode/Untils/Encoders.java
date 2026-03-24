package org.firstinspires.ftc.teamcode.Untils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Encoders {

    public DcMotor motor = null;
    private HardwareMap hwMap;


    // Инициализация и настройка энкодеров
    public void init(DcMotor.Direction direction, boolean resetEncoder, boolean runUsingEncoder) {
        motor.setDirection(direction);

        if (resetEncoder) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // сброс энкодера
        }

        if (runUsingEncoder) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER); // работа с энкодером
        } else {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // без энкодера
        }

        motor.setPower(0); // начальная мощность 0
    }

    // Установить целевое положение (для RUN_TO_POSITION)
    public void setTargetPosition(int position) {
        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    // Проверка достиг ли мотор целевого положения
    public boolean isBusy() {
        return motor.isBusy();
    }

    // Включение/выключение мощности
    public void setPower(double power) {
        motor.setPower(power);
    }

    public void stop() {
        motor.setPower(0);
    }
}