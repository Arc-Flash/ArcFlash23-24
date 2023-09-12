package org.firstinspires.ftc.teamcode.manipulator;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import android.graphics.Color;
import android.view.View;

import com.arcrobotics.ftclib.util.Timing;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.PIDController;
import org.firstinspires.ftc.teamcode.RobotHardware;

public class Deposit {
    private Servo slideServo1;
    private Servo slideServo2;
    private Servo latchServo;
    private Servo swivelServo;
    private NormalizedColorSensor colorSensor;
    private View relativeLayout;

    private double targetPosition1 = 0;
    private double targetPosition2 = 0;
    private double latchPosition = 0;
    private double swivelPosition = 0;

    private static double latchServoOpen = 90;
    private static double latchServoClosed = 0;


    // PLACEHOLDER VALUES
    private static double downPosition = 0;

    private static double lowPosition1 = 0.2;
    private static double lowPosition2 = 0.2;

    private static double midPosition1 = 0.6;
    private static double midPosition2 = 0.6;

    private static double highPosition1 = 0.8;
    private static double highPosition2 = 0.8;

    private static double swivelServoLeft = 0;
    private static double swivelServoRight = 0.3;
    private static double swivelServoCenter = 0.6;

    private boolean isServoToggled = false;

    private double slideSpeed;

    private SlideState currentSlideState = SlideState.DOWN;
    private SlideState targetSlideState = SlideState.DOWN;
    private LatchState currentLatchState = LatchState.CLOSED;
    private SwivelState currentSwivelState = SwivelState.CENTER;
    private SwivelState targetSwivelState = SwivelState.CENTER;
    private DepositState currentDepositState = DepositState.NO_PIXEL;

    private Timing.Timer timer = new Timing.Timer(1);


    private int colorSensorGain = 2;
    private float[] hsvValues = new float[3];




    public Deposit(RobotHardware robot){
        slideServo1 = robot.slideServo1;
        slideServo2 = robot.slideServo2;
        latchServo = robot.latchServo;
        swivelServo = robot.swivelServo;
        colorSensor = robot.colorSensor;
        relativeLayout = robot.relativeLayout;

        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight)colorSensor).enableLight(true);
        }

        colorSensor.setGain(colorSensorGain);
    }

    public void loop(){



        slideServo1.setPosition((int)targetPosition1);
        slideServo2.setPosition((int)targetPosition2);
        latchServo.setPosition(latchPosition);
        swivelServo.setPosition(swivelPosition);
        telemetry.addData("Current Slide State", currentSlideState);
        telemetry.addData("Target Slide State", targetSlideState);
        telemetry.addData("Current Swivel State", currentSwivelState);
        telemetry.addData("Target Swivel State", targetSwivelState);
        telemetry.addData("Latch State", currentLatchState);
        telemetry.addData("Deposit State", currentDepositState);
        telemetry.addData("Slide Motor 1 Current Position",  slideServo1.getPosition());
        telemetry.addData("Slide Motor 2 Current Position",  slideServo2.getPosition());
        telemetry.addData("Slide Motor 1 Target Position", targetPosition1);
        telemetry.addData("Slide Motor 2 Target Position", targetPosition2);
        telemetry.addData("Latch Servo Current Position", latchServo.getPosition());
        telemetry.addData("Latch Servo Target Position", latchPosition);

        runColorSensor();

        if(Math.abs(targetPosition1-slideServo1.getPosition()) < 10 || Math.abs(targetPosition2-slideServo2.getPosition()) < 10){
            currentSlideState = SlideState.TRANSITION;
        } else {
            currentSlideState = targetSlideState;
        }

        if(Math.abs(swivelPosition-swivelServo.getPosition()) < 0.01){
            currentSwivelState = SwivelState.TRANSITION;
        } else {
            currentSwivelState = targetSwivelState;
        }

    }

    public void latchToggle(){
        if(currentSlideState != SlideState.TRANSITION && currentSwivelState != SwivelState.TRANSITION) {
            isServoToggled = !isServoToggled;
            latchPosition = isServoToggled ? latchServoOpen : latchServoClosed;
            currentLatchState = isServoToggled ? LatchState.OPEN : LatchState.CLOSED;
        }
    }

    public void twoPixelScore(){
        timer.start();
        latchToggle();
        if(timer.elapsedTime() == 0.2) latchToggle();
        if (timer.elapsedTime() == 0.5) latchToggle();
        if (timer.elapsedTime() == 0.7) latchToggle();

    }


    public void setDownPosition(){
         if(currentLatchState == LatchState.OPEN) latchToggle();

            targetPosition1 = downPosition;
            targetPosition2 = downPosition;
            targetSlideState = SlideState.DOWN;
    }

    public void setLowPosition(){
        if(currentDepositState == DepositState.HAS_PIXEL) {
            if(currentLatchState == LatchState.OPEN) latchToggle();
            targetPosition1 = lowPosition1;
            targetPosition2 = lowPosition2;
            targetSlideState = SlideState.LOW;
        }

    }

    public void setMidPosition(){
        if(currentDepositState == DepositState.HAS_PIXEL) {
            if(currentLatchState == LatchState.OPEN) latchToggle();
            targetPosition1 = midPosition1;
            targetPosition2 = midPosition2;
            targetSlideState = SlideState.MID;
        }
    }

    public void setHighPosition(){
        if(currentDepositState == DepositState.HAS_PIXEL) {
            if(currentLatchState == LatchState.OPEN) latchToggle();
            targetPosition1 = highPosition1;
            targetPosition2 = highPosition2;
            targetSlideState = SlideState.HIGH;
        }
    }

    public void setSwivelServoLeft(){
        swivelPosition = swivelServoLeft;
        targetSwivelState = SwivelState.LEFT;
    }

    public void setSwivelServoRight(){
        swivelPosition = swivelServoRight;
        targetSwivelState = SwivelState.RIGHT;
    }

    public void setSwivelServoCenter(){
        swivelPosition = swivelServoCenter;
        targetSwivelState = SwivelState.CENTER;
    }

    public void runColorSensor(){

        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        Color.colorToHSV(colors.toColor(),  hsvValues);

        telemetry.addLine()
                .addData("Red", "%.3f", colors.red)
                .addData("Green", "%.3f", colors.green)
                .addData("Blue", "%.3f", colors.blue);
        telemetry.addLine()
                .addData("Hue", "%.3f", hsvValues[0])
                .addData("Saturation", "%.3f", hsvValues[1])
                .addData("Value", "%.3f", hsvValues[2]);
        telemetry.addData("Alpha", "%.3f", colors.alpha);

        if (colorSensor instanceof DistanceSensor) {
            telemetry.addData("Distance (cm)", "%.3f", ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM));

            if(((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM) < 1){
                currentDepositState = DepositState.HAS_PIXEL;
            } else {
                currentDepositState = DepositState.NO_PIXEL;
            }
        }


        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.HSVToColor(hsvValues));
            }
        });

    }

    public enum SlideState{
        DOWN,
        LOW,
        MID,
        HIGH,
        TRANSITION
    }

    public enum SwivelState{
        LEFT,
        RIGHT,
        CENTER,
        TRANSITION
    }

    public enum LatchState{
        OPEN,
        CLOSED
    }

    public enum DepositState{
        HAS_PIXEL,
        NO_PIXEL
    }



}

