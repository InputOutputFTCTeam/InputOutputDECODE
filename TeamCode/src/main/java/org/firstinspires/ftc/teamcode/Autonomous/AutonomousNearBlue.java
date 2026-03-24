package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "AutonomousNearBlue", group = "Autonomous")
public class AutonomousNearBlue extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight, catchMotorRight, catchMotorLeft, motorShooterRight, motorShooterLeft;

    private Servo carousel, catchUp, angle, pusher;
    private CRServo catchServo;

    ColorSensor colorSensor;
    private DistanceSensor distanceSensor;

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    int index = 0;
    boolean lastButtonState = false;
    double[] positions = {0.44, 0.00, 0.90};
    private int armState = 0;

    @Override
    public void runOpMode() {

        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");

        carousel = hardwareMap.get(Servo.class, "servo");
        catchUp = hardwareMap.get(Servo.class, "catchUp");

        catchMotorRight = hardwareMap.get(DcMotor.class, "catchMotorRight");

        pusher = hardwareMap.get(Servo.class, "pusher");
        angle = hardwareMap.get(Servo.class, "angle");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        catchMotorRight.setDirection(DcMotor.Direction.REVERSE);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        aprilTag = new AprilTagProcessor.Builder()
                .build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.addProcessor(aprilTag);
        builder.enableLiveView(true);
        builder.setAutoStopLiveView(false);

        visionPortal = builder.build();

        waitForStart();



        if (opModeIsActive()) {
            carousel.setPosition(0.44);
            angle.setPosition(0.40);
            pusher.setPosition(0.98);
            catchUp.setPosition(0.30);
            sleep(500);

            moveForward(100, -0.5);
            sleep(500);

            List<AprilTagDetection> detections = aprilTag.getDetections();
            int detectedId = detections.get(0).id;
            telemetry.addData("ID", detectedId);
            sleep(500);

            catchMotorRight.setPower(1);
            sleep(1000);

            if (detectedId == 21) {
                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);

                carousel.setPosition(0.00);
                sleep(1000);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);

                carousel.setPosition(0.90);
                sleep(100);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);

            }

            if (detectedId == 22) {
                carousel.setPosition(0.00);
                sleep(1000);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);

                carousel.setPosition(0.44);
                sleep(1000);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);

                carousel.setPosition(0.90);
                sleep(100);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);
            }

            if (detectedId == 23) {
                carousel.setPosition(0.00);
                sleep(1000);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);

                carousel.setPosition(0.90);
                sleep(1000);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);

                carousel.setPosition(0.44);
                sleep(100);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);
            }
            else {
                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);

                carousel.setPosition(0.00);
                sleep(1000);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);

                carousel.setPosition(0.90);
                sleep(100);

                pusher.setPosition(0.61);
                sleep(1000);

                pusher.setPosition(0.98);
                sleep(500);
            }
            catchMotorRight.setPower(0);
            sleep(500);

            turn(500, 0.5, false);
            sleep(500);

            moveForward(100, 0.5);
            sleep(500);
        }
    }









    public void moveForward(int ticks, double power) {

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setTargetPosition(ticks);
        frontRight.setTargetPosition(ticks);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(power);
        frontRight.setPower(power);

        while (opModeIsActive() &&
                frontLeft.isBusy() &&
                frontRight.isBusy()) {

            telemetry.addData("frontLeft", frontLeft.getCurrentPosition());
            telemetry.addData("frontRight", frontRight.getCurrentPosition());
            telemetry.update();
        }

        frontLeft.setPower(0);
        frontRight.setPower(0);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void turn(int ticks, double power, boolean rightTurn) {

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        if (rightTurn) {
            frontLeft.setTargetPosition(ticks);
            frontRight.setTargetPosition(-ticks);
        } else {
            frontLeft.setTargetPosition(-ticks);
            frontRight.setTargetPosition(ticks);
        }

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(power);
        frontRight.setPower(power);

        while (opModeIsActive() && frontLeft.isBusy() && frontRight.isBusy()) {
            telemetry.addData("frontLeft", frontLeft.getCurrentPosition());
            telemetry.addData("frontRight", frontRight.getCurrentPosition());
            telemetry.update();
        }

        frontLeft.setPower(0);
        frontRight.setPower(0);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}

