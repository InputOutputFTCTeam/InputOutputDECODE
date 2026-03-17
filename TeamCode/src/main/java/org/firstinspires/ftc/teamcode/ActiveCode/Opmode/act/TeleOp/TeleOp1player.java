package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "TeleOp1Player", group = "TeleOp")
public class TeleOp1player extends OpMode {

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
        //колесная база
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        //Пулятель
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        uptrigger = hardwareMap.get(DcMotor.class, "uptrigger");
        //подача
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        //карусель
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
        // === Управление движением (Gamepad 1) ===
        double driveSpeedScale = gamepad2.b ? 0.2 : 1.0;
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double turn = 0.0;
        if (gamepad1.left_bumper) {
            turn = -TURN_POWER2;
        } else if (gamepad1.right_bumper) {
            turn = TURN_POWER2;
        }

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
        }

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
        if (gamepad1.a) {
            clawServo.setPosition(CLAW_CLOSE_POS);
        } else {
            clawServo.setPosition(CLAW_OPEN_POS);
        }

        if (gamepad1.x) {
            servo2.setPower(1);

        } else if (gamepad1.y) {
            servo2.setPower(-1);
        } else {
            servo2.setPower(0);
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
        telemetry.update();
    }
}