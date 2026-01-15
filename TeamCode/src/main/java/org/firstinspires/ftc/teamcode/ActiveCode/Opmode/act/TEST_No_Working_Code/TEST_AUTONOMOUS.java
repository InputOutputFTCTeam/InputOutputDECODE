package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.TEST_No_Working_Code;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "Drive 42", group = "Auto")
public class TEST_AUTONOMOUS extends LinearOpMode {

    // === Настройки колёс и энкодеров ===
    private static final double WHEEL_DIAMETER_CM = 9.6;
    private static final double COUNTS_PER_MOTOR_REV = 1120;
    private static final double DRIVE_GEAR_REDUCTION = 1.0;
    private static final double WHEEL_CIRCUMFERENCE_CM = Math.PI * WHEEL_DIAMETER_CM;
    private static final double COUNTS_PER_CM = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_CM;

    // === Настройка поворота ===
    private static final double TRACK_WIDTH_CM = 35.0;
    private static final double COUNTS_PER_DEGREE = (TRACK_WIDTH_CM * Math.PI / WHEEL_CIRCUMFERENCE_CM) * COUNTS_PER_MOTOR_REV / 360.0;

    private static final double DRIVE_POWER = 1.0;
    private static final double TURN_POWER = 1.0;

    // === Настройки барабана (continuous rotation servo) ===
    private static final double DRUM_FORWARD = -1.0;     // Полный вперёд (значение зависит от серво!)
    private static final double DRUM_STOP = 0.5;        // Нейтраль = стоп
    private static final double DRUM_BACKWARD = 0.0;    // Назад (если нужно)
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
        Servo drumServo = hardwareMap.get(Servo.class, "servo2"); // Барабан как continuous rotation servo

        // === Установка барабана в STOP сразу после инициализации (чтобы не дергался при INIT) ===
        drumServo.setPosition(DRUM_STOP);

        // === Настройка направления моторов колёс ===
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // === Инициализация клешни (опционально: установить в исходное положение) ===
        clawServo.setPosition(0.8); // например, "закрыто" или "вниз"

        telemetry.addData("Status", "Initialized – Drum servo in STOP");
        telemetry.update();

        waitForStart(); // <-- До этого момента НИКАКИХ движений барабана!

        if (opModeIsActive()) {
            armMotor.setPower(1.0); // Включить мотор пушек

            // 1. Проехать 8 см назад
            driveDistanceBackward(8.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 2. Открыть/опустить клешню
            clawServo.setPosition(0.1); // верх
            sleep(300);
            clawServo.setPosition(0.8); // вниз
            sleep(700);

            // === Повторяющиеся действия: вращение барабана + клешня ===
            for (int i = 0; i < 4; i++) {
                // Вращение барабана
                drumServo.setPosition(DRUM_FORWARD);
                sleep(DRUM_DURATION_MS);
                drumServo.setPosition(DRUM_STOP);
                sleep(600);

                // Движение клешни
                clawServo.setPosition(0.1); // верх
                sleep(300);
                clawServo.setPosition(0.8); // вниз
                sleep(700);
            }

            // Последнее открытие клешни
            clawServo.setPosition(0.1); // открыто
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(500);

            // Остановка мотора пушки
            armMotor.setPower(0.0);

            // 3. Отъехать назад на 120 см
            driveDistanceBackward(120.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // Финальная остановка всех систем
            stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
            armMotor.setPower(0.0);
            drumServo.setPosition(DRUM_STOP);
            clawServo.setPosition(0.8);

            telemetry.addData("Status", "Autonomous complete!");
            telemetry.update();
        }
    }

    // === Вспомогательные методы ===

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