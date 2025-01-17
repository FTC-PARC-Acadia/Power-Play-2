//package org.firstinspires.ftc.teamcode;
//
//import static org.firstinspires.ftc.teamcode.TrajectoryBuilder.backward;
//import static org.firstinspires.ftc.teamcode.TrajectoryBuilder.forward;
//import static org.firstinspires.ftc.teamcode.TrajectoryBuilder.left;
//import static org.firstinspires.ftc.teamcode.TrajectoryBuilder.right;
//
//import com.acmerobotics.roadrunner.geometry.Pose2d;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.ClassFactory;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
//import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
//import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
//import org.firstinspires.ftc.teamcode.drive.advanced.SampleMecanumDriveCancelable;
//
//import java.util.ArrayList;
//
//@Disabled
//@Autonomous(name = "AutonomousRight", group = "AutoOpModes")
//public class AutonomousRight extends LinearOpMode {
//    public int label;
//
//    private static final String TFOD_MODEL_ASSET = "ImageRecognition.tflite";
//
//    private static final String[] LABELS = {
//            "1 green",
//            "2 orange",
//            "3 purple"
//    };
//
//    private static final String VUFORIA_KEY = "AZ6GcV7/////AAABmfVKnUPVmEYUptmw3QQGPrYoGakkYwWd0zW/qAD6t6xQnIEP0joqQnwSb97A4E1E8uNf4f3VeF+KpfB01M2h/NKfHZYkaZMwQPMe0NWZAMJJpeZptIh2B8kD9aLrAyQxb8Mr9oyb5W8D99jkiCecECXqOtkNvC4cvo8iT9c1qtWmuUbOCct4kLPTuQ/SW3VlsjRsruuzOiW9yoo4/XtEZsts0YVdN255mU7xQU9+M8MXbog790+rK4GKwl2JuSpPCC6LhxuvoaX1K2XEJSHR/0OfzsItcNsBh+7lD9NA02EUObjohybJscDPQ8wfCLWgo9BH+KrKM3ZVx3+s42FsXUCYuybMR7um9Xn/pyzKCPvF";
//
//    private VuforiaLocalizer vuforia;
//    private TFObjectDetector tfod;
//
//    ElapsedTime runtime;
//    SampleMecanumDrive drive;
//
//    boolean state;
//
//    Robot robot;
//
//    public void runOpMode() {
//        state = true;
//        drive = new SampleMecanumDrive(hardwareMap);
//        runtime = new ElapsedTime();
//        robot = new Robot(hardwareMap.get(Servo.class, "intake"), new DcMotor[]{hardwareMap.get(DcMotor.class, "lift1"), hardwareMap.get(DcMotor.class, "lift2")});
//
//        Thread autoThread = new AutoThread();
//
//        initVuforia();
//        initTfod();
//
//        if (tfod != null) {
//            tfod.activate();
//            tfod.setZoom(1.0, 16.0/9.0);
//        }
//
//        waitForStart();
//        runtime.reset();
//
//        if (opModeIsActive()) {
//            autoThread.start();
//        }
//
//        while (opModeIsActive()) {
//            if (runtime.seconds() > 20) {
//                autoThread.interrupt();
//                drive.breakFollowing();
//                drive.setDrivePower(new Pose2d());
//            }
//
//            drive.update();
//        }
//    }
//
//    private void runAuto() {
//        robot.intake.grasp(false);
//
//        //Sleeve Detection
//        while (opModeIsActive() && tfod.getRecognitions().isEmpty()) {
//            telemetry.addLine("Hi");
//            telemetry.update();
//        }
//
//        ArrayList<Recognition> recognitions = (ArrayList<Recognition>) tfod.getRecognitions();
//
//        if (recognitions != null) {
//            telemetry.addData("Recognitions", recognitions);
//            telemetry.update();
//
//            label = labelToInt(recognitions.get(0).getLabel());
//            telemetry.addData("Color Detected", recognitions.get(0).getLabel());
//            telemetry.update();
//        }
//        drive.followTrajectory(left(drive, 24));
//        drive.followTrajectory(forward(drive, 51));
//        drive.followTrajectory(right(drive, 12));
//
//        robot.lift.lift(4);
//        drive.followTrajectory(forward(drive, 5));
//        robot.intake.grasp(true);
//        drive.followTrajectory(backward(drive, 5));
//        robot.lift.lift(0);
//        robot.intake.grasp(false);
//
//        drive.followTrajectory(left(drive, 12));
//        drive.followTrajectory(right(drive, (label - 1)*24));
//    }
//
//    private class AutoThread extends Thread {
//        public void run() {
//            while (opModeIsActive() && state) {
//                runAuto();
//            }
//        }
//    }
//
//    public int labelToInt(String label) {
//        return Integer.parseInt(label.substring(0,1));
//    }
//
//    private void initVuforia() {
//        /*
//         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
//         */
//        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
//
//        parameters.vuforiaLicenseKey = VUFORIA_KEY;
//        parameters.cameraName = hardwareMap.get(WebcamName.class, "webcam");
//
//        //  Instantiate the Vuforia engine
//        vuforia = ClassFactory.getInstance().createVuforia(parameters);
//    }
//
//    private void initTfod() {
//        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
//                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
//        tfodParameters.minResultConfidence = 0.5f;
//        tfodParameters.isModelTensorFlow2 = true;
//        tfodParameters.inputSize = 300;
//        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
//        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
//    }
//}
//
