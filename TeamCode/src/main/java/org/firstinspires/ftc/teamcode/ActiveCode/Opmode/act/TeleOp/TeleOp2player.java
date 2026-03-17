package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "TeleOp2player", group = "TeleOp")
public class TeleOp2player extends OpMode {

    // Движение
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotor armMotor;
    private DcMotor catchMotor;
    private DcMotor CatchMotor1;

    private DcMotor uptrigger;


    private Servo servo2;
    private CRServo servo22;
    private Servo servoGun;
    private Servo clawServo;
    private Servo catch_up;
    // Минимальная коррекция движения
    private static final double CORRECTION_SCALE = 0.1;

    // Позиции клешни
    private final double CLAW_OPEN_POS = 0.8;
    private final double CLAW_CLOSE_POS = 0.1;
    private final double TURN_POWER2 = 0.9;
    private double currentAngle = 135;
    private int drumIndex = 1;
    private static final double START_ANGLE = 50;
    private static final double FINAL_ANGLE = 150;
    private static final double FN = 270;
    private static final double FN1 = 270;
    private boolean dpadRightPressed = false;
    private boolean dpadLeftPressed = false;
    // === ИСПРАВЛЕНО: только значения от 0.0 до 1.0 ===
    private double servoGunPosition = 90.0; // начальная позиция в градусах (середина 0-180)
    private boolean rightBumperPressed = false;
    private boolean leftBumperPressed = false;

    // Состояния

    //coctoния
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
        armMotor = hardwareMap.get(DcMotor.class, "1motor_shooter");//armmotor
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        uptrigger = hardwareMap.get(DcMotor.class, "2motor_shooter");//uptrigger
        servo2 = hardwareMap.get(Servo.class, "carousel");//servo2
        servo22 = hardwareMap.get(CRServo.class,"catch_3");//servo2
        catchMotor = hardwareMap.get(DcMotor.class, "catch");
        CatchMotor1 = hardwareMap.get(DcMotor.class, "catch1");
        servoGun = hardwareMap.get(Servo.class, "guide");//servoGun
        catch_up = hardwareMap.get(Servo.class,"catch_up");
        // Направления моторов
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        catchMotor.setDirection(DcMotor.Direction.REVERSE);
        CatchMotor1.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Режимы работы (без энкодеров)
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        uptrigger.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Исходное положение барабана — стоп

        drumIndex = 1; // начальная позиция — 120°
        servo2.setPosition((drumIndex * 120.0) / 270.0);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        servo2.setPosition(START_ANGLE / 270.0);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop ( ) {
        // === Управление движением
        double driveSpeedScale = gamepad1.b ? 0.2 : 1.0;
        double y = gamepad1.left_stick_y * driveSpeedScale;
        double x = gamepad1.left_stick_x * driveSpeedScale;
        double turn = 0.0;


        if (armState == 1) {
            double forward = 0.2;
            frontLeft.setPower(forward);
            backLeft.setPower(forward);
            frontRight.setPower(forward);
            backRight.setPower(forward);
        } else {
            double fl = y + x + turn;
            double fr = y - x - turn;
            double bl = y - x + turn;
            double br = y + x - turn;
        }

        if (gamepad1.left_bumper) {
            turn = -TURN_POWER2 * driveSpeedScale;
        } else if (gamepad1.right_bumper) {
            turn = TURN_POWER2 * driveSpeedScale;
        }

        double fl = y + x + turn;
        double fr = y - x - turn;
        double bl = y - x + turn;
        double br = y + x - turn;

        //// Нормализация (если сумма > 1.0)
        double max = Math.max(1.0, Math.max(Math.abs(fl),
               Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br)))));
        if (max > 1.0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }
        //
        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);

        // === Toggle armMotor (B на gamepad2) ===
        if (gamepad2.y&& !bPressed) {
            armState = (armState == 1) ? 0 : 1;
            servo2.setPosition(245.0);
            bPressed = true;
        } else if (!gamepad2.y) {
            bPressed = false;
        }
        armMotor.setPower(armState == 1 ? 1.0 : 0.0);

        // === Клешня (A на gamepad2) ===
        if (gamepad2.a) {
            clawServo.setPosition(CLAW_CLOSE_POS);
        } else {
            clawServo.setPosition(CLAW_OPEN_POS);
        }
