package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;


/**
 * Created by Zip on 10/8/2015.
 */

public class Codemobile extends OpMode {
    DcMotor    frontright;
    DcMotor     frontleft;
    DcMotor     backright;
    DcMotor      backleft;
    DcMotor          tape;
    Servo        tapeSpin;
    Servo      leftSlider;
    Servo  rightSliderArm;
    Servo     rightSlider;
    Servo   leftSliderArm;
    Servo    autoClimbers;
    Servo          armPos;
    Servo       bucketPos;


    public void init()
    {
        frontright     = hardwareMap.dcMotor.get         ("motor1");
        backright      = hardwareMap.dcMotor.get         ("motor2");
        frontleft      = hardwareMap.dcMotor.get         ("motor3");
        backleft       = hardwareMap.dcMotor.get         ("motor4");
        tape           = hardwareMap.dcMotor.get           ("tape");
        tapeSpin       = hardwareMap.servo.get         ("tapeSpin");
        leftSlider     = hardwareMap.servo.get       ("leftSlider");
        rightSliderArm = hardwareMap.servo.get   ("rightSliderArm");
        rightSlider    = hardwareMap.servo.get      ("rightSlider");
        leftSliderArm  = hardwareMap.servo.get    ("leftSliderArm");
        autoClimbers   = hardwareMap.servo.get     ("autoclimbers");
        armPos         = hardwareMap.servo.get              ("arm");
        bucketPos      = hardwareMap.servo.get           ("bucket");
    }


    public void power(float right, float left, boolean forwards) {
        if(forwards) {
            frontright.setPower(right);
            backright.setPower(right);
            frontleft.setPower(left);
            backleft.setPower(left);
        }
        else {
            frontright.setPower(left);
            backright.setPower(left);
            frontleft.setPower(right);
            backleft.setPower(right);
        }
    }

    public void servoControl(Servo servo, boolean cond1, boolean cond2)
    {
        if(cond1)      { servo.setPosition  (1); }
        else if(cond2) { servo.setPosition  (0); }
        else           { servo.setPosition(0.5); }
    }

    public void loop()
    {

        /// [GAMEPAD 1] ///

        float rightMotorPower = gamepad1.right_stick_y;
        float leftMotorPower  = -gamepad1.left_stick_y;
        rightMotorPower = Range.clip(rightMotorPower, -1, 1);
        leftMotorPower  = Range.clip (leftMotorPower, -1, 1);

        power(rightMotorPower, leftMotorPower, frontright.getDirection()==DcMotor.Direction.FORWARD);
        if(gamepad1.dpad_down) tape.setPower(1);
        else if(gamepad1.dpad_up) tape.setPower(-1);
        else tape.setPower(0);
        servoControl          (tapeSpin, gamepad1.x, gamepad1.y);
        servoControl      (autoClimbers, gamepad1.a, gamepad1.b);

            /// [CHANGE FRONT OF ROBOT] ///

        if(gamepad1.right_bumper && frontright.getDirection()==DcMotor.Direction.FORWARD) {
            frontright.setDirection(DcMotor.Direction.REVERSE);
            backright.setDirection (DcMotor.Direction.REVERSE);
            frontleft.setDirection (DcMotor.Direction.REVERSE);
            backleft.setDirection  (DcMotor.Direction.REVERSE);
        }

        else if(gamepad1.right_bumper && frontright.getDirection()==DcMotor.Direction.REVERSE) {
            frontright.setDirection(DcMotor.Direction.FORWARD);
            backright.setDirection (DcMotor.Direction.FORWARD);
            frontleft.setDirection (DcMotor.Direction.FORWARD);
            backleft.setDirection  (DcMotor.Direction.FORWARD);
        }
            /// [/CHANGE FRONT OF ROBOT] ///

        /// [/GAMEPAD 1] ///

        /// [GAMEPAD 2] ///

        float  leftSliderPower = gamepad2.left_stick_y;
        float rightSliderPower = gamepad2.right_stick_y;

        rightSliderPower = Range.clip(rightSliderPower, -1, 1);
        leftSliderPower  = Range.clip (leftSliderPower, -1, 1);

        servoControl   (leftSlider, leftSliderPower > 0.3, leftSliderPower < -0.3);
        servoControl(rightSlider, rightSliderPower > 0.3, rightSliderPower < -0.3);

        servoControl (leftSliderArm, gamepad2.x, gamepad2.y);
        servoControl(rightSliderArm, gamepad2.a, gamepad2.b);

        servoControl          (armPos, gamepad2.dpad_up, gamepad2.dpad_down);
        servoControl(bucketPos, gamepad2.left_bumper, gamepad2.right_bumper);

        /// [/GAMEPAD 2] ///

    }
}
