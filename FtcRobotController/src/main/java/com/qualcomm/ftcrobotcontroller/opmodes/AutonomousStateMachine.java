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
    RobotStatus status = new RobotStatus(0,0,0,0);
    double targetTime;
    int COMMAND_NUMBER = 0;
    int LOOP_COUNT = 0;
    int targetEncoderValue;
    ArrayList<Command> commandList = new ArrayList<Command>();
    @Override
    public void init() { status.initRobot();
        new Command(States.IDLING, 10000.0);
        new Command(States.DRIVING, 5000);
        new Command(States.TURNING_LEFT, 1000);
        new Command(States.DRIVING, 1000);
        new Command(States.DUMPING_CLIMBERS, 1000);
        new Command(States.STOPPED, 0);
    }
    @Override
    public void loop() {
        int prevCommandNumber = COMMAND_NUMBER;
        doCommand(commandList.get(COMMAND_NUMBER));
        if(COMMAND_NUMBER > prevCommandNumber) {
            status.setDriveMode(DcMotorController.RunMode.RESET_ENCODERS);
            commandList.get(COMMAND_NUMBER).updateVals();
        }
    }
    @Override
    public void stop() {
        status.setDriveMode(DcMotorController.RunMode.RESET_ENCODERS);
    }
    public enum States {IDLING,AWAITING_COMMAND, DRIVING, BACKING, TURNING_RIGHT, TURNING_LEFT, DUMPING_CLIMBERS, STOPPED}
    public void doCommand(Command command) {
        switch(command.state) {
            case IDLING:
                status.currentState = "IDLING";
                targetTime = command.milliseconds;
                command.doIdling();
                break;
            case AWAITING_COMMAND:
                command.doAwaitingNext();
                break;
            case DRIVING:
                status.currentState = "DRIVING";
                targetEncoderValue = command.encoderValue;
                command.doDriving();
                break;
            case BACKING:
                status.currentState = "BACKING";
                targetEncoderValue = command.encoderValue;
                command.doBacking();
                break;
            case TURNING_RIGHT:
                status.currentState = "TURNING_RIGHT";
                targetEncoderValue = command.encoderValue;
                command.doTurningRight();
                break;
            case TURNING_LEFT:
                status.currentState = "TURNING_LEFT";
                targetEncoderValue = command.encoderValue;
                command.doTurningLeft();
                break;
            case STOPPED:
                status.currentState = "STOPPED";
                break;
            case DUMPING_CLIMBERS:
                status.currentState = "DUMPING_CLIMBERS";
                targetTime = command.milliseconds;
                command.doDumpingClimbers();
                break;
        }
        if(LOOP_COUNT%5==0) status.update();
        LOOP_COUNT++;
    }
    private class Command {
        public States state;
        public int milliseconds;
        int encoderValue;
        public Command(States state, double milliseconds) {
            this.state = state;
            this.encoderValue = 0;
            this.milliseconds = (int) milliseconds;
            commandList.add(this);
        }
        public Command(States state, int encoderValue) {
            this.state = state;
            this.encoderValue= (status.currentFrontRight  + status.currentBackRight + status.currentBackLeft)/3 + encoderValue;
            this.milliseconds = 0;
            commandList.add(this);
        }
        public void doIdling() {
            if(!(getRuntime()< targetTime)) {
                COMMAND_NUMBER++;
                doAwaitingNext();
            }
        }
        public void doAwaitingNext() {

        }
        public void doDriving() {
            if (lessThanTarget()) {
                power(.4f,.4f);
            }
            else {
                power(0,0);
                COMMAND_NUMBER++;
                doAwaitingNext();
            }
        }
        public void doBacking() {
            if(lessThanTarget()) {
                power(-.4f, -.4f);
            }
            else {
                power(0,0);
                COMMAND_NUMBER++;
                doAwaitingNext();
            }
        }
        public void doTurningLeft() {
            if(lessThanTarget()) {
                power(.4f, -.4f);
            }
            else {
                power(0,0);
                COMMAND_NUMBER++;
                doAwaitingNext();
            }
        }
        public void doTurningRight() {
            if(lessThanTarget()) {
                power(-.4f, .4f);
            }
            else {
                power(0, 0);
                COMMAND_NUMBER++;
                doAwaitingNext();
            }
        }
        public void doDumpingClimbers() {
            if(getRuntime() < targetTime) {
                autoClimbers.setPosition(.2);
            }
            else {
                autoClimbers.setPosition(.5);
                COMMAND_NUMBER++;
                doAwaitingNext();
            }
        }
        private boolean lessThanTarget() {
            return (status.currentFrontRight +status.currentBackRight)/2 < targetEncoderValue;
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
        private void updateVals() {
            this.encoderValue+= (status.currentBackRight+status.currentFrontRight)/2;
            this.milliseconds+= getRuntime();
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
            telemetry.addData("Motor front right",currentFrontRight);
            telemetry.addData("Motor front left", currentFrontLeft);
            telemetry.addData("Motor back right", currentBackRight);
            telemetry.addData("Motor back left", currentBackLeft);
            telemetry.addData("Time elapsed", getRuntime());
            telemetry.addData("State", currentState);
            telemetry.addData("Target Time", targetTime);
            telemetry.addData("Target Encoders", targetEncoderValue);

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
