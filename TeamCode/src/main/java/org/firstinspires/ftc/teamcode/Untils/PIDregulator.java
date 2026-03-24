package org.firstinspires.ftc.teamcode.Untils;

public class PIDregulator {

    private double kP, kI, kD;      // коэффициенты PID
    private double setPoint = 0;     // целевое значение
    private double integral = 0;     // интегральная часть
    private double lastError = 0;    // предыдущая ошибка
    private double outputMin = -1;   // минимальный выход
    private double outputMax = 1;    // максимальный выход

    // Конструктор

    // Установка целевого значения
    public void setSetPoint(double setPoint) {
        this.setPoint = setPoint;
    }

    // Ограничение выхода PID
    public void setOutputLimits(double min, double max) {
        this.outputMin = min;
        this.outputMax = max;
    }

    // Основной метод расчета PID
    public double calculate(double currentValue) {
        double error = setPoint - currentValue;
        integral += error;
        double derivative = error - lastError;

        double output = kP * error + kI * integral + kD * derivative;

        // Ограничиваем выход
        if (output > outputMax) output = outputMax;
        if (output < outputMin) output = outputMin;

        lastError = error;
        return output;
    }

    // Сброс интеграла и ошибки
    public void reset() {
        integral = 0;
        lastError = 0;
    }
}