package org.firstinspires.ftc.teamcode.Osiris.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Osiris.Autonomous.RingDetector.TargetZone;
import org.firstinspires.ftc.teamcode.Osiris.Robot.Osiris;

import MasqLibrary.MasqOdometry.MasqWayPoint;
import MasqLibrary.MasqResources.MasqLinearOpMode;

import static MasqLibrary.MasqOdometry.MasqWayPoint.PointMode.*;
import static MasqLibrary.MasqResources.MasqUtils.clip;
import static MasqLibrary.MasqResources.MasqUtils.turnController;
import static MasqLibrary.MasqRobot.OpMode.AUTO;
import static org.firstinspires.ftc.teamcode.Osiris.Autonomous.RingDetector.TargetZone.*;

/**
 * Created by Keval Kataria on 3/8/2021
 */

@Autonomous(preselectTeleOp = "RobotTeleOp", group = "Main")
public class Red extends MasqLinearOpMode {
    private final Osiris robot = new Osiris();
    TargetZone zone = C;
    private final MasqWayPoint target = new MasqWayPoint().setTimeout(5).setSwitchMode(SWITCH)
            .setTargetRadius(5).setAngularCorrectionSpeed(0.01).setModeSwitchRadius(24)
            .setName("Drop Zone").setDriveCorrectionSpeed(0.13);

    @Override
    public void runLinearOpMode() {
        robot.init(AUTO);
        RingDetector detector = (RingDetector) robot.camera.detector;

        while (!opModeIsActive()) {
            zone = detector.findZone();

            dash.create("Zone:", zone);
            dash.create("Control:",  detector.getControl());
            dash.create("Top:",  detector.getTop());
            dash.create("Bottom:",  detector.getBottom());
            dash.update();

            if (isStopRequested()) {
                robot.camera.stop();
                break;
            }
        }

        waitForStart();

        timeoutClock.reset();
        robot.camera.stop();
        robot.tracker.reset();

        if(zone == A) A();
        else if(zone == B) B();
        else C();
    }

    public void A() {
        target.setPoint(13, 58, 45);
        firstWobble(false);
        powerShots(-6.5, -17.5, -28.5);
        secondWobble(2, 0, -17, 0);
        park(true);
    }
    public void B() {
        target.setPoint(3, 82, 0);
        firstWobble(true);
        powerShots(-9, -18.5, -28.5);
        secondWobble(2, 4, -17.5, 1);
        shootInGoal(1, 9, 67.5);
        park(false);
    }
    public void C() {
        target.setPoint(17, 107, 45).setDriveCorrectionSpeed(0.17);
        firstWobble(true);
        powerShots(-7.5, -18, -28.5);
        secondWobble(0, 0, -18, 4);
        shootInGoal(3, 8, 65);
        park(false);
    }

