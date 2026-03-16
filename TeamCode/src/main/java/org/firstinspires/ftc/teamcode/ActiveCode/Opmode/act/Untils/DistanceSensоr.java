/*package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "TeleOp", group = "TeleOp")
public class EvgenBro extends OpMode {
    private Servo servo2;

    @Override
    public void init ( ) {
        servo2 = hardwareMap.get(Servo.class, "carousel");
    }

    @Override
    public void loop ( ) {
        if (gamepad1.x) {
            servo2.setPosition(1);

        }if (gamepad1.y) {
            servo2.setPosition(0.1);
        } if(gamepad1.b) {
            servo2.setPosition(0);
        }
    }
}*/
package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.Untils;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "DistanceSensоr[TEST]", group = "TeleOp")
public class DistanceSensоr extends LinearOpMode {

    private DistanceSensor distanceSensor;

    @Override
    public void runOpMode() {

        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");

        waitForStart();

        while (opModeIsActive()) {

            double distanceCm = distanceSensor.getDistance(DistanceUnit.CM);

            telemetry.addData("Distance (cm)", distanceCm);
            telemetry.update();
        }
    }
}