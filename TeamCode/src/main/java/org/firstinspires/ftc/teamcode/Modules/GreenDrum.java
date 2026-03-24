package org.firstinspires.ftc.teamcode.Modules;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class GreenDrum {
    // Объявление мотора
    public DcMotor motor = null;

    HardwareMap hwMap;

    // Конструктор
    public GreenDrum(HardwareMap hardwareMap) {
        hwMap = hardwareMap;
    }

    // Инициализация мотора
    public void init() {
        motor = hwMap.get(DcMotor.class, "GreenDrum");
        motor.setDirection(DcMotor.Direction.FORWARD); // направление мотора
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // сброс энкодера
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);   // обычная работа без PID
        motor.setPower(0); // начальная мощность 0
    }

    // Метод для установки мощности
    public void setPower(double power) {
        motor.setPower(power);
    }

    // Метод остановки мотора
    public void stop() {
        motor.setPower(0);
    }
}