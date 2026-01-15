package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Drive 4 - LEFT", group = "Auto")
public class big_blue_triangle extends LinearOpMode {

    // === Настройки колёс и энкодеров ===
    private static final double WHEEL_DIAMETER_CM = 9.6;
    private static final double COUNTS_PER_MOTOR_REV = 1120;
    private static final double DRIVE_GEAR_REDUCTION = 1.0;
    private static final double WHEEL_CIRCUMFERENCE_CM = Math.PI * WHEEL_DIAMETER_CM;
    private static final double COUNTS_PER_CM = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_CM;

    // === Настройка поворота
    private static final double TRACK_WIDTH_CM = 35.0;
    private static final double COUNTS_PER_DEGREE = (TRACK_WIDTH_CM * Math.PI / WHEEL_CIRCUMFERENCE_CM) * COUNTS_PER_MOTOR_REV / 360.0;

    private static final double DRIVE_POWER = 1.0;
    private static final double TURN_POWER = 1.0;

    // === Настройки барабана  ===
    private static final double DRUM_FORWARD = -1.0;     // Полный вперёд (напр., по часовой)
    private static final double DRUM_STOP = 0.5;        // Стоп (середина)
    private static final double DRUM_BACKWARD = 0.0;    // Назад (против часовой) — если нужно
    private static final int DRUM_DURATION_MS = 200;    // Время вращения

    //для камеры
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    @Override
    public void runOpMode ( ) {
        // 1) Создаём AprilTag processor)
        aprilTag = new AprilTagProcessor.Builder()
                // Если знаешь семейство — можно ограничить (ускоряет):
                // .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                // Если знаешь размеры тега и хочешь метры в pose:
                // .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .build();

        // 2) Поднимаем VisionPortal (камера + превью + процессор сканирование)
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Выбор камеры:
        // - Для вебки:
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        // - Для встроенной камеры телефона (если нужно), закомментируй строку выше и раскомментируй ниже:
        // builder.setCamera(BuiltinCameraDirection.BACK);

        // Включаем процессор AprilTag
        builder.addProcessor(aprilTag);

        // Превью на DS включено по умолчанию, но оставим явно:
        builder.enableLiveView(true);

        // Можно включить/выключить кнопку остановки стрима в DS:
        builder.setAutoStopLiveView(false);

        visionPortal = builder.build();

        telemetry.addLine("AprilTag scanner ready.");
        telemetry.addLine("If LiveView not visible: open Camera Stream on Driver Station.");


        // === Инициализация моторов движения ===
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "frontLeft");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "backLeft");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "frontRight");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "backRight");

        // === Дополнительные устройства ===
        DcMotor armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        Servo clawServo = hardwareMap.get(Servo.class, "clawServo");
        Servo drumServo = hardwareMap.get(Servo.class, "servo2"); // ← Барабан как серво!




        // === Направление моторов ОСТАВЛЯЕМ ВСЕ FORWARD (как в оригинале) ===
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // === Начальное положение серво ===
        clawServo.setPosition(0.8); // Закрыто

        telemetry.addData("Status", "Initialized - LEFT SIDE");
        telemetry.update();

        waitForStart();


        if (opModeIsActive()) {

            List<AprilTagDetection> detections = aprilTag.getDetections();

            telemetry.addData("Detections", detections.size());

            for (AprilTagDetection det : detections) {
                // Базовое: ID и уверенность
                telemetry.addLine("--------------------------------");
                telemetry.addData("ID", det.id);
                telemetry.addData("DecisionMargin", "%.1f", det.decisionMargin);

                // Центр в пикселях (полезно для наведения)
                telemetry.addData("Center (px)", "(%.0f, %.0f)", det.center.x, det.center.y);

                // Если pose доступен (обычно доступен при наличии intrinsics/tag library):
                if (det.ftcPose != null) {
                    telemetry.addData("Range (m)", "%.3f", det.ftcPose.range);
                    telemetry.addData("Bearing (deg)", "%.2f", det.ftcPose.bearing);
                    telemetry.addData("Yaw (deg)", "%.2f", det.ftcPose.yaw);

                    // Дополнительно:
                    telemetry.addData("X (m)", "%.3f", det.ftcPose.x);
                    telemetry.addData("Y (m)", "%.3f", det.ftcPose.y);
                    telemetry.addData("Z (m)", "%.3f", det.ftcPose.z);
                    telemetry.addData("Pitch (deg)", "%.2f", det.ftcPose.pitch);
                    telemetry.addData("Roll (deg)", "%.2f", det.ftcPose.roll);
                } else {
                    telemetry.addLine("Pose: not available (no intrinsics/tag library).");
                }
            }

            // === Исходная последовательность (зеркально: заменяем повороты) ===

            // 1. Запустить armMotor
            armMotor.setPower(1);

            // 2. Проехать 10 см назад
            driveDistanceBackward(8.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);
            sleep(10);
            // 3. Открыть clawServo
            clawServo.setPosition(0.1); // верх
            sleep(300);
            clawServo.setPosition(0.8); // вниз
            sleep(500);
//вращение барабана
            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS);
            drumServo.setPosition(DRUM_STOP);
            sleep(250);
            // 3. Открыть clawServo
            clawServo.setPosition(0.1);
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(500);
//вращение барабана
            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS);
            drumServo.setPosition(DRUM_STOP);
            sleep(250);
