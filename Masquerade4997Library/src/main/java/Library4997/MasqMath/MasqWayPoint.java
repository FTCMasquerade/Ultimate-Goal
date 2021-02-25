package Library4997.MasqMath;

import Library4997.MasqResources.MasqHardware;

/**
 * Created by Archishmaan Peyyety on 2020-01-23.
 * Project: MasqLib
 */
public class MasqWayPoint implements MasqHardware {
    private double x, y, h, targetRadius = 1, modeSwitchRadius = 10, pointSwitchRadius = 10,
            minVelocity = 0.5, maxVelocity = 1, timeout = 2, lookAhead = 10,
            angularCorrectionSpeed = 0.08, speedBias = 0.5, driveCorrectionSpeed = 0.07,
            turnRadius = 5;

    private boolean noHeading = false;

    private String name;
    private PointMode switchMode = PointMode.MECH;

    private Runnable onComplete = () -> {};


    public enum PointMode {
        MECH, TANK, SWITCH
    }

    public MasqWayPoint(){}

    public MasqWayPoint(double x,double y, double h) {
        this.x = x;
        this.y = y;
        this.h = h;
    }

    public MasqWayPoint setSwitchMode(PointMode switchMode) {
        this.switchMode = switchMode;
        return this;
    }

    public MasqWayPoint setPoint(MasqPoint p) {
        this.x = p.getX();
        this.y = p.getY();
        this.h = p.getH();
        return this;
    }

    public MasqWayPoint setPoint(double x, double y, double h) {
        this.x = x;
        this.y = y;
        this.h = h;
        return this;
    }

    public MasqWayPoint setX(double x) {
        this.x = x;
        return this;
    }

    public MasqWayPoint setY(double y) {
        this.y = y;
        return this;
    }

    public MasqWayPoint setH(double h) {
        this.h = h;
        return this;
    }

    public MasqWayPoint setTargetRadius(double targetRadius) {
        modeSwitchRadius += targetRadius - this.targetRadius;
        pointSwitchRadius += targetRadius - this.targetRadius;
        this.targetRadius = targetRadius;
        return this;
    }

    public MasqWayPoint setModeSwitchRadius(double modeSwitchRadius) {
        this.modeSwitchRadius = modeSwitchRadius;
        return this;
    }

    public MasqWayPoint setPointSwitchRadius(double pointSwitchRadius) {
        this.pointSwitchRadius = pointSwitchRadius;
        return this;
    }

    public MasqWayPoint setMinVelocity(double minVelocity) {
        this.minVelocity = minVelocity;
        return this;
    }

    public MasqWayPoint setDriveCorrectionSpeed(double driveCorrectionSpeed) {
        this.driveCorrectionSpeed = driveCorrectionSpeed;
        return this;
    }

    public MasqWayPoint setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
        return this;
    }

    public MasqWayPoint setTimeout(double timeout) {
        this.timeout = timeout;
        return this;
    }

    public MasqWayPoint setLookAhead(double lookAhead) {
        this.lookAhead = lookAhead;
        return this;
    }

    public MasqWayPoint setAngularCorrectionSpeed(double angularCorrectionSpeed) {
        this.angularCorrectionSpeed = angularCorrectionSpeed;
        return this;
    }

    public MasqWayPoint setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getH() {
        return h;
    }

    public double getTargetRadius() {
        return targetRadius;
    }

    public double getModeSwitchRadius() {
        return modeSwitchRadius;
    }

    public double getPointSwitchRadius() {
        return pointSwitchRadius;
    }

    public double getMinVelocity() {
        return minVelocity;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public double getTimeout() {
        return timeout;
    }

    public double getLookAhead() {
        return lookAhead;
    }

    public double getAngularCorrectionSpeed() {
        return angularCorrectionSpeed;
    }

    public PointMode getSwitchMode() {
        return switchMode;
    }

    public double getDriveCorrectionSpeed() {
        return driveCorrectionSpeed;
    }

    public MasqWayPoint setSpeedBias(double speedBias) {
        this.speedBias = speedBias;
        return this;
    }

    public double getSpeedBias() {
        return speedBias;
    }

    public Runnable getOnComplete() {
        return onComplete;
    }

    public void setTurnRadius(double turnRadius) {this.turnRadius = turnRadius;}
    public double getTurnRadius() {return turnRadius;}

    public MasqVector getPoint() {
        return new MasqVector(x,y);
    }

    public MasqWayPoint setName(String name) {
        this.name = name;
        return this;
    }

    public MasqWayPoint setNoHeading(boolean noHeading) {
        this.noHeading = noHeading;
        return this;
    }
    public boolean noHeading() {return noHeading;}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getDash() {
        return new String[0];
    }
}