package org.firstinspires.ftc.teamcode.testops;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.manipulator.Deposit;
import org.firstinspires.ftc.teamcode.pipelines.BluePropDetectionPipeline;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.opencv.core.Point;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

public class BlueBasicMecAutoShort extends LinearOpMode{



    private Deposit deposit;
    private final RobotHardware robot = RobotHardware.getInstance();
    private OpenCvCamera camera;
    private BluePropDetectionPipeline pipeline;
    private BlueMecanumAuto.PropState currentState;

    SampleMecanumDrive drivetrain = new SampleMecanumDrive(hardwareMap);
    TrajectorySequence Path = drivetrain.trajectorySequenceBuilder(new Pose2d(0, 0, Math.toRadians(0)))
            .forward(10)
            .turn(Math.toRadians(90))
            .forward(50)
            .build();
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap, telemetry);
        deposit = new Deposit(robot);drivetrain = new SampleMecanumDrive(hardwareMap);

        waitForStart();
        drivetrain.followTrajectorySequence(Path);

    }
}
