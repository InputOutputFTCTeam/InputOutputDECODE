package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "TeleOp1layer", group = "TeleOp")
public class TeleOp1player extends OpMode {

    // Движение
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotor motorShotter1;
    private DcMotor catchMotor2;
    private DcMotor сatchMotor1;
    private DcMotor motorShotter2;

    private Servo servo2;
    private CRServo servo22;
    private Servo servoGun;
    private Servo clawServo;
    private Servo catch_up;
    ColorSensor colorSensor;

    // Минимальная коррекция движения
    private static final double CORRECTION_SCALE = 0.1;

    // Позиции клешни
    private final double CLAW_OPEN_POS = 0.98;
    double pos = 0.0;
    private final double CLAW_CLOSE_POS = 0.61;
    private final double CLAW_CLOSE_POS1 = 0;
    private final double TURN_POWER2 = 0.9;
    private double currentAngle = 135;
    private int drumIndex = 1;
    private static final double START_ANGLE = 50;
    private static final double FINAL_ANGLE = 150;
    private static final double FN = 270;
    private static final double FN1 = 270;
    private boolean dpadRightPressed = false;
    private boolean dpadLeftPressed = false;
    private boolean dpadRightPressed1 = false;
    private boolean dpadRightPressed2 = false;

    // === Исправлено: только значения от 0.0 до 1.0 ===
    private double servoGunPosition = 90.0;
    private boolean rightBumperPressed = false;
    private boolean leftBumperPressed = false;

    // Состояния
    private int armState = 0;
    private boolean bPressed = false;
    int catchState = 0;           // 0 = стоп, 1 = вперед (X), -1 = назад (A)
    boolean xPressedG3 = false;   // Флаг для кнопки X
    boolean aPressedG3 = false;   // Флаг для кнопки A

    private boolean dpadUpPressed = false;
    private boolean dpadDownPressed = false;

    private boolean catchMotorsOn = false;
    private boolean isServo2Moving = false;
    private boolean aPressedG2 = false;

    // === Флаги toggle для D-Pad (gamepad1) ===
    private boolean dpadUpToggle = false;
    private boolean dpadDownToggle = false;
    private boolean dpadLeftToggle = false;
    private boolean dpadRightToggle = false;

    // === Флаги для отслеживания нажатия (edge detection) ===
    private boolean dpadUpLast = false;
    private boolean dpadDownLast = false;
    private boolean dpadLeftLast = false;
    private boolean dpadRightLast = false;

    //sensor
    private DistanceSensor distanceSensor;

    // Автомат для последовательности
    private enum SequenceState {
        IDLE,
        STEP1_DOWN,
        STEP2_ROTATE,
        STEP3_UP
    }

    private SequenceState sequenceState = SequenceState.IDLE;
    private ElapsedTime timer = new ElapsedTime();

