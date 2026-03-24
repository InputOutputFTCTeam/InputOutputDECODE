package org.firstinspires.ftc.teamcode.Models;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class VariableAngle {
    // Объявляем серву
    public Servo catchServo = null;

    HardwareMap hwMap;

    // Инициализация сервы
    public void init() {
        catchServo = hwMap.get(Servo.class, "servo");
        catchServo.setPosition(0); // начальная позиция, можно поменять
    }

    // Метод для установки позиции сервы
    public void setPosition(double position) {
        catchServo.setPosition(position);
    }

    // Методы для быстрых команд, если нужно
    public void open() {
        catchServo.setPosition(0.0); // открыто
    }

    public void close() {
        catchServo.setPosition(1.0); // закрыто
    }
}
