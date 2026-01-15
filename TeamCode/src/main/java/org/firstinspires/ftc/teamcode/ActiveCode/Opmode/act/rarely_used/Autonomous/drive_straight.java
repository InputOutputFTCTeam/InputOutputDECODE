package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.rarely_used.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


//@Autonomous(name = "Drive Straight 100cm", group = "Auto")
public class drive_straight extends LinearOpMode {

    // Константы
    private static final double WHEEL_DIAMETER_CM = 9.6;        // Диаметр колеса в см
    private static final double COUNTS_PER_MOTOR_REV = 28;      // Ticks на валу мотора
    private static final double DRIVE_GEAR_REDUCTION = 12.0;    // Редуктор 20:1
    private static final double COUNTS_PER_WHEEL_REV = COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION;
    private static final double WHEEL_CIRCUMFERENCE_CM = Math.PI * WHEEL_DIAMETER_CM;
    private static final double COUNTS_PER_CM = COUNTS_PER_WHEEL_REV / WHEEL_CIRCUMFERENCE_CM;

    // Целевое расстояние
    private static final double TARGET_DISTANCE_CM = 100.0;
    private static final int TARGET_TICKS = (int) (COUNTS_PER_CM * TARGET_DISTANCE_CM);

    // Максимальная мощность
    private static final double DRIVE_POWER = 1;

    @Override
    public void runOpMode() {
        // Подключение моторов
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "frontLeft");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "backLeft");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "frontRight");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "backRight");

        // Направление моторов
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Режим энкодера
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized. Target ticks: " + TARGET_TICKS);
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Установка целевого положения
            leftFront.setTargetPosition(TARGET_TICKS);
            leftBack.setTargetPosition(TARGET_TICKS);
            rightFront.setTargetPosition(TARGET_TICKS);
            rightBack.setTargetPosition(TARGET_TICKS);

            // Переключение в режим RUN_TO_POSITION
            leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Установка мощности
            leftFront.setPower(-DRIVE_POWER);
            leftBack.setPower(-DRIVE_POWER);
            rightFront.setPower(-DRIVE_POWER);
            rightBack.setPower(-DRIVE_POWER);

            // Ожидание завершения движения
            while (opModeIsActive() &&
                    (leftFront.isBusy() || leftBack.isBusy() ||
                            rightFront.isBusy() || rightBack.isBusy())) {
                telemetry.addData("Left Front", leftFront.getCurrentPosition());
                telemetry.addData("Right Front", rightFront.getCurrentPosition());
                telemetry.addData("Target", TARGET_TICKS);
                telemetry.update();
            }

            // Остановка моторов
            leftFront.setPower(0);
            leftBack.setPower(0);
            rightFront.setPower(0);
            rightBack.setPower(0);

            telemetry.addData("Status", "Movement complete");
            telemetry.update();
        }
    }
}

