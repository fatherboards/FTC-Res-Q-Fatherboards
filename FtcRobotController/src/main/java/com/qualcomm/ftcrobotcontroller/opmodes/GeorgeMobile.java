package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by zipper on 1/30/16.
 */
public class GeorgeMobile extends OpMode {
    DcMotor frontRight;
    DcMotor frontLeft;
    DcMotor backRight;
    DcMotor backLeft;
    DcMotor tape;
    Servo tapeSpin;
    Servo sliderLeft;
    Servo sliderRight;
    Servo armLeft;
    Servo armRight;

    public void init() {
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backRight = hardwareMap.dcMotor.get("backRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        tape = hardwareMap.dcMotor.get("tape");
        tapeSpin = hardwareMap.servo.get("tapeSpin");
        sliderLeft = hardwareMap.servo.get("sliderLeft");
        sliderRight = hardwareMap.servo.get("sliderRight");
        armLeft = hardwareMap.servo.get("armLeft");
        armRight = hardwareMap.servo.get("armRight");

    }

    public void servoControl(Servo servo, boolean cond1, boolean cond2) {
        if(cond1)      { servo.setPosition  (1); }
        else if(cond2) { servo.setPosition  (0); }
        else           { servo.setPosition(0.5); }
    }

    public void motorControl(DcMotor motor, boolean cond1, boolean cond2) {
        if(cond1)      { motor.setPower (1); }
        else if(cond2) { motor.setPower(-1); }
        else           { motor.setPower (0); }
    }

    public void powerMotors(float right, float left) {
        frontRight.setPower(right);
        backRight.setPower(right);
        frontLeft.setPower(left);
        backLeft.setPower(left);
    }

    public void loop() {

        /// [GAMEPAD 1] ///

        float rightPower = gamepad1.right_stick_y;
        float leftPower = -gamepad1.left_stick_y;
        powerMotors(rightPower,leftPower);
        motorControl(tape, gamepad1.dpad_up, gamepad1.dpad_down);
        servoControl(tapeSpin, gamepad1.x, gamepad1.y);

        /// [GAMEPAD 2] ///

        servoControl(sliderLeft, gamepad2.left_stick_y > .1, gamepad2.left_stick_y < -.1);
        servoControl(sliderRight, gamepad2.right_stick_y > .1, gamepad2.right_stick_y < -.1);
        servoControl(armLeft, gamepad2.dpad_up, gamepad2.dpad_down);
        servoControl(armRight, gamepad2.a, gamepad2.b);
    }
}
