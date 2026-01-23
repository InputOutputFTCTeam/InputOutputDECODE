
package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.trash;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "Drive _fake4", group = "Auto")
public class gunAut_333 extends LinearOpMode {

    // === Настройки колёс и энкодеров ===
    private static final double WHEEL_DIAMETER_CM = 9.6;
    private static final double COUNTS_PER_MOTOR_REV = 1120;
    private static final double DRIVE_GEAR_REDUCTION = 1.0;
    private static final double WHEEL_CIRCUMFERENCE_CM = Math.PI * WHEEL_DIAMETER_CM;
    private static final double COUNTS_PER_CM = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_CM;

    // === Настройка поворота
    private static final double TRACK_WIDTH_CM = 35.0;
    private static final double COUNTS_PER_DEGREE = (TRACK_WIDTH_CM * Math.PI / WHEEL_CIRCUMFERENCE_CM) * COUNTS_PER_MOTOR_REV / 360.0;

    private static final double DRIVE_POWER = 0.8;
    private static final double TURN_POWER = 1.0;

    @Override
    public void runOpMode ( ) {
        // === Инициализация моторов движения ===
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "frontLeft");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "backLeft");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "frontRight");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "backRight");

        // === Дополнительные устройства ===
        DcMotor armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        Servo clawServo = hardwareMap.get(Servo.class, "clawServo");

        // === Направление моторов ОСТАВЛЯЕМ ВСЕ FORWARD  ===
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // === Начальное положение серво ===
        //clawServo.setPosition(0.2); // Закрыто

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            // === Исходная последовательность (шаги 1–14) ===
            //sleep();
            // 1. Запустить armMotor
            //armMotor.setPower(1);

            // 2. Проехать 10 см назад
            driveDistanceBackward(10.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            armMotor.setPower(1);
            sleep(500);
            // 3. Открыть clawServo
            clawServo.setPosition(0.1);
            sleep(500);

            // 4. Выключить armMotor
            armMotor.setPower(0.0);

            // 5. Проехать 40 см назад
            driveDistanceBackward(120.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);
           /* // 6. Повернуть налево на 30 градусов
            turnLeft(30.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 7. Проехать 40 см ВПЕРЁД
            driveDistanceForward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);
/*
            // 8. Проехать 40 см НАЗАД
            driveDistanceBackward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 9. Повернуть направо на 30 градусов
            turnRight(30.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 10. Включить armMotor
            armMotor.setPower(-0.4);

            // 11. Проехать 10 см ВПЕРЁД
            driveDistanceForward(10.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 12. Закрыть clawServo
            clawServo.setPosition(0.2);
            sleep(500);

            // 13. Проехать 10 см НАЗАД
            driveDistanceBackward(10.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 14. Выключить armMotor
            armMotor.setPower(0.0);

            // === НОВАЯ ДОБАВЛЕННАЯ ПОСЛЕДОВАТЕЛЬНОСТЬ ===

            // 15. Повернуть направо на 40 градусов
            turnRight(40.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 16. Проехать 40 см вперёд
            driveDistanceForward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 17. Проехать 40 см назад
            driveDistanceBackward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 18. Повернуть налево на 40 градусов
            turnLeft(40.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 19. Включить armMotor
            armMotor.setPower(-0.4);
            sleep(300);

            // 20. Проехать прямо 40 см
            driveDistanceForward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 21. Открыть clawServo (выпустить объект)
            clawServo.setPosition(0.8);
            sleep(500);

            // 22. Выключить armMotor
            armMotor.setPower(0.0);
            sleep(100);

            // 23. Отъехать на 70 см назад
            driveDistanceBackward(70.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 24. Повернуть направо на 40 градусов
            turnRight(40.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 25. Проехать 40 см вперёд
            driveDistanceForward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 26. Проехать 40 см назад
            driveDistanceBackward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 27. Повернуть налево на 40 градусов
            turnLeft(40.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 28. Включить armMotor
            armMotor.setPower(-0.4);
            sleep(300);

            // 29. Проехать 80 см прямо
            driveDistanceForward(80.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 30. Закрыть clawServo (захватить объект)
            clawServo.setPosition(0.2);
            sleep(500);

            // 31. Проехать 50 см назад
            driveDistanceBackward(50.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 32. Выключить armMotor
            armMotor.setPower(0.0);*/

            // === Завершение ===
            stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
            telemetry.addData("Status", "Autonomous complete!");
            telemetry.update();
        }
    }

    // === Вспомогательный метод: сброс энкодеров ===
    private void resetDriveEncoders (DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    // === Вспомогательный метод: остановка ===
    private void stopDriveMotors (DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setPower(0);
        }
    }

    // === Езда ВПЕРЁД на заданное расстояние ===
    private void driveDistanceForward (double distanceCm, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        driveDistance(distanceCm, power, false, leftFront, leftBack, rightFront, rightBack);
    }

    // === Езда НАЗАД на заданное расстояние ===
    private void driveDistanceBackward (double distanceCm, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        driveDistance(distanceCm, power, true, leftFront, leftBack, rightFront, rightBack);
    }

    // === Общий метод движения (вперёд/назад) ===
    private void driveDistance (double distanceCm, double power, boolean backward, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (distanceCm * COUNTS_PER_CM);

        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

        if (backward) {
            // Движение НАЗАД: левые вперёд (+), правые назад (-)
            leftFront.setTargetPosition(targetTicks);
            leftBack.setTargetPosition(targetTicks);
            rightFront.setTargetPosition(-targetTicks);
            rightBack.setTargetPosition(-targetTicks);
        } else {
            // Движение ВПЕРЁД: левые назад (-), правые вперёд (+)
            leftFront.setTargetPosition(-targetTicks);
            leftBack.setTargetPosition(-targetTicks);
            rightFront.setTargetPosition(targetTicks);
            rightBack.setTargetPosition(targetTicks);
        }

        setMotorModes(DcMotor.RunMode.RUN_TO_POSITION, leftFront, leftBack, rightFront, rightBack);
        setMotorPowers(Math.abs(power), leftFront, leftBack, rightFront, rightBack);

        while (opModeIsActive() &&
                (leftFront.isBusy() || leftBack.isBusy() ||
                        rightFront.isBusy() || rightBack.isBusy())) {
            telemetry.addData("Driving", "%.0f cm %s", distanceCm, backward ? "BACK" : "FWD");
            telemetry.update();
        }

        stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
        setMotorModes(DcMotor.RunMode.RUN_USING_ENCODER, leftFront, leftBack, rightFront, rightBack);
    }

    // === Поворот налево ===
    private void turnLeft (double degrees, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (degrees * COUNTS_PER_DEGREE);

        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

        // Налево: левые назад (-), правые вперёд (+)
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

    // === Поворот направо ===
    private void turnRight (double degrees, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (degrees * COUNTS_PER_DEGREE);

        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

        // Направо: левые вперёд (+), правые назад (-)
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

    // === Утилиты ===
    private void setMotorModes (DcMotor.RunMode mode, DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setMode(mode);
        }
    }

    private void setMotorPowers (double power, DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setPower(power);
        }
    }

}