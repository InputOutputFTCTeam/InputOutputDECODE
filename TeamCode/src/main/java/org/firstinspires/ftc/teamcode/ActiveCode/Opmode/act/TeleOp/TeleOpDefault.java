package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "TeleOpDefault", group = "TeleOp")
public class TeleOpDefault extends OpMode {

    // Движение
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotor armMotor;
    private Servo clawServo;
    private DcMotor uptrigger;

    // НОВЫЕ сервы
    private Servo servo2; // 360° (постоянное вращение)

    // Положения
    private final double CLAW_OPEN_POS = 0.8;
    private final double CLAW_CLOSE_POS = 0.1;
    private final double TURN_POWER2 = 0.9;

    private final double SERVO2_ROTATE_SPEED = -0.7;
    private final long SERVO2_ROTATE_TIME_MS = 200;

    // Состояния armMotor
    private int armState = 0;
    private boolean bPressed = false;
    private boolean yPressedArm = false; // для armMotor

    // Состояние uptrigger (отдельно!)
    private boolean uptriggerOn = false;
    private boolean yPressedUptrigger = false;

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
    public void init() {
        // Движение
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        uptrigger = hardwareMap.get(DcMotor.class, "uptrigger");

        // НОВЫЕ сервы
        servo2 = hardwareMap.get(Servo.class, "servo2");

        // Направления
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Режимы
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        uptrigger.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Исходные позиции
        servo2.setPosition(0.5); // стоп

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        // === Управление движением (Gamepad 1) ===
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
        fl /= max; fr /= max; bl /= max; br /= max;

        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);

        // === Toggle armMotor (B на gamepad2) ===
        if (gamepad2.b && !bPressed) {
            armState = (armState == 1) ? 0 : 1;
            bPressed = true;
        } else if (!gamepad2.b) {
            bPressed = false;
        }
        armMotor.setPower(armState == 1 ? 1.0 : (armState == -1 ? -1.0 : 0.0));

   /*     // === Toggle uptrigger (Y на gamepad2) ===
        if (gamepad2.y && !yPressedUptrigger) {
            uptriggerOn = !uptriggerOn; // переключаем состояние
            yPressedUptrigger = true;
        } else if (!gamepad2.y) {
            yPressedUptrigger = false;
        }
        uptrigger.setPower(uptriggerOn ? 1.0 : 0.0);
*/
        // === Клешня (A на gamepad2) ===
        if (gamepad2.a) {
            clawServo.setPosition(CLAW_CLOSE_POS);
        } else {
            clawServo.setPosition(CLAW_OPEN_POS);
        }

        // === НОВАЯ СЕКВЕНЦИЯ: X на gamepad2 ===
        if (gamepad2.x && !xPressed) {
            if (sequenceState == SequenceState.IDLE) {
                sequenceState = SequenceState.STEP1_DOWN;
                timer.reset();
            }
            xPressed = true;
        } else if (!gamepad2.x) {
            xPressed = false;
        }

        // === Выполнение секвенции без delay ===
        switch (sequenceState) {
            case STEP1_DOWN:
                if (timer.milliseconds() > 100) {
                    sequenceState = SequenceState.STEP2_ROTATE;
                    servo2.setPosition(SERVO2_ROTATE_SPEED);
                    timer.reset();
                }
                break;

            case STEP2_ROTATE:
                if (timer.milliseconds() > SERVO2_ROTATE_TIME_MS) {
                    sequenceState = SequenceState.STEP3_UP;
                    servo2.setPosition(0.5); // остановить
                    timer.reset();
                }
                break;

            case STEP3_UP:
                if (timer.milliseconds() > 100) {
                    sequenceState = SequenceState.IDLE;
                }
                break;

            default:
                break;
        }

        // === Телеметрия ===
        telemetry.addData("Drive", "Y: %.2f, X: %.2f", y, x);
        telemetry.addData("Arm", armState == 1 ? "UP" : "STOP");
        telemetry.addData("Uptrigger", uptriggerOn ? "ON" : "OFF");
        telemetry.addData("Claw", gamepad2.a ? "CLOSED" : "OPEN");
        telemetry.addData("Sequence", sequenceState.name());
        telemetry.update();
    }

    // Флаг для X (секвенция)
    private boolean xPressed = false;
}