    @Override
    public void init ( ) {
        // Инициализация моторов и серво
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        motorShotter1 = hardwareMap.get(DcMotor.class, "1motor_shooter");
        motorShotter2 = hardwareMap.get(DcMotor.class, "2motor_shooter");



        clawServo = hardwareMap.get(Servo.class, "clawServo");
        servo2 = hardwareMap.get(Servo.class, "carousel");

        catchMotor2 = hardwareMap.get(DcMotor.class, "catch");
        сatchMotor1 = hardwareMap.get(DcMotor.class, "catch1");
        servo22 = hardwareMap.get(CRServo.class, "catch_3");

        servoGun = hardwareMap.get(Servo.class, "guide");
        catch_up = hardwareMap.get(Servo.class, "catch_up");

        //сенсоры
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        colorSensor = hardwareMap.get(ColorSensor.class, "color");

        // Направления моторов
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        catchMotor2.setDirection(DcMotor.Direction.REVERSE);
        сatchMotor1.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Режимы работы (без энкодеров)
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorShotter1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorShotter2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorShotter1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motorShotter2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


        // Исходное положение барабана — стоп
        drumIndex = 1;
        //   servo2.setPosition((drumIndex * 120.0) / 270.0);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        servo2.setPosition(0);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop ( ) {
        //настройка сенсоров
        double distanceCm = distanceSensor.getDistance(DistanceUnit.CM);

        int r = colorSensor.red();
        int g = colorSensor.green();
        int b = colorSensor.blue();

        String detectedColor;

            if (g > r && g > b) {
                detectedColor = "GREEN";
            }
            else if (b > g) {
                detectedColor = "PURPLE";
            }
            else {
                detectedColor = "ERROR / OTHER";
            }

        // === Управление движением ===
        double y = gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double turn = 0.0;

        turn = gamepad1.left_trigger - gamepad1.right_trigger;
        double fl = y + x + turn;
        double fr = y - x - turn;
        double bl = y - x + turn;
        double br = y + x - turn;

        double max = Math.max(1.0, Math.max(Math.abs(fl),
                Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br)))));
        if (max > 1.0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;

            frontLeft.setPower(fl);
            frontRight.setPower(fr);
            backLeft.setPower(bl);
            backRight.setPower(br);

            // настройка motorShotter
            if (gamepad1.b && !bPressed) {
                armState = (armState == 1) ? 0 : 1;
                motorShotter1.setPower(armState == 1 ? 1.0 : 0.0);
            }


            // === Клешня (A на gamepad2) ===
            if (gamepad1.dpad_up) {
                clawServo.setPosition(CLAW_CLOSE_POS);
            }
            if (gamepad1.dpad_down) {
                clawServo.setPosition(CLAW_OPEN_POS);
            }


   /*  // ВЛЕВО: -120° (только если клешня ОТКРЫТА)
        if (clawServo.getPosition() != CLAW_CLOSE_POS1) {
            if (gamepad2.b && !dpadLeftPressed) {
                double currentPosition = servo2.getPosition();
                double newPosition = currentPosition - 0.19;
                if (newPosition < 0.0) {
                    newPosition = 0.0;
                }
                servo2.setPosition(newPosition);
                dpadLeftPressed = true;
            } else if (!gamepad2.b) {
                dpadLeftPressed = false;
            }
        }

        // ВПРАВО: +120° (только если клешня ОТКРЫТА)
        if (clawServo.getPosition() != CLAW_CLOSE_POS1) {
            if (gamepad2.circle && !dpadRightPressed) {
                double currentPosition = servo2.getPosition();
                double newPosition = currentPosition + 0.19;
                if (newPosition > 1.0) {
                    newPosition = 1.0;
                }
                servo2.setPosition(newPosition);
                dpadRightPressed = true;
            } else if (!gamepad2.circle) {
                dpadRightPressed = false;
            }
        }
*/

            // захват настройка
            if (gamepad1.y) {
                catchMotor2.setPower(1.0);
                сatchMotor1.setPower(1.0);
                servo22.setPower(-1.0);
            } else {
                catchMotor2.setPower(0.0);
                сatchMotor1.setPower(0.0);
                servo22.setPower(0.0);
            }

            if (gamepad1.x) {
                catch_up.setPosition(0.30);
            } else {
                catch_up.setPosition(0.07);
            }

            //*if (gamepad1.dpad_up && !dpadRightPressed) {
            //    pos += 0.01;
            //    dpadRightPressed = true;
            //} else if (!gamepad1.dpad_up) {
            //   dpadRightPressed = false;
            //}
            //if (gamepad1.dpad_down && !dpadLeftPressed) {
            //    pos -= 0.01;
            //    dpadLeftPressed = true;
            //} else if (!gamepad1.dpad_down) {
            //    dpadLeftPressed = false;
            //}
            //if (gamepad1.dpad_right && !dpadRightPressed1) {
            //    pos += 0.1;
            //    dpadRightPressed1 = true;
            //} else if (!gamepad1.dpad_right) {
            //    dpadRightPressed1 = false;
            //}
            //if (gamepad1.dpad_left && !dpadRightPressed2) {
            //    pos -= 0.1;
            //    dpadRightPressed2 = true;
            //} else if (!gamepad1.dpad_left) {
            //    dpadRightPressed2 = false;
            //}
            pos = Math.max(0.0, Math.min(1.0, pos));
            servoGun.setPosition(pos);


            // === Телеметрия ===
            telemetry.addData("Detected Color", detectedColor);
            telemetry.addData("Distance (cm)", distanceCm);
            telemetry.addData("Arm", armState == 1 ? "UP" : "STOP");
            telemetry.addData("Claw", gamepad2.a ? "CLOSED" : "OPEN");
            telemetry.addData("Sequence", sequenceState.name());
            telemetry.addData("Position", "%.3f", pos);
            telemetry.addData("Servo command", "%.3f", servoGun.getPosition());
            telemetry.addData("Drive Speed", gamepad1.b ? "SLOW (20%)" : "NORMAL");
            telemetry.addData("ServoGun", "%.0f°", servoGunPosition);
            telemetry.addData("Catch Motors", catchMotorsOn ? "ON (Intake)" : "OFF");
            telemetry.addData("Drum Pos", "%.0f° (%d/3)", drumIndex * 120.0, drumIndex + 1);
            telemetry.addData("DPad Mode",


                    dpadUpToggle ? "100%" :
                            dpadLeftToggle ? "75%" :
                                    dpadRightToggle ? "25%" :
                                            dpadDownToggle ? "STOP" : "JOYSTICK");
            telemetry.update();
        }
    }
}