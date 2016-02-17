package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

/**
 * Created by zipper on 2/16/16.
 */
public class Command  {
    public static int currentCommandNumber = 0;
    public States currentState;
    public boolean continueCommand;
    public static ElapsedTime time;
    public int targetTime;
    public int targetEncoders;
    public Command(States state) {
        currentState = state;
    }
    public void continueCommand(boolean commandExpression) {
        continueCommand = commandExpression;
    }
    public void doCommand() {
        if(!continueCommand && currentState != States.STOPPED) {
            currentCommandNumber++;
        }
    }
    public void setTargetTime(int targetTime) {
        time.reset();
        this.targetTime = targetTime;
    }
    public void setTargetEncoders(int targetEncoders, DcMotor motor) {
        this.targetEncoders = motor.getCurrentPosition() + targetEncoders;
    }
}
