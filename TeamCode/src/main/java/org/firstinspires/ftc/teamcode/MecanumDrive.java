package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class MecanumDrive {
    //312 rpm motor
    public static final double STEP_PER_INCH = 45.285;

    //Motor Variables
    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;

    private Gamepad gamepad1;

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

    public MecanumDrive(Gamepad gamepad1, DcMotor frontLeftDrive, DcMotor frontRightDrive, DcMotor backLeftDrive, DcMotor backRightDrive, BNO055IMU imu) {
        this.frontLeftDrive = frontLeftDrive;
        this.frontRightDrive = frontRightDrive;
        this.backLeftDrive = backLeftDrive;
        this.backRightDrive = backRightDrive;

        this.gamepad1 = gamepad1;

        this.imu = imu;

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

    public MecanumDrive(DcMotor frontLeftDrive, DcMotor frontRightDrive, DcMotor backLeftDrive, DcMotor backRightDrive) {
        this.frontLeftDrive = frontLeftDrive;
        this.frontRightDrive = frontRightDrive;
        this.backLeftDrive = backLeftDrive;
        this.backRightDrive = backRightDrive;

        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeftDrive.setTargetPosition(0);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void move(Direction dir, int inches) {
        double powerFrontLeftBackRight = 0;
        double powerFrontRightBackLeft = 0;

        switch(dir) {
            case FORWARD:
                powerFrontLeftBackRight = 1;
                powerFrontRightBackLeft = 1;
                break;

            case BACKWARD:
                powerFrontLeftBackRight = -1;
                powerFrontRightBackLeft = -1;
                break;

            case RIGHT:
                powerFrontLeftBackRight = 1;
                powerFrontRightBackLeft = -1;
                break;

            case LEFT:
                powerFrontLeftBackRight = -1;
                powerFrontRightBackLeft = 1;
                break;
        }

        frontLeftDrive.setTargetPosition((int)(inches*STEP_PER_INCH));

        frontLeftDrive.setPower(powerFrontLeftBackRight);
        frontRightDrive.setPower(powerFrontRightBackLeft);
        backLeftDrive.setPower(powerFrontRightBackLeft);
        backRightDrive.setPower(powerFrontLeftBackRight);

        if (!frontLeftDrive.isBusy()) {
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
        }
    }

    public void robotCentricDrive() {
        drive(0);
    }

    public void fieldCentricDrive() {
        orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        drive(forwardAngle - orientation.firstAngle);
    }

    public void drive(double adjustmentAngle)
    {
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
        if (gamepad1.x)
        {
            forwardAngle = orientation.firstAngle;
        }

        //Combining power and turn
        frontLeftPower = Range.clip(powerFrontLeftBackRight - turn, -1, 1);
        backRightPower = Range.clip(-(powerFrontLeftBackRight + turn), -1, 1);
        frontRightPower = Range.clip(powerFrontRightBackLeft - turn, -1, 1);
        backLeftPower = Range.clip(-(powerFrontRightBackLeft + turn), -1, 1);

        //Set motor power
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }
}
