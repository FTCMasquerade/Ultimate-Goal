package Library4997.MasqControlSystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import Library4997.MasqMotors.MasqMotor;
import Library4997.MasqSensors.MasqAdafruitIMU;
import Library4997.MasqSensors.MasqEncoder;
import Library4997.MasqUtilities.MasqHelpers.MasqEncoderModel;
import Library4997.MasqUtilities.MasqHelpers.MasqHardware;

/**
 * Created by Archish on 4/22/18.
 */

public class MasqPositionTracker implements MasqHardware {
    public MasqAdafruitIMU imu;
    public MasqEncoder yWheel, xWheel;
    private double xStart = 0, xEnd = 0, ignoreXTicks = 0;
    private double yStart = 0, yEnd = 0, ignoreYTicks = 0;
    HardwareMap hardwareMap;
    public MasqPositionTracker(HardwareMap hardwareMap, MasqMotor yWheelMotor, MasqEncoderModel yPPR, MasqMotor xWheelMotor, MasqEncoderModel xPPR) {
        this.hardwareMap = hardwareMap;
        imu = new MasqAdafruitIMU("imu", this.hardwareMap);
        yWheel = new MasqEncoder(yWheelMotor, yPPR);
        xWheel = new MasqEncoder(xWheelMotor, xPPR);
    }
    public double getX () {
        return xWheel.getRelativePosition() - ignoreXTicks;
    }
    public double getY () {
        return yWheel.getRelativePosition() - ignoreYTicks;
    }
    public double getXInches () {
        return xWheel.getInches() - (ignoreXTicks * xWheel.getClicksPerInch());
    }
    public double getYInches () {
        return yWheel.getInches() - (ignoreYTicks * yWheel.getClicksPerInch());
    }
    public double getRotation () {
        return imu.getRelativeYaw();
    }
    public void resetSystem () {
        xWheel.resetEncoder();
        yWheel.resetEncoder();
        imu.reset();
    }
    public void startIgnoringRotation () {
        xStart = getX();
        yStart = getY();
    }
    public void endIgnoringRotation () {
        xEnd = getX();
        yEnd = getY();
        ignoreXTicks = ignoreXTicks + (xEnd - xStart);
        ignoreYTicks = ignoreYTicks + (yEnd - yStart);
    }

    @Override
    public String getName() {
        return "Position Tracker";
    }

    @Override
    public String[] getDash() {
        return new String[]{
                "X "+ getX(),
                "Y " + getY(),
                "θ " + getRotation()
        };
    }
}