    public void firstWobble(boolean avoidStack) {
        MasqWayPoint strafe = new MasqWayPoint(7,30,0).setSwitchMode(TANK).setMinVelocity(0.8);

        robot.guard.setPosition(1);

        robot.hopper.setPosition(1);
        robot.claw.mid();
        if (avoidStack) robot.xyPath(strafe, target);
        else robot.xyPath(target);

        robot.claw.lower();
        robot.claw.open();
        robot.flicker.setPosition(0);
        sleep();
    }
    public void powerShots(double... x) {
        robot.compressor.setPosition(1);
        robot.claw.raise();

        robot.shooter.setPower(0.57);

        robot.xyPath(new MasqWayPoint(x[0],65, 0).setMinVelocity(0.25).setTimeout(6)
                .setDriveCorrectionSpeed(0.06).setAngularCorrectionSpeed(0.03)
                .setName("First Power Shot"));
        robot.turnAbsolute(0,1);
        robot.claw.close();
        sleep(250);
        flick();

        robot.xyPath(new MasqWayPoint(x[1],65, 0).setMinVelocity(0.25).setTimeout(4)
                .setDriveCorrectionSpeed(0.06).setAngularCorrectionSpeed(0.03)
                .setName("Second Power Shot"));
        robot.turnAbsolute(0,1);
        sleep(250);
        flick();

        robot.xyPath(new MasqWayPoint(x[2],66, 0).setMinVelocity(0.25).setTimeout(4)
                .setDriveCorrectionSpeed(0.06).setAngularCorrectionSpeed(0.03)
                .setName("Third Power Shot"));
        robot.turnAbsolute(0,1);
        sleep(250);
        flick();

        robot.shooter.setPower(0);
        robot.hopper.setPosition(0);
        robot.compressor.setPosition(0);
    }
    public void secondWobble(double yDecrease, double xDecrease, double wobbleX, int numRings) {
        robot.claw.mid();
        robot.claw.open();
        robot.guard.setPosition(0);

        robot.turnAbsolute(170,1);
        robot.xyPath(new MasqWayPoint(wobbleX, 27.5, 180).setMinVelocity(0).setTimeout(5)
                .setName("Second Wobble Goal").setDriveCorrectionSpeed(0.03)
                .setAngularCorrectionSpeed(0.02));

        robot.claw.lower();
        sleep();

        robot.claw.close();

        target.setSwitchMode(MECH).setAngularCorrectionSpeed(0.03).setY(target.getY() - yDecrease)
                .setX(target.getX() - xDecrease);

        sleep(1000);
        robot.claw.mid();

        MasqWayPoint curve = new MasqWayPoint(robot.tracker.getGlobalX(), robot.tracker.getGlobalY(),
                robot.tracker.getHeading());

        robot.guard.setPosition(1);

        if(numRings > 0) {
            MasqWayPoint starterStack = new MasqWayPoint(-3, 40, 0).setSwitchMode(TANK)
                    .setDriveCorrectionSpeed(0.04).setAngularCorrectionSpeed(0.02)
                    .setName("Starter Stack");

            robot.intake.setPower(1);

            turnController.setKp(0.02);

            if(numRings > 1) {
                robot.claw.raise();

                robot.xyPath(new MasqWayPoint(robot.tracker.getGlobalX(), 10, 0));
                starterStack.setDriveCorrectionSpeed(0.005).setMinVelocity(0.2).setY(58).setX(2)
                        .setOnComplete(() -> {
                            Thread thread = new Thread(() -> {
                                sleep(1000);
                                robot.claw.mid();
                            });
                            thread.start();
                        }).setSwitchMode(TANK);
                target.setOnComplete(() -> {
                    robot.intake.setPower(0);
                    robot.aligner.setPosition(1);
                    sleep();
                    robot.intake.setPower(0);
                    robot.aligner.setPosition(0);
                }).setMaxVelocity(0.7).setDriveCorrectionSpeed(0.11).setAngularCorrectionSpeed(0.02);
            }
            robot.turnAbsolute(30,0.5);
            robot.xyPath(starterStack, target);
        }
        else robot.xyPath(target);
        sleep(250);

        robot.claw.open();
        sleep(750);
        robot.claw.raise();
    }
    public void shootInGoal(int rings, double x, double y) {
        robot.intake.setPower(-1);
        robot.shooter.setPower(0.67);
        MasqWayPoint topGoal = new MasqWayPoint(x, y,0).setMinVelocity(0.2).setDriveCorrectionSpeed(0.04)
                .setTimeout(5).setAngularCorrectionSpeed(0.04);
        if (rings > 3) robot.xyPath(new MasqWayPoint(x - 10, y + 10,0).setSwitchMode(TANK)
                .setAngularCorrectionSpeed(0.04), topGoal);
        else robot.xyPath(topGoal);
        robot.aligner.setPosition(1);
        robot.intake.setPower(0);
        robot.hopper.setPosition(1);
        robot.claw.close();
        robot.claw.lower();
        turnController.setKp(0.04);
        robot.turnAbsolute(0, 2);
        robot.compressor.setPosition(1);
        sleep(750);
        robot.aligner.setPosition(0);
        for(int i = 0; i < rings; i++) {
            flick();
            sleep();
        }
        robot.shooter.setPower(0);

    }
    public void park(boolean backUp) {
        MasqWayPoint park = new MasqWayPoint(robot.tracker.getGlobalX(), 75, robot.tracker.getHeading())
                .setDriveCorrectionSpeed(0.025).setAngularCorrectionSpeed(0.03).setName("Park");
        MasqWayPoint exit = new MasqWayPoint(robot.tracker.getGlobalX() - 25, 50, robot.tracker.getHeading());

        robot.claw.lower();

        if(backUp) robot.xyPath(exit, park.setX(robot.tracker.getGlobalX() - 25));
        else robot.xyPath(park);

        dash.create("Time Left:", 30 - timeoutClock.seconds());
        dash.update();
        sleep((long) Range.clip(30e3 - timeoutClock.milliseconds(),0,30e3));
    }

    private void flick() {
        robot.flicker.setPosition(1);
        sleep();
        robot.flicker.setPosition(0);
        sleep();
    }
}