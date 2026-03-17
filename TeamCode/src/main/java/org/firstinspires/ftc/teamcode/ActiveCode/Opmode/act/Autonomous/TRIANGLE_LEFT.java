package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.Autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.net.IDN;
import java.util.List;

@Autonomous(name = "RRDrive 4 - LEFT", group = "Auto")
public class TRIANGLE_LEFT extends LinearOpMode {

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

    // === Настройки барабана ===
    // private static final double DRUM_FORWARD = -1.0;     // Для continuous rotation servo
    //  private static final double DRUM_STOP = 0.5;
    //  private static final int DRUM_DURATION_MS = 200;
    private static final double DRUM_POS_0 = 0.0;        // 0°
    private static final double DRUM_POS_120 = 0.5;   // ≈ 0.444
    private static final double DRUM_POS_240 = 1;   // ≈ 0.889
    // Камера
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    @Override
    public void runOpMode() {

        // === Инициализация AprilTag ===
        aprilTag = new AprilTagProcessor.Builder().build();

        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.addProcessor(aprilTag);
        builder.enableLiveView(true);
        builder.setAutoStopLiveView(false);

        visionPortal = builder.build();

        telemetry.addLine("AprilTag scanner ready.");
        telemetry.addLine("If LiveView not visible: open Camera Stream on Driver Station.");
        telemetry.update();

        // === Инициализация моторов ===
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "frontLeft");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "backLeft");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "frontRight");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "backRight");

        DcMotor armMotor = hardwareMap.get(DcMotor.class, "1motor_shooter");
        Servo clawServo = hardwareMap.get(Servo.class, "clawServo");
        Servo drumServo = hardwareMap.get(Servo.class, "carousel");
        DcMotor catchMotor = hardwareMap.get(DcMotor.class, "catch");
        DcMotor CatchMotor1 = hardwareMap.get(DcMotor.class, "catch1");
        Servo servoGun = hardwareMap.get(Servo.class, "guide");//servoGun
        Servo catch_up = hardwareMap.get(Servo.class,"catch_up");
        //Servo servoGun =hardwareMap.get(Servo.class,"servoGun");


        // === Направление моторов — все REVERSE (как у вас) ===
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        // === Начальное положение серво ===
        clawServo.setPosition(0.8); // Закрыто
        drumServo.setPosition(0);
        catch_up.setPosition(1);
        servoGun.setPosition(1);

        telemetry.addData("Status", "Initialized - LEFT SIDE");
        telemetry.update();

        waitForStart();

        // Сначала отъезжаем назад ПРЯМО
        // driveDistanceForward(20.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);
        // sleep(500);

        // turnLeft( 15,TURN_POWER,leftFront,leftBack,rightFront,rightBack);
        sleep(200);
        servoGun.setPosition(0.4);



        if (opModeIsActive()) {

            List<AprilTagDetection> detections = aprilTag.getDetections();
            telemetry.addData("Detections", detections.size());
            telemetry.update();

            // === Fallback: если нет тегов ===
            if (detections.isEmpty()) {
                telemetry.addLine("⚠️ No AprilTag detected! Executing fallback...");
                telemetry.update();

                //  driveDistanceBackward(15.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);
                // sleep(500);
                //servoGun.setPosition(0.5);

                armMotor.setPower(1);
                sleep(1000);
                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(5000);


                drumServo.setPosition(DRUM_POS_120);
                sleep(3000);
                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(5000);
                drumServo.setPosition(DRUM_POS_240);
                sleep(3000);
                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(5000);

                sleep(300);
                clawServo.setPosition(0.8);
                sleep(500);




                stopDriveMotors(leftFront, leftBack, rightFront, rightBack);


                armMotor.setPower(0);
                turnLeft(25,TURN_POWER,leftFront,leftBack,rightFront,rightBack);
                driveDistanceForward(10,DRIVE_POWER,rightFront,rightBack,leftFront,leftBack);
                telemetry.addData("Status", "Fallback complete – no tag found");
                telemetry.update();
                return;
            }

            // === Получаем ID первого тега ===
            int detectedId = detections.get(0).id;
            telemetry.addData("Detected ID", detectedId);
            telemetry.update();

            // === Движение вперёд после сканирования ===
            //   driveDistanceBackward(15.0, DRIVE_POWER, leftFront, leftBack, rightFront, rightBack);
            //  sleep(500);

            // === Выполнение по ID ===
            if (detectedId == 21) {
                armMotor.setPower(1);
                sleep(1000);

                clawServo.setPosition(0.1); sleep(300);
                clawServo.setPosition(0.8); sleep(500);

                drumServo.setPosition(DRUM_POS_120);
                sleep(750);

                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(500);

                drumServo.setPosition(DRUM_POS_240);
                sleep(750);

                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(500);

                armMotor.setPower(0);

            } else if (detectedId == 22) {
                armMotor.setPower(1);
                sleep(1000);

                drumServo.setPosition(DRUM_POS_120);
                sleep(750);


                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(500);

                drumServo.setPosition(0.07);
                sleep(750);


                sleep(750);

                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(500);

                drumServo.setPosition(DRUM_POS_240);
                sleep(750);
                //    drumServo.setPosition(DRUM_FORWARD); sleep(DRUM_DURATION_MS);
                //     drumServo.setPosition(DRUM_STOP);
                sleep(750);

                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(500);

                armMotor.setPower(0);

            } else if (detectedId == 23) {
                armMotor.setPower(1);
                sleep(1000);

                drumServo.setPosition(DRUM_POS_120);
                sleep(750);
                //     drumServo.setPosition(DRUM_FORWARD); sleep(DRUM_DURATION_MS);
                //    drumServo.setPosition(DRUM_STOP);


                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(500);

                drumServo.setPosition(DRUM_POS_240);
                sleep(750);

                sleep(750);

                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(500);

                drumServo.setPosition(0.07);
                sleep(750);

                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(500);

                armMotor.setPower(0);

            } else {
                // Неизвестный ID
                telemetry.addLine("⚠️ Unknown tag ID: " + detectedId);
                telemetry.update();

                armMotor.setPower(1);
                sleep(1000);
                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(5000);
                //armMotor.setPower(0);
                drumServo.setPosition(DRUM_POS_120);
                sleep(750);
                //      drumServo.setPosition(DRUM_FORWARD); sleep(DRUM_DURATION_MS);
                //      drumServo.setPosition(DRUM_STOP);


                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(5000);
                //armMotor.setPower(0);
                drumServo.setPosition(DRUM_POS_240);
                sleep(750);
                //         drumServo.setPosition(DRUM_FORWARD); sleep(DRUM_DURATION_MS);
                //         drumServo.setPosition(DRUM_STOP);
                sleep(750);

                clawServo.setPosition(0.1);
                sleep(300);
                clawServo.setPosition(0.8);
                sleep(5000);
                armMotor.setPower(0);
            }
            turnRight(15,TURN_POWER,leftFront,leftBack,rightFront,rightBack);
            driveDistanceBackward(10,DRIVE_POWER,rightFront,rightBack,leftFront,leftBack);
            // === Завершение ===


            stopDriveMotors(leftFront, leftBack, rightFront, rightBack);
            telemetry.addData("Status", "Autonomous LEFT complete!");
            telemetry.addLine();
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

    private void driveDistanceForward(double distanceCm, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        driveDistance(distanceCm, power, false, leftFront, leftBack, rightFront, rightBack);
    }

    private void driveDistanceBackward(double distanceCm, double power, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        driveDistance(distanceCm, power, true, leftFront, leftBack, rightFront, rightBack);
    }

    private void driveDistance(double distanceCm, double power, boolean backward, DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack) {
        int targetTicks = (int) (distanceCm * COUNTS_PER_CM);
        resetDriveEncoders(leftFront, leftBack, rightFront, rightBack);

        if (backward) {
            // Едем НАЗАД: левые (+), правые (-)
            leftFront.setTargetPosition(-targetTicks);
            leftBack.setTargetPosition(targetTicks);
            rightFront.setTargetPosition(targetTicks);
            rightBack.setTargetPosition(-targetTicks);
        } else {
            // Едем ВПЕРЁД: левые (-), правые (+)
            leftFront.setTargetPosition(targetTicks);
            leftBack.setTargetPosition(-targetTicks);
            rightFront.setTargetPosition(-targetTicks);
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
    }    }
