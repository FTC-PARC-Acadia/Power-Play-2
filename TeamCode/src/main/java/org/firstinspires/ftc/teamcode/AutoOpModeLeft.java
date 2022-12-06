package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "AutoOpModeLeft", group = "AutoOpModes")
public class AutoOpModeLeft extends LinearOpMode {
    Robot robot;

    public void runOpMode() {
        robot = new Robot(hardwareMap.get(Servo.class, "intake"), hardwareMap.get(DcMotor.class, "lift"), hardwareMap.get(DcMotor.class, "frontLeftDrive"), hardwareMap.get(DcMotor.class, "frontRightDrive"), hardwareMap.get(DcMotor.class, "backLeftDrive"), hardwareMap.get(DcMotor.class, "backRightDrive"), hardwareMap.get(BNO055IMU.class, "imu"));

        waitForStart();

        if (opModeIsActive()) {
            robot.intake.grasp(false);

            robot.drive.move(Direction.RIGHT, 24.5);
            robot.drive.move(Direction.FORWARD, 23);
            robot.drive.move(Direction.RIGHT, 13);

            robot.lift.lift(4);

            robot.intake.grasp(true);

            robot.lift.lift(0);

            robot.drive.move(Direction.LEFT, 13);
            robot.drive.move(Direction.FORWARD, 13);
            //We parked!
        }
    }
}
