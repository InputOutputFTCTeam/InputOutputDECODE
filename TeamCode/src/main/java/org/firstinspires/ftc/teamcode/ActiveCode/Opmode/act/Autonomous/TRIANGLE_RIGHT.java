package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "triangle-RIGHT", group = "Auto")
public class TRIANGLE_RIGHT extends LinearOpMode {

    // === Настройки колёс и энкодеров ===
    private static final double WHEEL_DIAMETER_CM = 9.6;
    private static final double COUNTS_PER_MOTOR_REV = 1120;
    private static final double DRIVE_GEAR_REDUCTION = 1.0;
    private static final double WHEEL_CIRCUMFERENCE_CM = Math.PI * WHEEL_DIAMETER_CM;
    private static final double COUNTS_PER_CM = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_CM;

    // === Настройка поворота (может потребовать калибровки) ===
    private static final double TRACK_WIDTH_CM = 35.0;
    private static final double COUNTS_PER_DEGREE = (TRACK_WIDTH_CM * Math.PI / WHEEL_CIRCUMFERENCE_CM) * COUNTS_PER_MOTOR_REV / 360.0;

    private static final double DRIVE_POWER = 0.8; // немного снижено для точности
    private static final double TURN_POWER = 0.6;

    @Override
    public void runOpMode() {
        // === Инициализация моторов движения ===
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "frontLeft");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "backLeft");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "frontRight");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "backRight");

        // === Дополнительные устройства ===
        DcMotor armMotor = hardwareMap.get(DcMotor.class, "armMotor"); // не используется, но оставлено
        Servo clawServo = hardwareMap.get(Servo.class, "clawServo");
        Servo drumServo = hardwareMap.get(Servo.class, "servo2"); // ← Барабан как серво!

        // === Направление моторов: ВСЕ ОСТАЮТСЯ В FORWARD (как требует сборка) ===
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        // === Начальное положение серво (закрыто) ===
        clawServo.setPosition(0.8);

        telemetry.addData("Status", "Ready. Waiting for start...");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            // 1. Движение прямо на 32 см
            driveDistanceForward(32, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 2. Поворот направо на 30 градусов
            turnRight(30, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            //3. вперед
            driveDistanceForward(5,DRIVE_POWER,leftFront,leftBack,rightFront,rightBack);



            // === Завершение ===
            stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
            telemetry.addData("Status", "Autonomous complete! (LEFT SIDE)");
            telemetry.update();
        }
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ  ===

    private void resetDriveEncoders(DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    private void stopDriveMotors(DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setPower(0);
        }
    }

    private void setMotorModes(DcMotor.RunMode mode, DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setMode(mode);
        }
    }

    private void setMotorPowers(double power, DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setPower(power);
        }
    }

    // === ДВИЖЕНИЕ ВПЕРЁД (все моторы в одну сторону) ===
    private void driveDistanceForward(double distanceCm, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (distanceCm * COUNTS_PER_CM);
        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

        // Все моторы получают ОДИНАКОВУЮ цель (
        leftFront.setTargetPosition(-targetTicks);
        leftBack.setTargetPosition(-targetTicks);
        rightFront.setTargetPosition(-targetTicks);
        rightBack.setTargetPosition(-targetTicks);

        setMotorModes(DcMotor.RunMode.RUN_TO_POSITION, leftFront, leftBack, rightFront, rightBack);
        setMotorPowers(Math.abs(power), leftFront, leftBack, rightFront, rightBack);

        while (opModeIsActive() &&
                (leftFront.isBusy() || leftBack.isBusy() ||
                        rightFront.isBusy() || rightBack.isBusy())) {
            telemetry.addData("Driving", "Forward %.0f cm", distanceCm);
            telemetry.update();
        }

        stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
        setMotorModes(DcMotor.RunMode.RUN_USING_ENCODER, leftFront, leftBack, rightFront, rightBack);
    }

    // === ПОВОРОТ НАПРАВО ===
    private void turnRight(double degrees, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (degrees * COUNTS_PER_DEGREE);
        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

        // Левая сторона — вперёд (+), правая — назад (-)
        leftFront.setTargetPosition(targetTicks);
        leftBack.setTargetPosition(targetTicks);
        rightFront.setTargetPosition(-targetTicks);
        rightBack.setTargetPosition(-targetTicks);

        setMotorModes(DcMotor.RunMode.RUN_TO_POSITION, leftFront, leftBack, rightFront, rightBack);
        setMotorPowers(Math.abs(power), leftFront, leftBack, rightFront, rightBack);

        while (opModeIsActive() &&
                (leftFront.isBusy() || leftBack.isBusy() ||
                        rightFront.isBusy() || rightBack.isBusy())) {
            telemetry.addData("Turning", "Right %.0f°", degrees);
            telemetry.update();
        }

        stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
        setMotorModes(DcMotor.RunMode.RUN_USING_ENCODER, leftFront, leftBack, rightFront, rightBack);
    }

    // === ПОВОРОТ НАЛЕВО (на случай будущего использования) ===
    private void turnLeft(double degrees, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (degrees * COUNTS_PER_DEGREE);
        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

        // Левая сторона — назад (-), правая — вперёд (+)
        leftFront.setTargetPosition(-targetTicks);
        leftBack.setTargetPosition(-targetTicks);
        rightFront.setTargetPosition(targetTicks);
        rightBack.setTargetPosition(targetTicks);

        setMotorModes(DcMotor.RunMode.RUN_TO_POSITION, leftFront, leftBack, rightFront, rightBack);
        setMotorPowers(Math.abs(power), leftFront, leftBack, rightFront, rightBack);

        while (opModeIsActive() &&
                (leftFront.isBusy() || leftBack.isBusy() ||
                        rightFront.isBusy() || rightBack.isBusy())) {
            telemetry.addData("Turning", "Left %.0f°", degrees);
            telemetry.update();
        }

        stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
        setMotorModes(DcMotor.RunMode.RUN_USING_ENCODER, leftFront, leftBack, rightFront, rightBack);
    }
}