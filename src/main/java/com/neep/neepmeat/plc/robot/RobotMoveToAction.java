package com.neep.neepmeat.plc.robot;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.RobotAction;
import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RobotMoveToAction implements RobotAction
{
    protected final BlockPos target;

    public RobotMoveToAction(BlockPos target)
    {
        this.target = target;
    }

    @Override
    public boolean finished(PLC plc)
    {
        return plc.getRobot().reachedTarget();
    }

    @Override
    public void start(PLC plc)
    {
        plc.getRobot().setTarget(target.up());
    }

    @Override
    public void tick(PLC plc)
    {
    }

    @Override
    public void end(PLC plc)
    {

    }
}
