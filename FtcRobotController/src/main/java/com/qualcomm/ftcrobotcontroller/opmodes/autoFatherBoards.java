package com.qualcomm.ftcrobotcontroller.opmodes;

import    com.qualcomm.robotcore.eventloop.opmode.OpMode;
import           com.qualcomm.robotcore.hardware.DcMotor;
import             com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorController;


public class autoFatherBoards extends OpMode
{
    //// Name the components //////
    DcMotor frontRight;
    DcMotor frontLeft;
    DcMotor backRight;
    DcMotor backLeft;
    DcMotor tape;
    Servo   tapeSpin;
    Servo   leftSlider;
    Servo   rightSliderArm;
    Servo   rightSlider;
    Servo   leftSliderArm;
    Servo   autoClimbers;
    Servo   armPos;
    Servo   bucketPos;

    /// Initialize variables for telemetry and drive calculations ///

    int currentFrontRight = 0;
    int currentFrontLeft  = 0;
    int currentBackRight  = 0;
    int currentBackLeft   = 0;

    double timeAfterDrive = 0;

    @Override
    public void init()

    ////Set up all parts and config////
    {
        /// Motors ///
        frontRight = hardwareMap.dcMotor.get("motor1");
        backRight  = hardwareMap.dcMotor.get("motor2");
        frontLeft  = hardwareMap.dcMotor.get("motor3");
        backLeft   = hardwareMap.dcMotor.get("motor4");
        tape       = hardwareMap.dcMotor.get("tape");

        /// Servos ///

        tapeSpin       = hardwareMap.servo.get("tapeSpin");
        leftSlider     = hardwareMap.servo.get("leftSlider");
        rightSliderArm = hardwareMap.servo.get("rightSliderArm");
        rightSlider    = hardwareMap.servo.get("rightSlider");
        leftSliderArm  = hardwareMap.servo.get("leftSliderArm");
        autoClimbers   = hardwareMap.servo.get("autoclimbers");
        /*armPos         = hardwareMap.servo.get("arm");
        bucketPos      = hardwareMap.servo.get("bucket");*/

        /// Because somehow running without enocders is with encoders ///
        setDriveMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

    }

    private void setDriveMode(DcMotorController.RunMode mode) {
        if (frontRight.getChannelMode() != mode || backRight.getChannelMode() != mode) {
            frontRight.setChannelMode(mode);
            backRight.setChannelMode(mode);
        }
        if (frontLeft.getChannelMode() != mode || backLeft.getChannelMode() != mode) {
            frontLeft.setChannelMode(mode);
            backLeft.setChannelMode(mode);
        }
    }

    public void power(float right, float left) {
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setPower(right);
        backRight.setPower (right);
        frontLeft.setPower (left);
        backLeft.setPower  (left);
    }

    ///// Get current status of robot////

    public void status()
    {
        currentBackRight  = backRight.getCurrentPosition();
        currentBackLeft   = backLeft.getCurrentPosition();
        currentFrontLeft  = frontLeft.getCurrentPosition();
        currentFrontRight = frontRight.getCurrentPosition();
    }

    public void telemtery() {
        telemetry.addData                     ("Motor front right", "val: " + currentFrontRight);
        telemetry.addData                       ("Motor front left", "val: " + currentFrontLeft);
        telemetry.addData                       ("Motor back right", "val: " + currentBackRight);
        telemetry.addData                         ("Motor back left", "val: " + currentBackLeft);
        telemetry.addData                               ("Time elapsed", "val: " + getRuntime());
        if(timeAfterDrive != 0)
        telemetry.addData("Time elapsed since drive", "val: " + (getRuntime() - timeAfterDrive));
    }

    public boolean encode(int encoderValue) {
        /*
         Returns a boolean that shows if the robot
         has travelled the desired amount of encoder
         ticks as of the last telemetry and status update
          */
        return  currentBackLeft   < encoderValue &&
                currentBackRight  < encoderValue &&
                currentFrontLeft  < encoderValue &&
                currentFrontRight < encoderValue;
    }

    public void startRun() {
        /// Initialize servos to home value to avoid auxillary movements ///
        tapeSpin.setPosition      (.5);
        leftSlider.setPosition    (.5);
        rightSlider.setPosition   (.5);
        leftSliderArm.setPosition (.5);
        rightSliderArm.setPosition(.5);
        /*armPos.setPosition        (.5);
        bucketPos.setPosition     (.5);*/

        if (getRuntime()<10) {
            autoClimbers.setPosition(.5);
        }
        else if(encode(4000)) {
            power(0.2f, 0.2f);
            status();
            telemtery();
            autoClimbers.setPosition(.5);
        }
        else if(encode(5000)) {
            power(0.2f, -0.2f);
            status();
            telemtery();
            autoClimbers.setPosition(.5);
        }
        else if(encode(7000)) {
            power(0.2f, 0.2f);
            status();
            telemtery();
            autoClimbers.setPosition(.5);
        }
        else if(timeAfterDrive==0) {
            timeAfterDrive = getRuntime();
            autoClimbers.setPosition(.5);
        }
        else {
            power(0,0);
            /// After parking, move the climber dropping mechanism for 3 seconds to (hopefully) score ///
            if(getRuntime() - timeAfterDrive < 3) { autoClimbers.setPosition(1); }
            else                                  { autoClimbers.setPosition(.5); }
            status();
            telemtery();
        }


    }

    @Override
    public void loop() {
        startRun();
    }
    @Override
    public void stop() {
        /// Reset encoders to zero when the stop button is pressed ///
        setDriveMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

}