// 3. Открыть clawServo
            clawServo.setPosition(0.1);
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(500);
//вращение барабана
            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS);
            drumServo.setPosition(DRUM_STOP);
            sleep(250);
//открвть серво
            clawServo.setPosition(0.1);
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(500);

            // 5. Проехать 40 см назад
            driveDistanceBackward(12.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 6. Повернуть НАПРАВО на 30 градусов (зеркально: было НАЛЕВО → теперь НАПРАВО)
            turnRight(30.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 7. Проехать 40 см ВПЕРЁД
            driveDistanceForward(8.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 8. Проехать 40 см НАЗАД
            driveDistanceBackward(8.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 9. Повернуть НАЛЕВО на 30 градусов (зеркально: было НАПРАВО → теперь НАЛЕВО)
            turnLeft(30.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 10. Включить armMotor
            armMotor.setPower(1);

            // 11. Проехать 10 см ВПЕРЁД
            driveDistanceForward(12.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 12. Закрыть clawServo
            clawServo.setPosition(0.1); // верх
            sleep(300);
            clawServo.setPosition(0.8); // вниз
            sleep(500);

            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS);
            drumServo.setPosition(DRUM_STOP);
            sleep(250);

            clawServo.setPosition(0.1);
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(500);

            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS);
            drumServo.setPosition(DRUM_STOP);
            sleep(250);

            clawServo.setPosition(0.1);
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(500);

            drumServo.setPosition(DRUM_FORWARD);
            sleep(DRUM_DURATION_MS);
            drumServo.setPosition(DRUM_STOP);
            sleep(250);

            clawServo.setPosition(0.1);
            sleep(300);
            clawServo.setPosition(0.8);
            sleep(500);


            // 13. Проехать 10 см НАЗАД
            driveDistanceBackward(10.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 14. Выключить armMotor
            armMotor.setPower(0.0);

            // === НОВАЯ ДОБАВЛЕННАЯ ПОСЛЕДОВАТЕЛЬНОСТЬ (зеркально) ===

            // 15. Повернуть НАЛЕВО на 40 градусов (было НАПРАВО)
            turnLeft(40.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 16. Проехать 40 см вперёд
            driveDistanceForward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 17. Проехать 40 см назад
            driveDistanceBackward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 18. Повернуть НАПРАВО на 40 градусов (было НАЛЕВО)
            turnRight(40.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 19. Включить armMotor
            armMotor.setPower(-0.4);
            sleep(300);

            // 20. Проехать прямо 40 см
            driveDistanceForward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 21. Открыть clawServo
            clawServo.setPosition(0.8);
            sleep(500);

            // 22. Выключить armMotor
            armMotor.setPower(0.0);
            sleep(100);

            // 23. Отъехать на 70 см назад
            driveDistanceBackward(70.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 24. Повернуть НАЛЕВО на 40 градусов (было НАПРАВО)
            turnLeft(40.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 25. Проехать 40 см вперёд
            driveDistanceForward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 26. Проехать 40 см назад
            driveDistanceBackward(40.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 27. Повернуть НАПРАВО на 40 градусов (было НАЛЕВО)
            turnRight(40.0, TURN_POWER, leftFront, leftBack, rightFront, rightBack);

            // 28. Включить armMotor
            armMotor.setPower(-0.4);
            sleep(300);

            // 29. Проехать 80 см прямо
            driveDistanceForward(80.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 30. Закрыть clawServo
            clawServo.setPosition(0.2);
            sleep(500);

            // 31. Проехать 50 см назад
            driveDistanceBackward(50.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);

            // 32. Выключить armMotor
            armMotor.setPower(0.0);

            // === Завершение ===
            stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
            telemetry.addData("Status", "Autonomous LEFT complete!");
            telemetry.update();
        }
    }

    // === Вспомогательные методы (без изменений) ===

    private void resetDriveEncoders (DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    private void stopDriveMotors (DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setPower(0);
        }
    }

    private void driveDistanceForward (double distanceCm, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        driveDistance(distanceCm, power, false, leftFront, leftBack, rightFront, rightBack);
    }

    private void driveDistanceBackward (double distanceCm, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        driveDistance(distanceCm, power, true, leftFront, leftBack, rightFront, rightBack);
    }

    private void driveDistance (double distanceCm, double power, boolean backward, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
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