// Константа для шага 120° на 270° серво

        // ВЛЕВО: -120° (только если клешня ОТКРЫТА)
        if (clawServo.getPosition() != CLAW_CLOSE_POS) {
            if (gamepad2.b&& !dpadLeftPressed) {
                double currentPosition = servo2.getPosition();
                double newPosition = currentPosition - 0.49;
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
        if (clawServo.getPosition() != CLAW_CLOSE_POS) {
            if (gamepad2.circle&& !dpadRightPressed) {
                double currentPosition = servo2.getPosition();
                double newPosition = currentPosition + 0.49;
                if (newPosition > 1.0) {
                    newPosition = 1.0;
                }
                servo2.setPosition(newPosition);
                dpadRightPressed = true;
            } else if (!gamepad2.circle) {
                dpadRightPressed = false;
            }
        }
        {

            // --- Обработка кнопки X (Вперед) ---
            if (gamepad1.y&& !xPressedG3) {
                if (catchState == 1) {
                    catchState = 0; // Если уже вперед -> выключить
                } else {
                    catchState = 1; // Иначе -> включить вперед
                }
                xPressedG3 = true;
            } else if (!gamepad2.y) {
                xPressedG3 = false;
            }

            // --- Обработка кнопки A (Назад) ---
            if (gamepad1.a && !aPressedG2) {
                if (catchState == -1) {
                    catchState = 0; // Если уже назад -> выключить
                } else {
                    catchState = -1; // Иначе -> включить назад
                }
                aPressedG2 = true;
            } else if (!gamepad1.a) {
                aPressedG2 = false;
            }

            // --- Применение состояния к моторам и серво ---
            if (catchState == 1) {
                // РЕЖИМ ВПЕРЕД (Кнопка X)
                catchMotor.setPower(1.0);
                CatchMotor1.setPower(1.0);
                servo22.setPower(1.0); // Серво крутится вперед (полная скорость)

            } else if (catchState == -1) {
                // РЕЖИМ НАЗАД (Кнопка A)
                catchMotor.setPower(-1.0);
                CatchMotor1.setPower(-1.0);
                servo22.setPower(-1); // Серво крутится назад (полная скорость)

            } else {
                // РЕЖИМ СТОП
                catchMotor.setPower(0.0);
                CatchMotor1.setPower(0.0);
                servo22.setPower(0); // Серво останавливается
            }
        }
         if (gamepad2.left_bumper) {
            servoGun.setPosition(0.05); // стоп или позиция B
        } else {
            servoGun.setPosition(0.8); // назад или позиция C
        }

        if(gamepad2.right_bumper){
            catch_up.setPosition(0.30);
        }
        else {
            catch_up.setPosition(0.07);
        }
        if (gamepad1.dpad_up) {
            backLeft.setPower(1);
            backRight.setPower(1);
            frontLeft.setPower(1);
            frontRight.setPower(1);
        }else  {
            backRight.setPower(br);
            backLeft.setPower(bl);
            frontRight.setPower(fr);
            frontLeft.setPower(fl);
        }
        if(gamepad1.dpad_left){
            backLeft.setPower(0.75);
            backRight.setPower(0.75);
            frontLeft.setPower(0.75);
            frontRight.setPower(0.75);
        } else {
            backRight.setPower(br);
            backLeft.setPower(bl);
            frontRight.setPower(fr);
            frontLeft.setPower(fl);
        }
        if(gamepad1.dpad_right){
            backLeft.setPower(0.25);
            backRight.setPower(0.25);
            frontLeft.setPower(0.25);
            frontRight.setPower(0.25);
        } else {
            frontRight.setPower(fr);
            frontLeft.setPower(fl);
            backRight.setPower(br);
            backLeft.setPower(bl);
        }
        if(gamepad1.dpad_down){
          backLeft.setPower(0);
          backRight.setPower(0);
          frontLeft.setPower(0);
          frontRight.setPower(0);
        } else{
            backRight.setPower(br);
            backLeft.setPower(bl);
            frontRight.setPower(fr);
            frontLeft.setPower(fl);
        }
        // === Телеметрия ===
        telemetry.addData("Arm", armState == 1 ? "UP" : "STOP");
        telemetry.addData("Claw", gamepad2.a ? "CLOSED" : "OPEN");
        telemetry.addData("Sequence", sequenceState.name());
        // telemetry.addData("Servo2 Pos", "%.2f", servo2.setPosition();
        telemetry.addData("Drive Speed", gamepad1.b ? "SLOW (20%)" : "NORMAL");
        telemetry.addData("ServoGun", "%.0f°", servoGunPosition);
        telemetry.addData("Catch Motors", catchMotorsOn ? "ON (Intake)" : "OFF");
        telemetry.addData("Drum Pos", "%.0f° (%d/3)", drumIndex * 120.0, drumIndex + 1);
        telemetry.update();
    }
}
