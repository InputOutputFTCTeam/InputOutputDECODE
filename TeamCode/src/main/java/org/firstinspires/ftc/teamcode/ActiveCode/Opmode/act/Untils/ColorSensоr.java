package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.Untils;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp(name = "ColorSensоr[TEST]", group = "TeleOp")
public class ColorSensоr extends LinearOpMode {

    ColorSensor colorSensor;

    @Override
    public void runOpMode() {

        colorSensor = hardwareMap.get(ColorSensor.class, "color");

        waitForStart();

        while (opModeIsActive()) {

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

            telemetry.addData("Red", r);
            telemetry.addData("Green", g);
            telemetry.addData("Blue", b);
            telemetry.addData("Detected Color", detectedColor);

            telemetry.update();
        }
    }
}