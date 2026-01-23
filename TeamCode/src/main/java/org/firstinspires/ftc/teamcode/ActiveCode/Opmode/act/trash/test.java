package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.trash;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
@TeleOp(name = "test", group = "TeleOp")
public class test extends OpMode {
    private Servo servo;
    private double pos = 0.5;
    private final double STEP = 0.1;
    private boolean prevA = false;

    @Override
    public void init ( ) {
        servo = hardwareMap.get(Servo.class, "servo2");
        servo.setPosition(pos);
    }

    @Override
    public void loop ( ) {
        boolean a = gamepad1.a;
        if (a && !prevA) {
            pos = Range.clip(pos + STEP, 0.0, 1.0);
        }
        prevA = a;
        telemetry.addData("Servo pos", "%.2f", pos);
        telemetry.update();
    }
}
