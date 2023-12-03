package com.neep.neepmeat.plc.robot;

import com.neep.neepmeat.api.plc.robot.RobotAction;
import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RobotMoveToAction implements RobotAction
{
    private final SurgicalRobot robot;
    protected final BlockPos target;
    protected final Vec3d targetd;

    public RobotMoveToAction(SurgicalRobot robot, BlockPos target)
    {
        this.robot = robot;
        this.target = target;
        this.targetd = Vec3d.ofCenter(target);
    }

    @Override
    public boolean finished()
    {
        return robot.reachedTarget();
    }

    @Override
    public void start()
    {
        robot.setTarget(target.up());
    }

    @Override
    public void tick()
    {
    }
}
