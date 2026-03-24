package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "TeleOp", group = "TeleOp")
public class TeleOp1player extends OpMode {

    // ОБЪЯВЛЕНИЕ ПЕРЕМЕННЫХ
    private DcMotor frontLeft, frontRight, backLeft, backRight, catchMotorRight, catchMotorLeft, motorShooterRight, motorShooterLeft;

    private Servo carousel, catchUp, angle, pusher;
    private CRServo catchServo;

    ColorSensor colorSensor;
    private DistanceSensor distanceSensor;

    int index = 0;
    boolean lastButtonState = false;
    double[] positions = {0.44, 0.00, 0.90};
    private int armState = 0;



    //====================== ИНИЦИАЛИЗАЦИЯ ======================
    @Override
    public void init() {
        // Инициализация моторов и сервоприводов
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        motorShooterRight = hardwareMap.get(DcMotor.class, "motorShooterRight"); //1motor_shooter
        motorShooterLeft = hardwareMap.get(DcMotor.class, "motorShooterLeft"); //2motor_shooter

        pusher = hardwareMap.get(Servo.class, "pusher"); //clawServo

        carousel = hardwareMap.get(Servo.class, "carousel");


        catchMotorRight = hardwareMap.get(DcMotor.class, "catchMotorRight"); //catch
        catchMotorLeft = hardwareMap.get(DcMotor.class, "catchMotorLeft"); //catch1
        catchServo = hardwareMap.get(CRServo.class, "catchServo"); //catch_3

        angle = hardwareMap.get(Servo.class, "angle"); //guide

        catchUp = hardwareMap.get(Servo.class, "catchUp"); //catch_up

        colorSensor = hardwareMap.get(ColorSensor.class, "colorSencor"); //color
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");


        // Направления моторов
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        catchMotorRight.setDirection(DcMotor.Direction.REVERSE);
        catchMotorLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Режимы работы (без энкодеров)
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorShooterRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorShooterLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorShooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motorShooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        telemetry.addData("СТАТУС", "ИНИЦИАЛИЗИРОВАНО");
        telemetry.update();
        telemetry.addData("СТАТУС", "ИНИЦИАЛИЗИРОВАНО");
        telemetry.update();

    }

    //====================== ПОСЛЕ СТАРТА ======================
    @Override
    public void loop() {

        //УПРАВЛЕНИЕ ДВИЖЕНИЕМ
        double y = -gamepad1.left_stick_y ;
        double x = gamepad1.left_stick_x;


        double fl = y + x;
        double fr = y - x;
        double bl = y + x;
        double br = y - x;

        double max = Math.max(1.0, Math.max(Math.abs(fl),
                Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br)))));
        if (max > 1.0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }
        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);


        // УПРАВЛЕНИЕ БАРАБАНОМ
        boolean currentButtonState = gamepad1.b;

        if (currentButtonState && !lastButtonState) {
            index++;
            if (index >= positions.length) {
                index = 0;
            }

            carousel.setPosition(positions[index]);
        }

        lastButtonState = currentButtonState;



        // УПРАВЛЕНИЕ БАРАБАНОМ
        if (gamepad1.a) {
            armState = (armState == 1) ? 0 : 1;
        }
        motorShooterRight.setPower(armState == 1 ? 1.0 : 0.0);

        // УПРАВЛЕНИЕ ТОЛКАТЕЛЯ
        if (gamepad1.x) {
            pusher.setPosition(0.61);
        } else {
            pusher.setPosition(0.98);
        }


        // ЗАХВАТ
        if (gamepad1.y) {
            catchMotorRight.setPower(-1.0);
            catchMotorLeft.setPower(-1.0);
            catchServo.setPower(-1.0);
            catchUp.setPosition(0.07);
        }  else {
            catchMotorRight.setPower(0.0);
            catchMotorLeft.setPower(0.0);
            catchServo.setPower(0.0);
            catchUp.setPosition(0.30);
        }

        //УПРАВЛЕНИЕ УГЛОМ
        double distanceCm = distanceSensor.getDistance(DistanceUnit.CM);

        if (gamepad1.dpad_up && distanceCm > 250){
            angle.setPosition(0.40);
        }
        if(gamepad1.dpad_up && distanceCm <= 250){
            angle.setPosition(-0.30);
        }

        //ДАТЧИК ЦВЕТА
        int g = colorSensor.green();
        int b = colorSensor.blue();

        String detectedColor;

        if (g > b) {
            detectedColor = "ЗЕЛЕННЫЙ";
        }
        else if (b > g) {
            detectedColor = "ФИОЛЕТОВЫЙ";
        }
        else {
            detectedColor = "ПУСТО";
        }

        //ТЕЛЕМЕТРИЯ
        telemetry.addData("ТЕКУЩИЙ АРТЕФАКТ", detectedColor);
        telemetry.addData("РАССТОЯНИЕ см", distanceCm);
        if (distanceCm > 250){
            telemetry.addLine("ДАЛЬНИЙ УГОЛ COS = -0.4");
        }
        if(gamepad1.dpad_up && distanceCm <= 250){
            telemetry.addLine("БЛИЖНИЙ УГОЛ COS = 0.3");
        }

        telemetry.update();
    }
}