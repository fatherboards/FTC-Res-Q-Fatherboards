package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;


public class AutonomousPostSeason extends OpMode implements RobotStatus{
    public ArrayList<Command> commandList = new ArrayList<Command>();
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
    Servo hang;

    public void setUpCommands() {
        commandList.add(new Command(States.DRIVING));
        commandList.get(0).setTargetTime(3);
        commandList.add(new Command(States.IDLING));
        commandList.get(1).setTargetTime(5);
        commandList.add(new Command(States.TURNING_RIGHT));
        commandList.get(2).setTargetEncoders(1000, frontLeft);
        commandList.add(new Command(States.DUMPING_CLIMBERS));
        commandList.get(3).setTargetTime(3);
        commandList.add(new Command(States.STOPPED));
    }

    public void initServos(Servo servo) {
        servo.setPosition(.5);
    }

    public static void setDriveMode(DcMotorController.RunMode mode, DcMotor motor) {
        if(motor.getChannelMode() != mode) {
            motor.setChannelMode(mode);
        }
    }

    public void initRobotHardware() {
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
        hang = hardwareMap.servo.get("hang");
        initServos(tapeSpin);
        initServos(sliderLeft);
        initServos(sliderRight);
        initServos(armLeft);
        initServos(armRight);
        initServos(autoClimbers);
        initServos(hang);
        resetEncoders();
    }

    @Override
    public void init() {
        setUpCommands();
        initRobotHardware();
    }

    @Override
    public void loop() {
        telemetryValues(frontRight, "Front Right Motor");
        telemetryValues(frontLeft, "Front Left Motor");
        telemetryValues(backRight, "Back Right Motor");
        telemetryValues(backLeft, "Back Left Motor");
        telemetryValues(Command.time.time(), "Current RunTime");
        telemetryValues(commandList.get(Command.currentCommandNumber).currentState, "State");
        if(Command.currentCommandNumber == 0) {
            runThroughCommand(commandList.get(Command.currentCommandNumber), Command.time.time() < commandList.get(Command.currentCommandNumber).targetTime);
            powerMotors(1,1);
            initServos(autoClimbers);
        }
        else if(Command.currentCommandNumber == 1) {
            runThroughCommand(commandList.get(Command.currentCommandNumber), Command.time.time() < commandList.get(Command.currentCommandNumber).targetTime);
            initServos(autoClimbers);
        }
        else if(Command.currentCommandNumber == 2) {
            runThroughCommand(commandList.get(Command.currentCommandNumber), frontLeft.getCurrentPosition() < commandList.get(Command.currentCommandNumber).targetEncoders);
            powerMotors(-1,1);
            initServos(autoClimbers);
        }
        else if(Command.currentCommandNumber == 3) {
            runThroughCommand(commandList.get(Command.currentCommandNumber), Command.time.time() < commandList.get(Command.currentCommandNumber).targetTime);
            autoClimbers.setPosition(0);
        }
        else {
            initServos(autoClimbers);
        }
    }

    @Override
    public void telemetryValues(Object obj, String description) {
        if(obj instanceof DcMotor) {
            telemetry.addData(description, ((DcMotor) obj).getCurrentPosition());
        }
        else if(obj instanceof Servo) {
            telemetry.addData(description, ((Servo) obj).getPosition());
        }
        else {
            telemetry.addData(description, obj.toString());
        }
    }
    public void runThroughCommand(Command command, boolean commandExpression) {
        command.continueCommand(commandExpression);
        command.doCommand();
    }
    public void powerMotors(double rightPower, double leftPower) {
        frontRight.setPower(rightPower);
        backRight.setPower(rightPower);
        frontLeft.setPower(-leftPower);
        backLeft.setPower(-leftPower);
    }
    public void resetEncoders() {
        setDriveMode(DcMotorController.RunMode.RESET_ENCODERS, frontRight);
        setDriveMode(DcMotorController.RunMode.RESET_ENCODERS, frontLeft);
        setDriveMode(DcMotorController.RunMode.RESET_ENCODERS, backRight);
        setDriveMode(DcMotorController.RunMode.RESET_ENCODERS, backLeft);
        setDriveMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS, frontRight);
        setDriveMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS, frontLeft);
        setDriveMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS, backRight);
        setDriveMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS, backLeft);
    }
}
