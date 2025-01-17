package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

public class MecanumDrive {
    //Motor Variables
    public DcMotor frontLeftDrive;
    public DcMotor frontRightDrive;
    public DcMotor backLeftDrive;
    public DcMotor backRightDrive;

    private Gamepad gamepad1;

    public HardwareMap hardwareMap;

    //Power Variables
    private double frontLeftPower;
    private double frontRightPower;
    private double backLeftPower;
    private double backRightPower;

    //Joystick Variables
    private double joystickAngle;
    private double joystickMagnitude;
    private double turn;

    private float forwardAngle;

    //IMU Variables
    BNO055IMU imu;
    Orientation orientation;

//    SampleMecanumDrive sampleMecanumDrive;

    public MecanumDrive(Gamepad gamepad1, DcMotor frontLeftDrive, DcMotor frontRightDrive, DcMotor backLeftDrive, DcMotor backRightDrive, BNO055IMU imu, HardwareMap hardwareMap) {
        this.frontLeftDrive = frontLeftDrive;
        this.frontRightDrive = frontRightDrive;
        this.backLeftDrive = backLeftDrive;
        this.backRightDrive = backRightDrive;

        this.gamepad1 = gamepad1;

        this.imu = imu;

        this.hardwareMap = hardwareMap;

//        sampleMecanumDrive = new SampleMecanumDrive(hardwareMap);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);
        orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        forwardAngle = orientation.firstAngle;

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void robotCentricDrive() {
        drive(0);
    }

    public void fieldCentricDrive() {
        orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        drive(forwardAngle - orientation.firstAngle);
    }

    int count = 0;

    public void drive(double adjustmentAngle) {
//        if(gamepad1.left_bumper && count > 5) {
//            sampleMecanumDrive.turn(Math.toRadians(90));
//            count = 0;
//        }
//
//        if(gamepad1.right_bumper && count > 5) {
//            sampleMecanumDrive.turn(Math.toRadians(-90));
//            count = 0;
//        }

        count++;

        //Vector Math
        joystickAngle = gamepad1.left_stick_x < 0 ? Math.atan(gamepad1.left_stick_y/gamepad1.left_stick_x) + Math.PI : Math.atan(gamepad1.left_stick_y/gamepad1.left_stick_x);
        joystickAngle -= adjustmentAngle;
        joystickMagnitude = Math.sqrt(Math.pow(gamepad1.left_stick_y, 2) + Math.pow(gamepad1.left_stick_x, 2));
        turn = gamepad1.right_stick_x;

        //Mecanum math, joystick angle and magnitude --> motor power
        double powerFrontLeftBackRight = (Math.sin(joystickAngle) - Math.cos(joystickAngle)) * joystickMagnitude;
        double powerFrontRightBackLeft = (-Math.sin(joystickAngle) - Math.cos(joystickAngle)) * joystickMagnitude;

        if (Double.isNaN(powerFrontLeftBackRight))
        {
            powerFrontLeftBackRight = 0D;
        }
        if (Double.isNaN(powerFrontRightBackLeft))
        {
            powerFrontRightBackLeft = 0D;
        }
        if (gamepad1.a)
        {
            forwardAngle = orientation.firstAngle;
        }

        //Combining power and turn
        frontLeftPower = 0.75*Range.clip(powerFrontLeftBackRight - turn, -1, 1);
        backRightPower = 0.75*Range.clip((-powerFrontLeftBackRight - turn), -1, 1);
        frontRightPower = 0.75*Range.clip(powerFrontRightBackLeft - turn, -1, 1);
        backLeftPower = 0.75*Range.clip((-powerFrontRightBackLeft - turn), -1, 1);

        //Set motor power
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }
}
