package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "TeleOp2Player", group = "TeleOp")
public class TeleOpDefault3 extends OpMode {

    // Движение
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotor armMotor;
    private DcMotor catchMotor;
    private DcMotor CatchMotor1;
    private Servo clawServo;
    private DcMotor uptrigger;

    // Continuous rotation servo (барабан) — 6208MG / MG996R
    private Servo servo2;
    private Servo servoGun;

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
    //private final double SERVO2_ROTATE_SPEED = 1.0;   // ← Проверьте направление! Если крутит не туда — поставьте 0.0
    //private final double SERVO2_STOP = 0.5;
    //private final long SERVO2_ROTATE_TIME_MS = 200;  // Настройте под вашу механику
    //серва
    private double servoGunPosition = 90.0; // начальная позиция в градусах (середина 0-180)
    private boolean rightBumperPressed = false; // для +10°
    private boolean leftBumperPressed = false;  // для -10°

    // Состояния
    private int armState = 0;
    private boolean bPressed = false;

    private boolean dpadUpPressed = false;
    private boolean dpadDownPressed = false;
    //private boolean uptriggerOn = false;
    //private boolean yPressedUptrigger = false;
    // private boolean xPressed = false;


    private boolean catchMotorsOn = false;
//    private boolean aPressed = false; // для debounce кнопки A
//   private boolean dpadDownPressedG2 = false;  // для gamepad2.dpad_down
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
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        uptrigger = hardwareMap.get(DcMotor.class, "uptrigger");
        servo2 = hardwareMap.get(Servo.class, "servo2");
        catchMotor = hardwareMap.get(DcMotor.class, "catch");
        CatchMotor1 = hardwareMap.get(DcMotor.class, "catch1");
        servoGun = hardwareMap.get(Servo.class, "servoGun");
        // Направления моторов
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Режимы работы (без энкодеров)
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        uptrigger.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

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
        double x = -gamepad1.left_stick_x * driveSpeedScale;
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
        if (gamepad1.b && !bPressed) {
            armState = (armState == 1) ? 0 : 1;
            servo2.setPosition(245.0);
            bPressed = true;
        } else if (!gamepad1.b) {
            bPressed = false;
        }
        armMotor.setPower(armState == 1 ? 1.0 : 0.0);

        // === Клешня (A на gamepad2) ===
        if (gamepad2.a) {
            clawServo.setPosition(CLAW_CLOSE_POS);
        } else {
            clawServo.setPosition(CLAW_OPEN_POS);
        }
        // === СВОБОДНОЕ ВРАЩЕНИЕ НА +120° БЕЗ ОГРАНИЧЕНИЙ (до 270°) ===

        if (gamepad1.dpad_right && !dpadRightPressed) {
            double currentAngle = servo2.getPosition() * 270.0;
            double newAngle = currentAngle + 120.0;
            if (newAngle > 270.0) newAngle = 270.0; // только ограничение сверху
            servo2.setPosition(newAngle / 270.0);
            dpadRightPressed = true;
        } else if (!gamepad1.dpad_right) {
            dpadRightPressed = false;
        }

        if (gamepad1.dpad_left && !dpadLeftPressed) {
            double currentAngle = servo2.getPosition() * 270.0;
            double newAngle = currentAngle - 120.0;
            if (newAngle < 0.0) newAngle = 0.0; // только ограничение снизу
            servo2.setPosition(newAngle / 270.0);
            dpadLeftPressed = true;
        } else if (!gamepad1.dpad_left) {
            dpadLeftPressed = false;
        }

// Вверх: стартовое положение
        if (gamepad1.dpad_up && !dpadUpPressed) {
            currentAngle = START_ANGLE; // 50°
            servo2.setPosition(currentAngle / 270.0);
            dpadUpPressed = true;
        } else if (!gamepad1.dpad_up) {
            dpadUpPressed = false;
        }

// Вниз: конечное положение
        if (gamepad1.dpad_down && !dpadDownPressed) {
            currentAngle = FINAL_ANGLE; // 150°
            servo2.setPosition(currentAngle / 270.0);
            dpadDownPressed = true;
        } else if (!gamepad1.dpad_down) {
            dpadDownPressed = false;
        }
        //стартовое положение номер 1
            if (gamepad2.dpad_down && !dpadUpPressed) {
                currentAngle = FN;
                servo2.setPosition(currentAngle / 270.0);
                dpadUpPressed = true;
            } else if (!gamepad2.dpad_down) {
                dpadUpPressed = false;}
//стартовое положение номер2
                if (gamepad2.dpad_up && !dpadUpPressed) {
                    currentAngle = FN1-15;
                    servo2.setPosition(currentAngle / 270.0);
                    dpadUpPressed = true;
                } else if (!gamepad2.dpad_up) {
                    dpadUpPressed = false;}

//моторы захвата
        if (gamepad2.a && !aPressedG2) {
            catchMotorsOn = !catchMotorsOn;
            aPressedG2 = true;

            if (catchMotorsOn) {
                catchMotor.setPower(1.0);
                CatchMotor1.setPower(-1.0);
                servo2.setPosition(0.0);
            } else {
                catchMotor.setPower(0.0);
                CatchMotor1.setPower(0.0);

            }
        } else if (!gamepad2.a) {
            aPressedG2 = false;}

        //  //  серва(угол) угол управление +10 градусов
                    if (gamepad2.right_bumper && !rightBumperPressed) {
                        servoGunPosition += 10.0;
                    if (servoGunPosition > 180.0) servoGunPosition = 180.0;
                    servoGun.setPosition(servoGunPosition / 180.0);
                        rightBumperPressed = true;
                    } else if (!gamepad2.right_bumper) {
                        rightBumperPressed = false;
        }
//угол управление -10 градусов
                    if (gamepad2.left_bumper && !leftBumperPressed) {
                        servoGunPosition -= 10.0;
                    if (servoGunPosition < 0.0) servoGunPosition = 0.0;
                    servoGun.setPosition(servoGunPosition / 180.0);
                        leftBumperPressed = true;
                    } else if (!gamepad2.left_bumper) {
                        leftBumperPressed = false;
        }



        // === Телеметрия ===
        telemetry.addData("Arm", armState == 1 ? "UP" : "STOP");
        telemetry.addData("Claw", gamepad2.a ? "CLOSED" : "OPEN");
        telemetry.addData("Sequence", sequenceState.name());
        // telemetry.addData("Servo2 Pos", "%.2f", servo2.setPosition();
        telemetry.addData("Drive Speed", gamepad1.b ? "SLOW (20%)" : "NORMAL");
        telemetry.addData("ServoGun", "%.0f°", servoGunPosition);
        telemetry.addData("Catch Motors", catchMotorsOn ? "ON (Intake)" : "OFF");
     //   telemetry.addData("Drum Pos", "%.0f° (%d/3)", currentAngle, ((int)(currentAngle/120))%3 + 1);
        telemetry.addData("Drum Pos", "%.0f° (%d/3)", drumIndex * 120.0, drumIndex + 1);
        telemetry.update();
    }
}