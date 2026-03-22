package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.Untils;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "servoSetter", group = "TeleOps")
public class ServoSetter extends LinearOpMode {
    Servo grab;
    double pos = 0.5;
//0.98
    //0.61
    @Override
    public void runOpMode(){
        grab = hardwareMap.servo.get("guide");
        waitForStart();

        while(opModeIsActive()){
            // === Обновление позиции ===
            if(gamepad2.dpad_up && !prevUp) {
                pos += 0.01;
                prevUp = true;
            } else if (!gamepad2.dpad_up) {
                prevUp = false;
            }

            if(gamepad2.dpad_down && !prevDown) {
                pos -= 0.01;
                prevDown = true;
            } else if (!gamepad2.dpad_down) {
                prevDown = false;
            }

            if(gamepad2.dpad_right && !prevRight) {
                pos += 0.1;
                prevRight = true;
            } else if (!gamepad2.dpad_right) {
                prevRight = false;
            }

            if(gamepad2.dpad_left && !prevLeft) {
                pos -= 0.1;
                prevLeft = true;
            } else if (!gamepad2.dpad_left) {
                prevLeft = false;
            }

            // === Ограничение диапазона 0.0 - 1.0 ===
            pos = Math.max(0.0, Math.min(1.0, pos));

            // === ПРИМЕНЯЕМ позицию к серво (ГЛАВНОЕ ИСПРАВЛЕНИЕ) ===
            grab.setPosition(pos);

            // === Телеметрия ===
            telemetry.addData("Position", "%.3f", pos);
            telemetry.addData("Servo command", "%.3f", grab.getPosition());
            telemetry.update();
            idle();
        }
    }

    // Флаги для обработки нажатий (объявить в начале класса)
    private boolean prevUp = false, prevDown = false, prevRight = false, prevLeft = false;
}