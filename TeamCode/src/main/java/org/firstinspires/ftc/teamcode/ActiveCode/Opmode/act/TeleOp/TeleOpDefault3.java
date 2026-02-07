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
    private Servo clawServo;
    private DcMotor uptrigger;

    // Continuous rotation servo (барабан) — 6208MG / MG996R
    private CRServo servo2;

    // Минимальная коррекция движения
    private static final double CORRECTION_SCALE = 0.1;

    // Позиции клешни
    private final double CLAW_OPEN_POS = 0.8;
    private final double CLAW_CLOSE_POS = 0.1;
    private final double TURN_POWER2 = 0.9;

    // === ИСПРАВЛЕНО: только значения от 0.0 до 1.0 ===
    private final double SERVO2_ROTATE_SPEED = 1.0;   // ← Проверьте направление! Если крутит не туда — поставьте 0.0
    private final double SERVO2_STOP = 0.5;
    private final long SERVO2_ROTATE_TIME_MS = 200;  // Настройте под вашу механику

    // Состояния
    private int armState = 0;
    private boolean bPressed = false;
    private boolean uptriggerOn = false;
    private boolean yPressedUptrigger = false;
    private boolean xPressed = false;

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
        servo2 = hardwareMap.get(CRServo.class, "servo2");

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
        servo2.setPower(0);

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

        if (gamepad2.x) {
            servo2.setPower(1);

        } else if (gamepad2.y) {
            servo2.setPower(-1);
        } else {
            servo2.setPower(0);
        }
/*
        //  ===
        if (gamepad1.x && !xPressed) {
            servo2.setPosition(120.0 / 180.0); // = 0.6667
            xPressed = true;
        } else if (!gamepad1.x) {
            xPressed = false;
        }*/
   /*    // === ЗАПУСК СЕКВЕНЦИИ: X на gamepad1 ===
        if (gamepad1.x && !xPressed) {
            servo2.setPosition(120.0 / 180.0); // = 0.6667
            xPressed = true;
        } else if (!gamepad1.x) {
            xPressed = false;
        }*/

/*        // === РУЧНОЕ УПРАВЛЕНИЕ барабаном (только в IDLE) ===
        if (sequenceState == SequenceState.IDLE) {
            double stickX = gamepad1.right_stick_x;
            if (Math.abs(stickX) > 0.1) {
                // Преобразуем [-1, 1] → [1.0, 0.0]
                double drumPower = 0.5 - (stickX * 0.5);
                drumPower = Math.max(0.0, Math.min(1.0, drumPower));
                servo2.setPower(drumPower);
            } else {
                servo2.setPower(SERVO2_STOP); // ← ГАРАНТИРОВАННАЯ ОСТАНОВКА
            }
        }

        // === АВТОМАТИЧЕСКАЯ СЕКВЕНЦИЯ ===
        switch (sequenceState) {
            case STEP1_DOWN:
                if (timer.milliseconds() > 100) {
                    sequenceState = SequenceState.STEP2_ROTATE;
                    servo2.setPower(SERVO2_ROTATE_SPEED); // начать вращение
                    timer.reset();
                }
                break;

            case STEP2_ROTATE:
                if (timer.milliseconds() > SERVO2_ROTATE_TIME_MS) {
                    sequenceState = SequenceState.STEP3_UP;
                    servo2.setPower(SERVO2_STOP); // ← ОБЯЗАТЕЛЬНО ОСТАНОВИТЬ
                    timer.reset();
                }
                break;

            case STEP3_UP:
                if (timer.milliseconds() > 100) {
                    sequenceState = SequenceState.IDLE;
                    servo2.setPower(SERVO2_STOP); // ← СТРАХОВКА
                }
                break;

            default:
                break;
        }
*/
        // === Телеметрия ===
        telemetry.addData("Arm", armState == 1 ? "UP" : "STOP");
        telemetry.addData("Claw", gamepad2.a ? "CLOSED" : "OPEN");
        telemetry.addData("Sequence", sequenceState.name());
        // telemetry.addData("Servo2 Pos", "%.2f", servo2.setPosition();
        telemetry.addData("Drive Speed", gamepad1.b ? "SLOW (20%)" : "NORMAL");
        telemetry.update();
    }
}