package org.firstinspires.ftc.teamcode.ActiveCode.Opmode.act.TEST;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "AprilTag Scan (VisionPortal)", group = "Vision")
public class cameraTEST extends LinearOpMode {

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    @Override
    public void runOpMode() {

        // 1) Создаём AprilTag processor
        aprilTag = new AprilTagProcessor.Builder()
                // Если знаешь семейство — можно ограничить (ускоряет):
                // .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                // Если знаешь размеры тега и хочешь метры в pose:
                // .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .build();

        // 2) Поднимаем VisionPortal (камера + превью + процессор)
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Выбор камеры:
        // - Для вебки:
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        // - Для встроенной камеры телефона (если нужно), закомментируй строку выше и раскомментируй ниже:
        // builder.setCamera(BuiltinCameraDirection.BACK);

        // Включаем процессор AprilTag
        builder.addProcessor(aprilTag);

        // Превью на DS включено по умолчанию, но оставим явно:
        builder.enableLiveView(true);

        // Можно включить/выключить кнопку остановки стрима в DS:
        builder.setAutoStopLiveView(false);

        visionPortal = builder.build();

        telemetry.addLine("AprilTag scanner ready.");
        telemetry.addLine("If LiveView not visible: open Camera Stream on Driver Station.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            List<AprilTagDetection> detections = aprilTag.getDetections();

            telemetry.addData("Detections", detections.size());

            for (AprilTagDetection det : detections) {
                // Базовое: ID и уверенность
                telemetry.addLine("--------------------------------");
                telemetry.addData("ID", det.id);
                telemetry.addData("DecisionMargin", "%.1f", det.decisionMargin);

                // Центр в пикселях (полезно для наведения)
                telemetry.addData("Center (px)", "(%.0f, %.0f)", det.center.x, det.center.y);

                // Если pose доступен (обычно доступен при наличии intrinsics/tag library):
                if (det.ftcPose != null) {
                    telemetry.addData("Range (m)", "%.3f", det.ftcPose.range);
                    telemetry.addData("Bearing (deg)", "%.2f", det.ftcPose.bearing);
                    telemetry.addData("Yaw (deg)", "%.2f", det.ftcPose.yaw);

                    // Дополнительно:
                    telemetry.addData("X (m)", "%.3f", det.ftcPose.x);
                    telemetry.addData("Y (m)", "%.3f", det.ftcPose.y);
                    telemetry.addData("Z (m)", "%.3f", det.ftcPose.z);
                    telemetry.addData("Pitch (deg)", "%.2f", det.ftcPose.pitch);
                    telemetry.addData("Roll (deg)", "%.2f", det.ftcPose.roll);
                } else {
                    telemetry.addLine("Pose: not available (no intrinsics/tag library).");
                }
            }

            telemetry.update();
            sleep(20);
        }

        // Корректно выключаем камеру
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}