package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "Drive 4", group = "Auto")
public class big_red_triangle extends LinearOpMode {

    // === Настройки колёс и энкодеров ===
    private static final double WHEEL_DIAMETER_CM = 9.6;
    private static final double COUNTS_PER_MOTOR_REV = 1120;
    private static final double DRIVE_GEAR_REDUCTION = 1.0;
    private static final double WHEEL_CIRCUMFERENCE_CM = Math.PI * WHEEL_DIAMETER_CM;
    private static final double COUNTS_PER_CM = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_CM;

    // === Настройка поворота ===
    private static final double TRACK_WIDTH_CM = 35.0;
    private static final double COUNTS_PER_DEGREE = (TRACK_WIDTH_CM * Math.PI / WHEEL_CIRCUMFERENCE_CM) * COUNTS_PER_MOTOR_REV / 360.0;

    private static final double DRIVE_POWER = 1;
    private static final double TURN_POWER = 1.0;

    // === Настройки барабана  ===
    private static final double DRUM_FORWARD = -1.0;     // Полный вперёд (напр., по часовой)
    private static final double DRUM_STOP = 0.5;        // Стоп (середина)
    private static final double DRUM_BACKWARD = 0.0;    // Назад (против часовой) — если нужно
    private static final int DRUM_DURATION_MS = 200;    // Время вращения

    @Override
    public void runOpMode() {
        // === Инициализация моторов движения ===
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "frontLeft");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "backLeft");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "frontRight");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "backRight");

        // === Дополнительные устройства ===
        DcMotor armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        Servo clawServo = hardwareMap.get(Servo.class, "clawServo");
        Servo drumServo = hardwareMap.get(Servo.class, "servo2"); // ← Барабан как серво!

        // === Направление моторов колёс (все FORWARD, как у вас) ===
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Устанавливаем барабан в положение СТОП при старте
        drumServo.setPosition(DRUM_STOP);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            armMotor.setPower(1); //включить мотор пулятеля  1 включить 0 выключить
            // 1. Проехать 10 см назад
            driveDistanceBackward(8.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 2. Поднять руку
            //armMotor.setPower(0.99);
            //  sleep(800);

            // 3. Открыть клешню (выпустить объект)
            clawServo.setPosition(0.1); // верх
            sleep(300);
            clawServo.setPosition(0.8); // вниз
            sleep(700);
            // ВРАЩЕНИЕ БАРАБАНА ЧЕРЕЗ СЕРВО ===

            // Первое вращение барабана
            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS );
            drumServo.setPosition(DRUM_STOP);
            sleep(600);

            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS );
            drumServo.setPosition(DRUM_STOP);
            sleep(600);

            // верх в низ клещни
            clawServo.setPosition(0.1); // верх
            sleep(300);
            clawServo.setPosition(0.8); //вниз
            sleep(700);

            // Второе вращение барабана
            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS);
            drumServo.setPosition(DRUM_STOP);
            sleep(600);

            //открыть клешню и отпустить
            clawServo.setPosition(0.1); // открыто
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(700);

            // третье вращение барабана
            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS);
            drumServo.setPosition(DRUM_STOP);
            sleep(600);

            // Снова открыть клешню  отпустить
            clawServo.setPosition(0.1); // открыто
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(700);

            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS);
            drumServo.setPosition(DRUM_STOP);
            sleep(600);

            clawServo.setPosition(0.1); // верх
            sleep(300);
            clawServo.setPosition(0.8); //вниз
            sleep(700);

            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS );
            drumServo.setPosition(DRUM_STOP);
            sleep(600);

            // верх в низ клещни
            clawServo.setPosition(0.1); // верх
            sleep(300);
            clawServo.setPosition(0.8); //вниз
            sleep(700);


            // Снова открыть клешню  отпустить
            clawServo.setPosition(0.1); // открыто
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(500);
            //остановка мотора пулятеля
            armMotor.setPower(0.0);



            // 4. Проехать 120 см назад
            driveDistanceBackward(120.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // Остановка всех систем(для точности )
            stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
            armMotor.setPower(0.0);
            drumServo.setPosition(DRUM_STOP);
            clawServo.setPosition(0.8);



            telemetry.addData("Status", "Autonomous complete!");
            telemetry.update();
        }
    }

    // === Вспомогательные методы (без изменений) ===

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

    private void driveDistance(double distanceCm, double power, boolean backward, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (distanceCm * COUNTS_PER_CM);
        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

        if (backward) {
            leftFront.setTargetPosition(targetTicks);
            leftBack.setTargetPosition(targetTicks);
            rightFront.setTargetPosition(-targetTicks);
            rightBack.setTargetPosition(-targetTicks);
        } else {
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

    private void driveDistanceForward(double distanceCm, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        driveDistance(distanceCm, power, false, leftFront, leftBack, rightFront, rightBack);
    }

    private void driveDistanceBackward(double distanceCm, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        driveDistance(distanceCm, power, true, leftFront, leftBack, rightFront, rightBack);
    }

    private void turnLeft(double degrees, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (degrees * COUNTS_PER_DEGREE);
        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

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

    private void turnRight(double degrees, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (degrees * COUNTS_PER_DEGREE);
        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

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
}


