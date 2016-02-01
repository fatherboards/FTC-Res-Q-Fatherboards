package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robot.Robot;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by zipper on 1/29/16.
 */
public class AutonomousStateMachine extends OpMode {
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
    RobotStatus status;
    double targetTime;
    int COMMAND_NUMBER = 0;
    ArrayList<Command> commandList = new ArrayList<Command>();
    private class Command {
        public States state;
        public Command(States state) {
            this.state = state;
            commandList.add(this);
        }

        public void doIdling() {
            if(getRuntime()< targetTime) {
                doIdling();
            }
            else {
                COMMAND_NUMBER++;
            }
        }
    }

    private class RobotStatus {
        public int currentFrontRight;
        public int currentBackRight;
        public int currentFrontLeft;
        public int currentBackLeft;

        public RobotStatus(int frontRight, int backRight, int frontLeft, int backLeft) {
            this.currentFrontRight = frontRight;
            this.currentBackRight = backRight;
            this.currentFrontLeft = frontLeft;
            this.currentBackLeft = backLeft;
        }

        public void initRobot() {
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

        public void telemetryData() {
            telemetry.addData("Motor front right", "val: " + currentFrontRight);
            telemetry.addData("Motor front left", "val: " + currentFrontLeft);
            telemetry.addData("Motor back right", "val: " + currentBackRight);
            telemetry.addData("Motor back left", "val: " + currentBackLeft);
            telemetry.addData("Time elapsed", "val: " + getRuntime());
        }
        public void updateEncoders() {
            currentFrontRight = frontRight.getCurrentPosition();
            currentBackRight = backRight.getCurrentPosition();
            currentFrontLeft = frontLeft.getCurrentPosition();
            currentBackLeft = backLeft.getCurrentPosition();
        }

        public void update() {
            telemetryData();
            updateEncoders();
        }
    }

    public enum States {IDLING,AWAITING_COMMAND, DRIVING, BACKING, TURNING_CW, TURNING_CCW, DUMPING_CLIMBERS, STOPPED}

    public void doCommand(Command command) {
        switch(command.state) {
            case IDLING:
                targetTime= getRuntime()+100;
                command.doIdling();

        }
    }

    @Override
    public void init() { status.initRobot(); }
    @Override
    public void loop() {
        doCommand(commandList.get(COMMAND_NUMBER));
    }
    @Override
    public void stop() {}


}
