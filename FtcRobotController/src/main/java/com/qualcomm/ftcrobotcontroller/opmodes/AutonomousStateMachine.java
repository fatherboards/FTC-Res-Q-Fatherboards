package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.ArrayList;

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
    Servo autoClimbers;
    RobotStatus status;
    double targetTime;
    int COMMAND_NUMBER = 0;
    int LOOP_COUNT = 0;
    ArrayList<Command> commandList = new ArrayList<Command>();
    @Override
    public void init() { status.initRobot();
        new Command(States.IDLING, 10000.0);
        new Command(States.DRIVING, 3000);
        new Command(States.TURNING_RIGHT, 1000);
        new Command(States.BACKING, 2000);
        new Command(States.DUMPING_CLIMBERS, 1000);
        new Command(States.STOPPED, 0);
    }
    @Override
    public void loop() {
        doCommand(commandList.get(COMMAND_NUMBER));
    }
    @Override
    public void stop() {

    }
    public enum States {IDLING,AWAITING_COMMAND, DRIVING, BACKING, TURNING_RIGHT, TURNING_LEFT, DUMPING_CLIMBERS, STOPPED}
    public void doCommand(Command command) {
        switch(command.state) {
            case IDLING:
                status.currentState = "IDLING";
                command.doIdling();
                break;
            case AWAITING_COMMAND:
                command.doAwaitingNext();
                break;
            case DRIVING:
                status.currentState = "DRIVING";
                command.doDriving();
                break;
            case BACKING:
                status.currentState = "BACKING";
                command.doBacking();
                break;
            case TURNING_RIGHT:
                status.currentState = "TURNING_RIGHT";
                command.doTurningRight();
                break;
            case TURNING_LEFT:
                status.currentState = "TURNING_LEFT";
                command.doTurningLeft();
                break;
            case STOPPED:
                status.currentState = "STOPPED";
                status.setDriveMode(DcMotorController.RunMode.RESET_ENCODERS);
                break;
            case DUMPING_CLIMBERS:
                command.doDumpingClimbers();
                break;
        }
        if(LOOP_COUNT%5==0) status.update();
        LOOP_COUNT++;
    }
    private class Command {
        public States state;
        public int targetEncoderValue;
        public int milliseconds;
        public Command(States state, double milliseconds) {
            this.state = state;
            this.targetEncoderValue = 0;
            this.milliseconds = (int) milliseconds;
            targetTime = getRuntime()+milliseconds;
            commandList.add(this);
        }
        public Command(States state, int encoderValue) {
            this.state = state;
            this.targetEncoderValue = (status.currentFrontRight + status.currentBackRight + status.currentFrontLeft + status.currentBackLeft)/4 + encoderValue;
            this.milliseconds = 0;
            commandList.add(this);
        }
        public void doIdling() {
            if(!(getRuntime()< targetTime)) {
                doAwaitingNext();
            }
        }
        public void doAwaitingNext() {
            if(!(getRuntime() < targetTime)) {
                COMMAND_NUMBER++;
            }
        }
        public void doDriving() {
            if (lessThanTarget()) {
                power(.4f,.4f);
            }
            else {
                power(0,0);
                doAwaitingNext();
            }
        }
        public void doBacking() {
            if(lessThanTarget()) {
                power(-.4f, -.4f);
            }
            else {
                power(0,0);
                doAwaitingNext();
            }
        }
        public void doTurningLeft() {
            if(lessThanTarget()) {
                power(.4f, -.4f);
            }
            else {
                power(0,0);
                doAwaitingNext();
            }
        }
        public void doTurningRight() {
            if(lessThanTarget()) {
                power(-.4f, .4f);
            }
            else {
                power(0, 0);
                doAwaitingNext();
            }
        }
        public void doDumpingClimbers() {
            if(getRuntime() < targetTime) {
                autoClimbers.setPosition(.7);
            }
            else {
                autoClimbers.setPosition(.5);
                doAwaitingNext();
            }
        }
        private boolean lessThanTarget() {
            return (status.currentFrontRight + status.currentBackRight + status.currentFrontLeft + status.currentBackLeft)/4 < targetEncoderValue;
        }
        private void power(float right, float left) {
            frontRight.setDirection(DcMotor.Direction.REVERSE);
            backRight.setDirection(DcMotor.Direction.REVERSE);
            backLeft.setDirection(DcMotor.Direction.FORWARD);
            frontLeft.setDirection(DcMotor.Direction.FORWARD);
            frontRight.setPower(right);
            backRight.setPower (right);
            frontLeft.setPower(left);
            backLeft.setPower(left);
        }
    }
    private class RobotStatus {
        public int currentFrontRight;
        public int currentBackRight;
        public int currentFrontLeft;
        public int currentBackLeft;
        String currentState = "";
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
            autoClimbers = hardwareMap.servo.get("autoClimbers");
            setDriveMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            tapeSpin.setPosition(.5);
            sliderLeft.setPosition(.5);
            sliderRight.setPosition(.5);
            armLeft.setPosition(.5);
            armRight.setPosition(.5);
            autoClimbers.setPosition(.5);

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
            telemetry.addData("State:", currentState);
        }
        public void updateEncoders() {
            currentFrontRight += Math.abs(frontRight.getCurrentPosition() - currentFrontRight);
            currentBackRight += Math.abs(backRight.getCurrentPosition() - currentBackRight);
            currentFrontLeft += Math.abs(frontLeft.getCurrentPosition() - currentFrontLeft);
            currentBackLeft += Math.abs(backLeft.getCurrentPosition() - currentBackLeft);
        }
        public void update() {
            telemetryData();
            updateEncoders();
        }
        public void setState(String s) {
            currentState = s;
        }
    }
}
