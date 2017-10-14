package Library4997.MasqWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Library4997.MasqRobot;

/**
 * Custom Linear opMode
 */

public abstract class MasqLinearOpMode extends LinearOpMode {
    protected DashBoard dash;
    public MasqRobot robot;
    protected MasqController controller1, controller2;
    public final void runOpMode() throws InterruptedException {
        try {
            robot = MasqRobot.getInstance(this);
            dash = new DashBoard(super.telemetry);
            dash.setNewFirst();
            controller1 = new MasqController(super.gamepad1, "controller1");
            controller2 = new MasqController(super.gamepad2, "controller2");
            run();
        } finally {
            stopLinearOpMode();
            controller1.close();
            controller2.close();
            dash.close();
        }
    }
    public abstract void run() throws InterruptedException;
    public void stopLinearOpMode() {}
}