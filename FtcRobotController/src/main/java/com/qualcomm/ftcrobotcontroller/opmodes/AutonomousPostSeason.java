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
    int loopAmounts = 0;
    public void setUpCommands()  {
        commandList.add(new Command(States.DRIVING));
        commandList.add(new Command(States.IDLING));
        commandList.add(new Command(States.TURNING_RIGHT));
        commandList.add(new Command(States.DUMPING_CLIMBERS));
        commandList.add(new Command(States.STOPPED));
    }

    public void initServos(Servo servo) {
        servo.setPosition(.5);
    }

    public static void setDriveMode(DcMotorController.RunMode mode, DcMotor motor) {
        if(motor.getMode() != mode) {
            motor.setMode(mode);
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
    }
    public void telemetryRobotStatus() {
        telemetryValues(commandList.get(Command.currentCommandNumber).currentState, "State");
        telemetryValues(time, "Current RunTime For Command");
        telemetryValues(commandList.get(Command.currentCommandNumber).targetTime, "Target Time");
        telemetryValues(commandList.get(Command.currentCommandNumber).targetEncoders, "Target Time");
        telemetryValues(frontRight.getCurrentPosition(), "Front Right Motor");
        telemetryValues(frontLeft.getCurrentPosition(), "Front Left Motor");
        telemetryValues(backRight.getCurrentPosition(), "Back Right Motor");
        telemetryValues(backLeft.getCurrentPosition(), "Back Left Motor");

    }
    @Override
    public void init() {
        initRobotHardware();
        resetEncoders();
        setUpCommands();
    }

    @Override
    public void loop() {
        telemetryRobotStatus();
        switch(Command.currentCommandNumber) {
            case 0:
                if(loopAmounts==0) { commandList.get(0).setTargetTime(3); resetStartTime(); }
                runThroughCommand(commandList.get(0), time < commandList.get(0).targetTime);
                powerMotors(1, 1);
                initServos(autoClimbers);
                loopAmounts++;
                break;
            case 1:
                if(loopAmounts==0) { commandList.get(1).setTargetTime(5); resetStartTime(); }
                runThroughCommand(commandList.get(1), time < commandList.get(1).targetTime);
                powerMotors(0, 0);
                initServos(autoClimbers);
                loopAmounts++;
                break;
            case 2:
                if(loopAmounts==0) { commandList.get(2).setTargetEncoders(1000, frontLeft); resetEncoders(); }
                runThroughCommand(commandList.get(2), frontLeft.getCurrentPosition() < commandList.get(2).targetEncoders);
                powerMotors(-1, 1);
                initServos(autoClimbers);
                loopAmounts++;
                break;
            case 3:
                if(loopAmounts==0) { commandList.get(3).setTargetTime(3); resetStartTime(); }
                runThroughCommand(commandList.get(3), time < commandList.get(3).targetTime);
                powerMotors(0, 0);
                autoClimbers.setPosition(0);
                loopAmounts++;
                break;
            default:
                initServos(autoClimbers);
                break;
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
        if(!command.continueCommand) loopAmounts=-1;
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
