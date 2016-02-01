package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
/**
 * Created by Zip on 12/10/2015.
 */
public class MemeCodeAutoCTF extends OpMode{

    DcMotor frontright;
    DcMotor frontleft;
    DcMotor backright;
    DcMotor backleft;
    DcMotor tape;
    Servo tapespin;
    Servo slider;
    Servo climberSlider;
    Servo climberArm;
    public void init() {
        frontright = hardwareMap.dcMotor.get("motor1");
        frontleft = hardwareMap.dcMotor.get("motor2");
        backright = hardwareMap.dcMotor.get("motor3");
        backleft = hardwareMap.dcMotor.get("motor4");
        tape = hardwareMap.dcMotor.get("tape");
        tapespin = hardwareMap.servo.get("tapespin");
        slider = hardwareMap.servo.get("slider");
        climberSlider = hardwareMap.servo.get("climberslider");
        climberArm = hardwareMap.servo.get("climberarm");

    }
    public void power(float right, float left) {
        frontright.setPower(right);
        backright.setPower(right);
        frontleft.setPower(left);
        backleft.setPower(left);
    }
    public void loop()
    {
        double rtm = getRuntime();
        if (rtm <5)
        {
            power(0.5f,-0.5f);
        }
        else if(rtm>5 && rtm<8) {
            slider.setPosition(0);
        }
        else if(rtm>8 && rtm<13) {
            climberSlider.setPosition(.45);
        }
        else if(rtm>13 && rtm<16) {
            climberSlider.setPosition(.6);
        }

    }

}
